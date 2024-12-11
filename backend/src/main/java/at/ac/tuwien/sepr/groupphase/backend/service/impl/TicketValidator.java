package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TicketValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketValidator.class);

    private final TicketRepository ticketRepository;

    public TicketValidator(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Validates the input data for a ticket.
     *
     * @param ticketCreateDto the data to validate
     * @throws ValidationException if the input data fails validation
     * @throws ConflictException if the ticket conflicts with an existing ticket
     */
    public void validateTicket(TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException {
        LOGGER.trace("Validating ticket: {}", ticketCreateDto);
        List<String> validationErrors = new ArrayList<>();

        if (ticketCreateDto.getPerformanceId() == null) {
            validationErrors.add("Performance ID is required");
        }

        if (ticketCreateDto.getTicketType() == null) {
            validationErrors.add("Ticket type is required");
        }

        if (ticketCreateDto.getSectorType() == null) {
            validationErrors.add("Sector type is required");
        }

        if (ticketCreateDto.getPriceCategory() == null) {
            validationErrors.add("Price category is required");
        }

        if (ticketCreateDto.getPrice() == null || ticketCreateDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            validationErrors.add("Price must be greater than 0");
        }

        if (ticketCreateDto.getStatus() == null || ticketCreateDto.getStatus().trim().isEmpty()) {
            validationErrors.add("Ticket status is required");
        }

        if (ticketCreateDto.getHall() == null) {
            validationErrors.add("Hall is required");
        }

        if (ticketCreateDto.getDate() == null) {
            validationErrors.add("Ticket date is required");
        } else if (ticketCreateDto.getDate().isBefore(LocalDate.now())) {
            validationErrors.add("Ticket date cannot be in the past");
        }

        if (ticketCreateDto.getTicketType() == TicketType.SEATED) {
            if (ticketCreateDto.getRowNumber() == null || ticketCreateDto.getRowNumber() <= 0) {
                validationErrors.add("Row number is required for seated tickets and must be greater than 0");
            }
            if (ticketCreateDto.getSeatNumber() == null || ticketCreateDto.getSeatNumber() <= 0) {
                validationErrors.add("Seat number is required for seated tickets and must be greater than 0");
            }
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Ticket validation failed with errors: {}", validationErrors);
            throw new ValidationException("Ticket validation failed", validationErrors);
        }

        //checkTicketUnique(ticketCreateDto.getPerformanceId(), ticketCreateDto.getRowNumber(), ticketCreateDto.getSeatNumber());
        LOGGER.info("Ticket validation passed for: {}", ticketCreateDto);
    }

    /**
     * Checks if a ticket for the same performance, row, and seat already exists.
     *
     * @param performanceId the performance ID
     * @param rowNumber the row number
     * @param seatNumber the seat number
     * @throws ConflictException if the ticket conflicts with an existing ticket
     */
    public void checkTicketUnique(Long performanceId, Integer rowNumber, Integer seatNumber) throws ConflictException {
        if (rowNumber != null && seatNumber != null && ticketRepository.existsByPerformanceIdAndRowNumberAndSeatNumber(performanceId, rowNumber, seatNumber)) {
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("A ticket already exists for performance ID " + performanceId
                + ", row " + rowNumber + ", and seat " + seatNumber);
            LOGGER.warn("Conflict detected for ticket: performanceId={}, rowNumber={}, seatNumber={}", performanceId, rowNumber, seatNumber);
            throw new ConflictException("Ticket creation conflict detected", conflictErrors);
        }
    }
}
