package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping(EventEndpoint.BASE_PATH)
public class EventEndpoint {

    public static final String BASE_PATH = "/api/v1/event";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService eventService;

    public EventEndpoint(EventService eventService) {
        this.eventService = eventService;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    public ResponseEntity<EventDetailDto> createOrUpdateEvent(@RequestBody EventCreateDto eventCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("Received request to create or update event: {}", eventCreateDto);
        EventDetailDto createdEvent = eventService.createEvent(eventCreateDto);
        LOGGER.debug("Event created/updated successfully: {}", createdEvent);
        return ResponseEntity.ok(createdEvent);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<EventDetailDto>> getAllEvents() {
        LOGGER.info("Fetching all events");
        List<EventDetailDto> events = eventService.getAllEvents();
        LOGGER.debug("Fetched {} events: {}", events.size(), events);
        return ResponseEntity.ok(events);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailDto> getEventById(@PathVariable Long id) {
        LOGGER.info("Fetching event with ID: {}", id);
        EventDetailDto event = eventService.getEventById(id);
        LOGGER.debug("Fetched event: {}", event);
        return ResponseEntity.ok(event);
    }

    @PermitAll
    @GetMapping("/search")
    public ResponseEntity<Stream<EventDetailDto>> search(EventSearchDto dto) {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("request parameters: {}", dto);
        Stream<EventDetailDto> result = eventService.search(dto);
        return ResponseEntity.ok(result);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        LOGGER.info("Deleting event with ID: {}", id);
        eventService.deleteEvent(id);
        LOGGER.debug("Event with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PermitAll
    @GetMapping("/artist/{id}")
    public ResponseEntity<List<EventDetailDto>> getEventsByArtistId(@PathVariable Long id) {
        LOGGER.info("Fetching events containing performances with artistId: {}", id);
        List<EventDetailDto> result = eventService.getEventsByArtistId(id);
        return ResponseEntity.ok(result);
    }
}