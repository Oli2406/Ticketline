package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.MerchandiseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class MerchandiseValidatorTest {

    private MerchandiseValidator merchandiseValidator;

    @Mock
    private MerchandiseRepository merchandiseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        merchandiseValidator = new MerchandiseValidator(merchandiseRepository);
    }

    private MerchandiseCreateDto createValidMerchandiseDto() {
        MerchandiseCreateDto dto = new MerchandiseCreateDto();
        dto.setName("Valid Merchandise");
        dto.setPrice(new BigDecimal("10.00"));
        dto.setCategory("Valid Category");
        dto.setStock(10);
        return dto;
    }

    @Test
    void validateCreate_validInput_noExceptionsThrown() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();

        when(merchandiseRepository.existsByName(dto.getName())).thenReturn(false);

        assertDoesNotThrow(() -> merchandiseValidator.validateCreate(dto));
        verify(merchandiseRepository, times(1)).existsByName(dto.getName());
    }

    @Test
    void validateCreate_emptyName_throwsValidationException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();
        dto.setName("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("Name is required"));
    }

    @Test
    void validateCreate_nameTooLong_throwsValidationException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();
        dto.setName("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("Name must be less than 255 characters"));
    }

    @Test
    void validateCreate_negativePrice_throwsValidationException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();
        dto.setPrice(new BigDecimal("-1.00"));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("Price must be greater than 0"));
    }

    @Test
    void validateCreate_emptyCategory_throwsValidationException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();
        dto.setCategory("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("Category is required"));
    }

    @Test
    void validateCreate_categoryTooLong_throwsValidationException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();
        dto.setCategory("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("Category must be less than 255 characters"));
    }

    @Test
    void validateCreate_negativeStock_throwsValidationException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();
        dto.setStock(-1);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("Stock must be greater than 0"));
    }

    @Test
    void validateCreate_nameAlreadyExists_throwsConflictException() {
        MerchandiseCreateDto dto = createValidMerchandiseDto();

        when(merchandiseRepository.existsByName(dto.getName())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            merchandiseValidator.validateCreate(dto)
        );

        assertTrue(exception.getErrors().contains("name is already registered"));
        verify(merchandiseRepository, times(1)).existsByName(dto.getName());
    }
}
