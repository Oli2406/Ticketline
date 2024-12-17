package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.AdminEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

class AdminEndpointTest {

    @Mock
    private AdminService adminService;

    @Mock
    private ResetPasswordService resetPasswordService;

    @InjectMocks
    private AdminEndpoint adminEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void unlockUser_Success() {
        Long userId = 1L;

        doNothing().when(adminService).unlockUser(userId);

        ResponseEntity<Void> response = adminEndpoint.unlockUser(userId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(adminService, times(1)).unlockUser(userId);
    }

    @Test
    void unlockUser_Failure() {
        Long userId = 1L;

        doThrow(new RuntimeException("Failed to unlock user"))
            .when(adminService).unlockUser(userId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminEndpoint.unlockUser(userId);
        });

        assertEquals("Failed to unlock user", exception.getMessage());
    }

    @Test
    void lockUser_Success() {
        Long userId = 1L;

        doNothing().when(adminService).lockUser(userId);

        ResponseEntity<Void> response = adminEndpoint.lockUser(userId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(adminService, times(1)).lockUser(userId);
    }

    @Test
    void lockUser_Failure() {
        Long userId = 1L;

        doThrow(new RuntimeException("Failed to lock user"))
            .when(adminService).lockUser(userId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminEndpoint.lockUser(userId);
        });

        assertEquals("Failed to lock user", exception.getMessage());
    }

    @Test
    void sendEmailToResetPassword_Success() {
        String email = "user@example.com";

        doAnswer(invocation -> null).when(resetPasswordService).sendEmailToResetPassword(email);

        ResponseEntity<Void> response = adminEndpoint.sendEmailToResetPassword(email);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(resetPasswordService, times(1)).sendEmailToResetPassword(email);
    }

    @Test
    void sendEmailToResetPassword_Failure() {
        String email = "user@example.com";

        doThrow(new RuntimeException("Failed to send email"))
            .when(resetPasswordService).sendEmailToResetPassword(email);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminEndpoint.sendEmailToResetPassword(email);
        });

        assertEquals("Failed to send email", exception.getMessage());
    }

    @Test
    void getAllUsers_Success() {
        List<UserDetailDto> users = new ArrayList<>();
        users.add(
            new UserDetailDto(1L, "Herta", "Testfrau", "herta.testfrau@email.com", false, false,
                123, true));
        users.add(
            new UserDetailDto(2L, "Max", "Testmann", "max.testmann@email.com", false, false, 1234,
                false));

        when(adminService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDetailDto>> response = adminEndpoint.getAllUsers();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(adminService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_EmptyList() {
        when(adminService.getAllUsers()).thenReturn(new ArrayList<>());

        ResponseEntity<List<UserDetailDto>> response = adminEndpoint.getAllUsers();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(adminService, times(1)).getAllUsers();
    }
}
