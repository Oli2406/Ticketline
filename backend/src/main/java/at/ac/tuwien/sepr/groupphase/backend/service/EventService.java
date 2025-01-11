package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSalesDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service interface for managing events.
 */
public interface EventService {

    /**
     * Creates or updates an event.
     *
     * @param eventCreateDto the data for creating or updating the event
     * @return the detailed representation of the created or updated event
     * @throws ValidationException if the input data fails validation (e.g., missing or invalid fields)
     * @throws ConflictException   if there are conflicts, such as an artist with the same name already existing
     */
    EventDetailDto createEvent(EventCreateDto eventCreateDto) throws ValidationException, ConflictException;

    /**
     * Retrieves all events.
     *
     * @return a list of detailed representations of all events
     */
    List<EventDetailDto> getAllEvents();

    /**
     * Retrieves a specific event by its ID.
     *
     * @param id the ID of the event to retrieve
     * @return the detailed representation of the requested event
     */
    EventDetailDto getEventById(Long id);

    /**
     * Deletes a specific event by its ID.
     *
     * @param id the ID of the event to delete
     */
    void deleteEvent(Long id);

    /**
     * Search for events in the persistent data store matching all provided fields.
     * The title and category are considered a match, if the search string is a substring of the field in event.
     *
     * @param dto the search parameters to use in filtering.
     * @return the event where the given fields match.
     */
    Stream<EventDetailDto> search(EventSearchDto dto);

    /**
     * Retrieves all events that contain at least one performance with artist {@code id}.
     *
     * @param id artist id to look for
     * @return a list of all events found
     */
    List<EventDetailDto> getEventsByArtistId(Long id);

    /**
     * Retrieves the top 10 events based on the percentage of tickets sold.
     *
     * @return a list of sales information of the top 10 events
     */
    List<EventSalesDto> getTop10Events(Integer year, Integer month, String category);

    /**
     * Retrieves a list of all unique categories from the events table.
     *
     * @return a list of distinct category names as strings
     */
    List<String> getAllCategories();
}
