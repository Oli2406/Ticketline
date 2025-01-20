package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.PerformanceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


class PerformanceValidatorTest {

    private PerformanceValidator performanceValidator;

    @Mock
    private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        performanceValidator = new PerformanceValidator(performanceRepository);
    }

    private PerformanceCreateDto createValidPerformanceDto() {
        PerformanceCreateDto dto = new PerformanceCreateDto();
        dto.setName("Valid Performance");
        dto.setArtistId(1L);
        dto.setLocationId(1L);
        dto.setDate(LocalDateTime.now().plusDays(1));
        dto.setPrice(new BigDecimal("100.00"));
        dto.setTicketNumber(50L);
        dto.setHall("Main Hall");
        dto.setDuration(120);
        return dto;
    }

    @Test
    void validatePerformance_validInput_noExceptionsThrown() {
        PerformanceCreateDto dto = createValidPerformanceDto();

        when(performanceRepository.findByLocationIdAndHall(dto.getLocationId(), dto.getHall()))
            .thenReturn(List.of());
        when(performanceRepository.existsByNameAndLocationIdAndDate(dto.getName(), dto.getLocationId(), dto.getDate()))
            .thenReturn(false);

        assertDoesNotThrow(() -> performanceValidator.validatePerformance(dto));
    }

    @Test
    void validatePerformance_missingName_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setName("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Performance name is required"));
    }

    @Test
    void validatePerformance_nameTooLong_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setName("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Performance name must be less than 255 characters"));
    }

    @Test
    void validatePerformance_nullArtistId_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setArtistId(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Artist id is required"));
    }

    @Test
    void validatePerformance_dateInPast_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setDate(LocalDateTime.now().minusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Performance date cannot be in the past"));
    }

    @Test
    void validatePerformance_priceOutOfRange_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setPrice(new BigDecimal("600"));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Price must not exceed 500"));
    }

    @Test
    void validatePerformance_priceNegative_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setPrice(new BigDecimal("-10.00"));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Price must be greater than 0"));
    }

    @Test
    void validatePerformance_ticketNumberZero_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setTicketNumber(0L);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Number of tickets must be greater than 0"));
    }

    @Test
    void validatePerformance_durationExceedsLimit_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setDuration(601);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Event duration must be less than 600 minutes or 10 hours"));
    }

    @Test
    void validatePerformance_hallConflict_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();

        Performance existingPerformance = new Performance();
        existingPerformance.setDate(LocalDateTime.now().plusDays(1));
        existingPerformance.setDuration(120);
        when(performanceRepository.findByLocationIdAndHall(dto.getLocationId(), dto.getHall()))
            .thenReturn(List.of(existingPerformance));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        List<String> errors = exception.errors();
        System.out.println(errors);

        assertTrue(errors.stream().anyMatch(error -> error.contains("A performance already exists in hall")));
    }

    @Test
    void validatePerformance_nameConflict_throwsConflictException() {
        PerformanceCreateDto dto = createValidPerformanceDto();

        when(performanceRepository.existsByNameAndLocationIdAndDate(dto.getName(), dto.getLocationId(), dto.getDate()))
            .thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        assertTrue(exception.getErrors().contains("Performance with the name 'Valid Performance' already exists at this location on the given date"));
    }

    @Test
    void validatePerformance_allEmptyFields_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();

        dto.setLocationId(null);
        dto.setDate(null);
        dto.setPrice(null);
        dto.setTicketNumber(null);
        dto.setHall(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        List<String> errors = exception.errors();

        assertTrue(errors.stream().anyMatch(error -> error.contains("Location id is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Performance date is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Price must not be null")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Number of tickets must be greater than 0")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Hall is required")));
    }

    @Test
    void validatePerformance_negativeDuration_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setDuration(-1);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        List<String> errors = exception.errors();
        assertTrue(errors.stream().anyMatch(error -> error.contains("Event duration must be greater than 0 minutes")));
    }

    @Test
    void validatePerformance_hallNameTooLong_throwsValidationException() {
        PerformanceCreateDto dto = createValidPerformanceDto();
        dto.setHall("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            performanceValidator.validatePerformance(dto)
        );

        List<String> errors = exception.errors();
        assertTrue(errors.stream().anyMatch(error -> error.contains("Performance hall must be less than 50 characters")));
    }
}
