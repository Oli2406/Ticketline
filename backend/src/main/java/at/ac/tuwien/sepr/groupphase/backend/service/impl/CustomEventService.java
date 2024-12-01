package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomEventService implements EventService {

    private final EventRepository eventRepository;

    public CustomEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventDetailDto createOrUpdateEvent(EventCreateDto eventCreateDto) {
        Event event = new Event(
            eventCreateDto.getTitle(),
            eventCreateDto.getDescription(),
            eventCreateDto.getDateOfEvent(),
            eventCreateDto.getCategory(),
            eventCreateDto.getDuration(),
            eventCreateDto.getPerformanceIds()
        );
        event = eventRepository.save(event);
        return new EventDetailDto(event.getId(), event.getTitle(), event.getDescription(), event.getCategory(), event.getDateOfEvent(), event.getDuration(), event.getPerformanceIds());
    }

    @Override
    public List<EventDetailDto> getAllEvents() {
        return eventRepository.findAll().stream()
            .map(event -> new EventDetailDto(event.getId(), event.getTitle(), event.getDescription(), event.getCategory(), event.getDateOfEvent(), event.getDuration(), event.getPerformanceIds()))
            .collect(Collectors.toList());
    }

    @Override
    public EventDetailDto getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return new EventDetailDto(event.getId(), event.getTitle(), event.getDescription(), event.getCategory(), event.getDateOfEvent(), event.getDuration(), event.getPerformanceIds());
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
