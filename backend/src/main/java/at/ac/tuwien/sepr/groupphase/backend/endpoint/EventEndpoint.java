package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
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

@RestController
@RequestMapping("/api/v1/event")
public class EventEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventService eventService;

    public EventEndpoint(EventService eventService) {
        this.eventService = eventService;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    public ResponseEntity<EventDetailDto> createOrUpdateEvent(@RequestBody EventCreateDto eventCreateDto) throws ValidationException, ConflictException {
        logger.info("Received request to create or update event: {}", eventCreateDto);
        EventDetailDto createdEvent = eventService.createOrUpdateEvent(eventCreateDto);
        logger.debug("Event created/updated successfully: {}", createdEvent);
        return ResponseEntity.ok(createdEvent);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<EventDetailDto>> getAllEvents() {
        logger.info("Fetching all events");
        List<EventDetailDto> events = eventService.getAllEvents();
        logger.debug("Fetched {} events: {}", events.size(), events);
        return ResponseEntity.ok(events);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailDto> getEventById(@PathVariable Long id) {
        logger.info("Fetching event with ID: {}", id);
        EventDetailDto event = eventService.getEventById(id);
        logger.debug("Fetched event: {}", event);
        return ResponseEntity.ok(event);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        logger.info("Deleting event with ID: {}", id);
        eventService.deleteEvent(id);
        logger.debug("Event with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}