package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.TicketValidator;
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


class TicketValidatorTest {

    private TicketValidator ticketValidator;

    @Mock
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketValidator = new TicketValidator(ticketRepository);
    }

    private TicketCreateDto createValidTicketDto() {
        TicketCreateDto dto = new TicketCreateDto();
        dto.setPerformanceId(1L);
        dto.setTicketType(TicketType.SEATED);
        dto.setSectorType(SectorType.C);
        dto.setPriceCategory(PriceCategory.VIP);
        dto.setPrice(new BigDecimal("50.00"));
        dto.setStatus("AVAILABLE");
        dto.setHall(Hall.A);
        dto.setDate(LocalDateTime.now().plusDays(1));
        dto.setRowNumber(5);
        dto.setSeatNumber(10);
        return dto;
    }

    @Test
    void validateTicket_validInput_noExceptionsThrown() {
        TicketCreateDto dto = createValidTicketDto();

        assertDoesNotThrow(() -> ticketValidator.validateTicket(dto));
    }

    @Test
    void validateTicket_nullPerformanceId_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setPerformanceId(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Performance ID is required"));
    }

    @Test
    void validateTicket_nullTicketType_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setTicketType(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Ticket type is required"));
    }

    @Test
    void validateTicket_nullPrice_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setPrice(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Price must be greater than 0"));
    }

    @Test
    void validateTicket_negativePrice_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setPrice(new BigDecimal("-10.00"));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Price must be greater than 0"));
    }

    @Test
    void validateTicket_invalidStatus_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setStatus("INVALID");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Invalid status value: INVALID"));
    }

    @Test
    void validateTicket_nullHall_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setHall(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Hall is required"));
    }

    @Test
    void validateTicket_dateInPast_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setDate(LocalDateTime.now().minusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        assertTrue(exception.getErrors().contains("Ticket date cannot be in the past"));
    }

    @Test
    void validateTicket_seatedWithoutRowOrSeat_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();
        dto.setRowNumber(null);
        dto.setSeatNumber(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        List<String> errors = exception.errors();

        assertTrue(errors.stream().anyMatch(error -> error.contains("Row number is required for seated tickets and must be greater than 0")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Seat number is required for seated tickets and must be greater than 0")));
    }


    @Test
    void validateTicket_conflictingTicket_throwsConflictException() {
        TicketCreateDto dto = createValidTicketDto();

        when(ticketRepository.existsByPerformanceIdAndRowNumberAndSeatNumber(
            dto.getPerformanceId(), dto.getRowNumber(), dto.getSeatNumber()))
            .thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            ticketValidator.checkTicketUnique(dto.getPerformanceId(), dto.getRowNumber(), dto.getSeatNumber())
        );

        assertTrue(exception.getErrors().contains(
            "A ticket already exists for performance ID 1, row 5, and seat 10"
        ));
    }

    @Test
    void validateTicket_allEmptyFields_throwsValidationException() {
        TicketCreateDto dto = createValidTicketDto();

        dto.setSectorType(null);
        dto.setPriceCategory(null);
        dto.setStatus(null);
        dto.setDate(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            ticketValidator.validateTicket(dto)
        );

        List<String> errors = exception.errors();

        assertTrue(errors.stream().anyMatch(error -> error.contains("Sector type is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Price category is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Ticket status is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Ticket date is required")));
    }
}
