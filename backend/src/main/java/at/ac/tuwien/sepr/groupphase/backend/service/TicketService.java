package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service interface for managing tickets.
 */
public interface TicketService {

    /**
     * Creates or updates a ticket.
     *
     * @param ticketCreateDto the data for creating or updating the ticket
     * @return the detailed representation of the created or updated ticket
     * @throws ValidationException if the input data fails validation (e.g., missing or invalid fields)
     * @throws ConflictException if there are conflicts, such as the ticket already existing for the same seat and performance
     */
    TicketDetailDto createTicket(TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException;

    /**
     * Retrieves all tickets.
     *
     * @return a list of detailed representations of all tickets
     */
    List<TicketDetailDto> getAllTickets();

    /**
     * Retrieves a specific ticket by its ID.
     *
     * @param id the ID of the ticket to retrieve
     * @return the detailed representation of the requested ticket
     */
    TicketDetailDto getTicketById(Long id);

    /**
     * Retrieves all tickets for a specific performance by its ID.
     *
     * @param performanceId the ID of the performance whose tickets are to be retrieved
     * @return a list of detailed representations of the tickets associated with the performance
     */
    List<TicketDetailDto> getTicketsByPerformanceId(Long performanceId);

    /**
     * Deletes a specific ticket by its ID.
     *
     * @param id the ID of the ticket to delete
     */
    void deleteTicket(Long id);
}
