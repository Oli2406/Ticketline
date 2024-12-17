package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service interface for managing performances.
 */
public interface PerformanceService {

    /**
     * Creates or updates a performance.
     *
     * @param performanceCreateDto the data for creating or updating the performance
     * @return the detailed representation of the created or updated performance
     * @throws ValidationException if the input data fails validation (e.g., missing or invalid fields)
     * @throws ConflictException   if there are conflicts, such as an artist with the same name already existing
     */
    PerformanceDetailDto createPerformance(PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException;

    /**
     * Retrieves all performances.
     *
     * @return a list of detailed representations of all performances
     */
    List<PerformanceDetailDto> getAllPerformances();

    /**
     * Retrieves a specific performance by its ID.
     *
     * @param id the ID of the performance to retrieve
     * @return the detailed representation of the requested performance
     */
    PerformanceDetailDto getPerformanceById(Long id);

    /**
     * Deletes a specific performance by its ID.
     *
     * @param id the ID of the performance to delete
     */
    void deletePerformance(Long id);


    List<PerformanceDetailDto> performAdvancedSearch(String query);

    /**
     * Search for performances in the persistent data store matching all provided fields.
     * The hall is considered a match, if the search string is a substring of the field in performances.
     *
     * @param dto the search parameters to use in filtering.
     * @return the performances where the given fields match.
     */
    Stream<PerformanceDetailDto> search(PerformanceSearchDto dto);

    /**
     * Retrieves all performances that are mapped to event with id {@code id}.
     *
     * @param id the ID of the event to get the performances from
     * @return the performances mapped to the event
     */
    List<PerformanceDetailDto> getByEventId(Long id);

    /**
     * Retrieves all performances that are mapped to location with id {@code id}.
     *
     * @param id the ID of the location to get the performances from
     * @return the performances mapped to the location
     */
    List<PerformanceDetailDto> getByLocationId(Long id);

    /**
     * Updates the ticket number for a performance by its ID.
     *
     * @param id the ID of the performance to update
     * @param ticketNumber the new ticket number
     * @return the updated performance details
     * @throws NotFoundException if the performance with the specified ID does not exist
     */
    PerformanceDetailDto updateTicketNumberById(Long id, Long ticketNumber) throws NotFoundException;


}
