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
     * Updates an existing ticket with the provided details.
     *
     * @param ticketId The ID of the ticket to update.
     * @param ticketCreateDto The new ticket details.
     * @return The updated ticket details.
     */
    TicketDetailDto updateTicket(Long ticketId, TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException;

    /**
     * Deletes a specific ticket by its ID.
     *
     * @param id the ID of the ticket to delete
     */
    void deleteTicket(Long id);

    /**
     * Updates the status of a list of tickets identified by their IDs.
     *
     * @param ticketIds the list of ticket IDs whose status needs to be updated
     * @param status the new status to be applied to the tickets
     */
    void updateTicketStatusList(List<Long> ticketIds, String status);
}
