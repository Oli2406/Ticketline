package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service interface for handling reservations.
 */
public interface ReservedService {

    /**
     * Retrieves the details of a reservation by its ID.
     *
     * @param reservationId the ID of the reservation to fetch
     * @return the details of the reservation
     */
    ReservedDetailDto getReservedById(Long reservationId);

    /**
     * Retrieves a list of reservations for a specific user by their user ID.
     *
     * @param userId the ID of the user
     * @return a list of reservation details for the given user
     */
    List<ReservedDetailDto> getReservationsByUserId(Long userId);

    /**
     * Creates a new reservation or updates an existing one based on the provided data.
     *
     * @param reservedCreateDto the data for creating or updating a reservation
     * @return the details of the created or updated reservation
     * @throws ValidationException if the input data is invalid
     */
    ReservedDetailDto createReservation(ReservedCreateDto reservedCreateDto) throws ValidationException;

    /**
     * Updates an existing one based on the provided data.
     *
     * @param reservedDetailDto the data forupdating a reservation
     */
    void updateReservation(ReservedDetailDto reservedDetailDto);

    /**
     * Deletes a specific ticket from an existing reservation.
     *
     * @param reservationId the ID of the reservation from which the ticket should be deleted
     * @param ticketId      the ID of the ticket to delete
     */
    void deleteTicketFromReservation(Long reservationId, Long ticketId);

    /**
     * Retrieves a list of detailed reservations for a specific user by their user ID.
     *
     * @param userId the ID of the user
     * @return a list of reservation details for the given user
     */
    List<ReservationOverviewDto> getReservationDetailsByUser(Long userId);
}

