package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.MerchandiseEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MerchandiseEndpointTest {

    private MerchandiseDetailDto merchandise1;
    private MerchandiseDetailDto merchandise2;
    private List<MerchandiseDetailDto> mockMerchandises;

    @Mock
    private MerchandiseService merchandiseService;

    @InjectMocks
    private MerchandiseEndpoint merchandiseEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        merchandise1 = new MerchandiseDetailDto();
        merchandise1.setName("T-Shirt");
        merchandise1.setPrice(BigDecimal.valueOf(20.00));
        merchandise1.setCategory("Clothing");
        merchandise1.setStock(10);
        merchandise1.setPoints(200);
        merchandise1.setImageUrl("image1.jpg");

        merchandise2 = new MerchandiseDetailDto();
        merchandise2.setName("Mug");
        merchandise2.setPrice(BigDecimal.valueOf(10.00));
        merchandise2.setCategory("Accessories");
        merchandise2.setStock(15);
        merchandise2.setPoints(100);
        merchandise2.setImageUrl("image2.jpg");

        mockMerchandises = List.of(merchandise1, merchandise2);
    }

    @Test
    void createMerchandiseWhenValidInputReturnsSuccess() throws ValidationException, ConflictException {
        MerchandiseCreateDto createDto = new MerchandiseCreateDto();
        createDto.setName("T-Shirt");
        createDto.setCategory("Clothing");
        createDto.setPrice(BigDecimal.valueOf(20.00));
        createDto.setStock(10);

        MockMultipartFile imageFile = new MockMultipartFile("image", "tshirt.jpg", "image/jpeg", "mockImageData".getBytes());

        when(merchandiseService.createMerchandise(any(MerchandiseCreateDto.class))).thenReturn(createDto);

        ResponseEntity<?> response = merchandiseEndpoint.createMerchandise(createDto, imageFile);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createDto, response.getBody());
    }

    @Test
    void createMerchandiseWhenInvalidInputThrowsValidationException() throws ValidationException, ConflictException {
        MerchandiseCreateDto createDto = new MerchandiseCreateDto();
        createDto.setName("T-Shirt");
        createDto.setCategory("Clothing");
        createDto.setPrice(BigDecimal.valueOf(20.00));
        createDto.setStock(10);

        List<String> validationErrors = List.of("Name is required", "Price must be greater than zero");

        doThrow(new ValidationException("Invalid input", validationErrors))
            .when(merchandiseService).createMerchandise(any(MerchandiseCreateDto.class));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseEndpoint.createMerchandise(createDto, null)
        );

        assertTrue(exception.getMessage().contains("Invalid input"));
        assertTrue(exception.getMessage().contains("Name is required"));
        assertTrue(exception.getMessage().contains("Price must be greater than zero"));

        if (exception.errors() != null) {
            List<String> actualErrors = exception.errors();
            assertTrue(actualErrors.contains("Name is required"));
            assertTrue(actualErrors.contains("Price must be greater than zero"));
        } else if (exception.getErrors() != null) {
            String actualError = exception.getErrors();
            assertTrue(actualError.contains("Name is required"));
            assertTrue(actualError.contains("Price must be greater than zero"));
        } else {
            fail("Unexpected type for errors: " + exception.getErrors().getClass().getName());
        }
    }


    @Test
    void createMerchandiseWhenDuplicateThrowsConflictException() throws ValidationException, ConflictException {
        MerchandiseCreateDto createDto = new MerchandiseCreateDto();
        createDto.setName("T-Shirt");
        createDto.setCategory("Clothing");
        createDto.setPrice(BigDecimal.valueOf(20.00));
        createDto.setStock(10);

        List<String> conflictErrors = List.of("Merchandise with the same name already exists");

        doThrow(new ConflictException("Duplicate merchandise", conflictErrors))
            .when(merchandiseService).createMerchandise(any(MerchandiseCreateDto.class));

        ConflictException exception = assertThrows(ConflictException.class, () ->
            merchandiseEndpoint.createMerchandise(createDto, null)
        );

        assertTrue(exception.getMessage().contains("Duplicate merchandise"));
        assertTrue(exception.getMessage().contains("Merchandise with the same name already exists"));

        if (exception.errors() != null) {
            List<String> actualErrors = exception.errors();
            assertTrue(actualErrors.contains("Merchandise with the same name already exists"));
        } else if (exception.getErrors() != null) {
            String actualError = (String) exception.getErrors();
            assertTrue(actualError.contains("Merchandise with the same name already exists"));
        } else {
            fail("Unexpected type for errors: " + exception.getErrors().getClass().getName());
        }
    }

    @Test
    void getAllMerchandiseReturnsAllMerchandise() {
        when(merchandiseService.getAllMerchandise()).thenReturn(mockMerchandises);

        ResponseEntity<List<MerchandiseDetailDto>> response = ResponseEntity.ok(merchandiseEndpoint.getAllMerchandise());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockMerchandises, response.getBody());
    }
}
