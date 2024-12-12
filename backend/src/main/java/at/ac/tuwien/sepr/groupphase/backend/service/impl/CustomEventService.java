package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomEventService implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;

    public CustomEventService(EventRepository eventRepository, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
    }

    @Override
    public EventDetailDto createEvent(EventCreateDto eventCreateDto) throws ValidationException, ConflictException {
        logger.info("Creating or updating event: {}", eventCreateDto);
        eventValidator.validateEvent(eventCreateDto);
        Event event = new Event(
            eventCreateDto.getTitle(),
            eventCreateDto.getDescription(),
            eventCreateDto.getDateFrom(),
            eventCreateDto.getDateTo(),
            eventCreateDto.getCategory(),
            eventCreateDto.getPerformanceIds()
        );
        event = eventRepository.save(event);
        logger.debug("Saved event to database: {}", event);
        return new EventDetailDto(event.getEventId(), event.getTitle(), event.getDescription(), event.getCategory(), event.getDateFrom(), event.getDateTo());
    }

    @Override
    public List<EventDetailDto> getAllEvents() {
        logger.info("Fetching all events");
        List<EventDetailDto> events = eventRepository.findAll().stream()
            .map(event -> new EventDetailDto(event.getEventId(), event.getTitle(), event.getDescription(), event.getCategory(), event.getDateFrom(), event.getDateTo()))
            .collect(Collectors.toList());
        logger.debug("Fetched {} events: {}", events.size(), events);
        return events;
    }

    @Override
    public EventDetailDto getEventById(Long id) {
        logger.info("Fetching event with ID: {}", id);
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        logger.debug("Fetched event: {}", event);
        return new EventDetailDto(event.getEventId(), event.getTitle(), event.getDescription(), event.getCategory(), event.getDateFrom(), event.getDateTo());
    }

    @Override
    public void deleteEvent(Long id) {
        logger.info("Deleting event with ID: {}", id);
        eventRepository.deleteById(id);
        logger.debug("Deleted event with ID: {}", id);
    }
}
