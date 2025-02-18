package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSalesDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.TopEvent;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TopEventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;

import at.ac.tuwien.sepr.groupphase.backend.service.validators.EventValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final TopEventRepository topEventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, TopEventRepository topEventRepository, EventValidator eventValidator,
                            EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.topEventRepository = topEventRepository;
        this.eventValidator = eventValidator;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDetailDto createEvent(EventCreateDto eventCreateDto)
        throws ValidationException, ConflictException {
        LOGGER.info("Creating or updating event: {}", eventCreateDto);
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
        LOGGER.debug("Saved event to database: {}", event);
        return new EventDetailDto(event.getEventId(), event.getTitle(), event.getDescription(),
            event.getCategory(), event.getDateFrom(), event.getDateTo());
    }

    @Override
    public List<EventDetailDto> getAllEvents() {
        LOGGER.info("Fetching all events");
        List<EventDetailDto> events = eventRepository.findAll().stream()
            .map(event -> new EventDetailDto(event.getEventId(), event.getTitle(),
                event.getDescription(), event.getCategory(), event.getDateFrom(),
                event.getDateTo()))
            .collect(Collectors.toList());
        LOGGER.debug("Fetched {} events: {}", events.size(), events);
        return events;
    }

    @Override
    public EventDetailDto getEventById(Long id) {
        LOGGER.info("Fetching event with ID: {}", id);
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        LOGGER.debug("Fetched event: {}", event);
        return new EventDetailDto(event.getEventId(), event.getTitle(), event.getDescription(),
            event.getCategory(), event.getDateFrom(), event.getDateTo());
    }

    @Override
    public void deleteEvent(Long id) {
        LOGGER.info("Deleting event with ID: {}", id);
        eventRepository.deleteById(id);
        LOGGER.debug("Deleted event with ID: {}", id);
    }

    @Override
    public Stream<EventDetailDto> search(EventSearchDto dto) {
        LOGGER.info("Searching events with data: {}", dto);
        var query = eventRepository.findAll().stream();
        if (dto.getTitle() != null) {
            query = query.filter(
                event -> event.getTitle().toLowerCase().contains(dto.getTitle().toLowerCase()));
        }
        if (dto.getCategory() != null) {
            query = query.filter(event -> event.getCategory().toLowerCase()
                .contains(dto.getCategory().toLowerCase()));
        }
        if (dto.getDateEarliest() != null) {
            query = query.filter(event -> event.getDateFrom().isAfter(dto.getDateEarliest()));
        }
        if (dto.getDateLatest() != null) {
            query = query.filter(event -> event.getDateTo().isBefore(dto.getDateLatest()));
        }

        return query.map(this.eventMapper::eventToEventDetailDto);
    }

    @Override
    public List<EventDetailDto> getEventsByArtistId(Long id) {
        LOGGER.info("Getting events with artistId: {}", id);
        return eventRepository.findEventsByArtistId(id)
            .stream()
            .map(this.eventMapper::eventToEventDetailDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<EventSalesDto> getTop10Events(Integer year, Integer month, String category) {
        LOGGER.info("Getting top ten events with category {} in {} {}", category, month, year);
        List<TopEvent> topEvents = topEventRepository.findByCategoryMonthYear(category, month, year);

        return topEvents.stream()
            .map(event -> new EventSalesDto(
                event.getEventId(),
                event.getEventTitle(),
                event.getSoldTickets()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        LOGGER.info("Getting all categories");
        return eventRepository.findAllCategories();
    }
}
