package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.InsufficientStockException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserEndpointTest {

    @Mock
    private UserService userService;

    @Mock
    private MerchandiseService merchandiseService;

    @Mock
    private RandomStringGenerator randomStringGenerator;

    @InjectMocks
    private UserEndpoint userEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateReadNewsSuccessfully() {
        UserUpdateReadNewsDto dto = new UserUpdateReadNewsDto();
        dto.setNewsId(1L);

        doNothing().when(userService).updateReadNews(dto);

        ResponseEntity<Void> response = userEndpoint.updateReadNews(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).updateReadNews(dto);
    }

    @Test
    void getUserPointsSuccessfully() {
        ApplicationUser user = new ApplicationUser();
        user.setPoints(100);

        when(userService.findApplicationUserByEmail(anyString())).thenReturn(user);

        ResponseEntity<Integer> response = userEndpoint.getUserPoints("user@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody());
        verify(userService, times(1)).findApplicationUserByEmail("user@example.com");
    }

    @Test
    void getUserPointsNotFound() {
        when(userService.findApplicationUserByEmail(anyString())).thenReturn(null);

        ResponseEntity<Integer> response = userEndpoint.getUserPoints("nonexistent@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, response.getBody());
    }

    @Test
    void deductPointsSuccessfully() throws Exception {
        when(userService.updateUserPoints(anyString(), anyInt()))
            .thenReturn("Points updated successfully!");

        ResponseEntity<?> response = userEndpoint.deductPoints("encryptedId", 50);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).updateUserPoints("encryptedId", 50);
    }



    @Test
    void deductPointsUnexpectedError() throws Exception {
        doThrow(new RuntimeException("Unexpected error")).when(userService).updateUserPoints(anyString(), anyInt());

        ResponseEntity<?> response = userEndpoint.deductPoints("encryptedId", 50);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("unexpected error", response.getBody());
    }

    @Test
    void addPointsSuccessfully() throws Exception {
        doNothing().when(userService).addUserPoints(anyString(), anyInt());

        ResponseEntity<?> response = userEndpoint.addPoints("encryptedId", 50);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).addUserPoints("encryptedId", 50);
    }

    @Test
    void addPointsUnexpectedError() throws Exception {
        doThrow(new RuntimeException("Unexpected error")).when(userService).addUserPoints(anyString(), anyInt());

        ResponseEntity<?> response = userEndpoint.addPoints("encryptedId", 50);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("unexpected error", response.getBody());
    }

    @Test
    void purchaseItemsSuccessfully() {
        List<PurchaseItemDto> purchaseItems = List.of(new PurchaseItemDto());

        doNothing().when(merchandiseService).processPurchase(purchaseItems);

        ResponseEntity<?> response = userEndpoint.purchaseItems(purchaseItems);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("message", "Purchase successful, stock updated"), response.getBody());
        verify(merchandiseService, times(1)).processPurchase(purchaseItems);
    }

    @Test
    void purchaseItemsWithEmptyListReturnsBadRequest() {
        ResponseEntity<?> response = userEndpoint.purchaseItems(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("error", "Purchase items cannot be empty"), response.getBody());
    }


    @Test
    void purchaseItemsWithInsufficientStockReturnsException() {
        List<PurchaseItemDto> purchaseItems = List.of(new PurchaseItemDto());

        doThrow(new RuntimeException("Insufficient stock")).when(merchandiseService).processPurchase(purchaseItems);

        ResponseEntity<?> response = userEndpoint.purchaseItems(purchaseItems);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("An unexpected error occurred"));
    }

    @Test
    void purchaseItemsWithInsufficientStockReturnsConflict() {
        List<PurchaseItemDto> purchaseItems = List.of(new PurchaseItemDto());

        doThrow(new InsufficientStockException("Insufficient stock")).when(merchandiseService).processPurchase(purchaseItems);

        ResponseEntity<?> response = userEndpoint.purchaseItems(purchaseItems);

        System.out.println(response);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Insufficient stock"));
    }

    @Test
    void updateUserSuccessfully() throws ValidationException, ConflictException {
        UserUpdateDto userUpdateDto = new UserUpdateDto();

        when(userService.updateUser(userUpdateDto)).thenReturn("User updated");

        String response = userEndpoint.updateUser(userUpdateDto);

        assertEquals("User updated", response);
        verify(userService, times(1)).updateUser(userUpdateDto);
    }

    @Test
    void deleteUserSuccessfully() throws ValidationException {
        DeleteUserDto deleteUserDto = new DeleteUserDto();

        doNothing().when(userService).deleteUser(deleteUserDto);

        assertDoesNotThrow(() -> userEndpoint.deleteUser(deleteUserDto));
        verify(userService, times(1)).deleteUser(deleteUserDto);
    }

    @Test
    void getUserSuccessfully() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        when(userService.getUser(anyString())).thenReturn(userUpdateDto);

        ResponseEntity<UserUpdateDto> response = userEndpoint.getUser("123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userUpdateDto, response.getBody());
        verify(userService, times(1)).getUser("123");
    }
}
