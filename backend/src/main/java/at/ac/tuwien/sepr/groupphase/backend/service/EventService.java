package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;

import java.util.List;

/**
 * Service interface for managing events.
 */
public interface EventService {

    /**
     * Creates or updates an event.
     *
     * @param eventCreateDto the data for creating or updating the event
     * @return the detailed representation of the created or updated event
     */
    EventDetailDto createOrUpdateEvent(EventCreateDto eventCreateDto);

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
}
