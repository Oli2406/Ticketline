package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomEventService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.EventValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomEventServiceTest {

    private CustomEventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventMapper eventMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventService = new CustomEventService(eventRepository, eventValidator, eventMapper);
    }

    @Test
    void createOrUpdateEvent_ShouldSaveEvent_WhenValidInput() throws ValidationException, ConflictException {
        EventCreateDto dto = new EventCreateDto("Title", "Description", "Category", LocalDate.now(), LocalDate.now().plusDays(1), List.of(1L));

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            e.setEventId(1L);
            return e;
        });

        EventDetailDto created = eventService.createEvent(dto);

        assertNotNull(created, "Created event DTO should not be null");
        assertAll(
            () -> assertNotNull(created.getEventId(), "Event ID should not be null"),
            () -> assertEquals(dto.getTitle(), created.getTitle(), "Title should match"),
            () -> assertEquals(dto.getDescription(), created.getDescription(), "Description should match"),
            () -> assertEquals(dto.getCategory(), created.getCategory(), "Category should match")
        );

        verify(eventValidator, times(1)).validateEvent(dto);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void getAllEvents_ShouldReturnEventList() {
        List<Event> events = List.of(new Event("Title1", "Description1", LocalDate.now(), LocalDate.now().plusDays(1), "Category1", List.of(1L)));
        when(eventRepository.findAll()).thenReturn(events);

        List<EventDetailDto> result = eventService.getAllEvents();

        assertFalse(result.isEmpty(), "Resulting event list should not be empty");
        assertEquals(1, result.size(), "Result list size should match");
        assertEquals("Title1", result.getFirst().getTitle(), "Title of the first event should match");

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void getEventByIdShouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> eventService.getEventById(1L),
            "Should throw exception for non-existent ID");

        assertEquals("Event not found", exception.getMessage(), "Exception message should match");
        verify(eventRepository, times(1)).findById(1L);
    }
}
