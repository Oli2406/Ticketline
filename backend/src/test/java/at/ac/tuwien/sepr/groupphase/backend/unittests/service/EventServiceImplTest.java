package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSalesDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TopEventsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.EventServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.EventValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceImplTest {

    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TopEventsRepository topEventsRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventMapper eventMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventServiceImpl(eventRepository, topEventsRepository, eventValidator, eventMapper);
    }

    @Test
    void createOrUpdateEvent_ShouldSaveEvent_WhenValidInput()
        throws ValidationException, ConflictException {
        EventCreateDto dto = new EventCreateDto("Title", "Description", "Category", LocalDate.now(),
            LocalDate.now().plusDays(1), List.of(1L));

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
            () -> assertEquals(dto.getDescription(), created.getDescription(),
                "Description should match"),
            () -> assertEquals(dto.getCategory(), created.getCategory(), "Category should match")
        );

        verify(eventValidator, times(1)).validateEvent(dto);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void getAllEvents_ShouldReturnEventList() {
        List<Event> events = List.of(
            new Event("Title1", "Description1", LocalDate.now(), LocalDate.now().plusDays(1),
                "Category1", List.of(1L)));
        when(eventRepository.findAll()).thenReturn(events);

        List<EventDetailDto> result = eventService.getAllEvents();

        assertFalse(result.isEmpty(), "Resulting event list should not be empty");
        assertEquals(1, result.size(), "Result list size should match");
        assertEquals("Title1", result.getFirst().getTitle(),
            "Title of the first event should match");

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

    @Test
    void searchEventByTitleReturnsMatchingEvent() {
        EventDetailDto event1DetailDto = new EventDetailDto(1L, "Matching Title", "Description1",
            "Category1", LocalDate.now(), LocalDate.now().plusDays(1));
        Event event1 = new Event("Matching Title", "Description1", LocalDate.now(),
            LocalDate.now().plusDays(1), "Category1", List.of(1L));
        Event event2 = new Event("Other Title", "Description2", LocalDate.now(),
            LocalDate.now().plusDays(1), "Category2", List.of(1L));
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        when(eventMapper.eventToEventDetailDto(event1)).thenReturn(event1DetailDto);

        EventSearchDto searchDto = new EventSearchDto("Matching", null, null, null, null, null);

        List<EventDetailDto> result = eventService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return only one event");
        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).eventToEventDetailDto(event1);
        verify(eventMapper, never()).eventToEventDetailDto(event2);
    }

    @Test
    void searchEventByTitleReturnsNoEventsWhenNoMatch() {
        Event event1 = new Event("Some Title", "Description1", LocalDate.now(),
            LocalDate.now().plusDays(1), "Category1", List.of(1L));
        Event event2 = new Event("Other Title", "Description2", LocalDate.now(),
            LocalDate.now().plusDays(1), "Category2", List.of(1L));
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        EventSearchDto searchDto = new EventSearchDto(null, null, null, LocalDate.of(1900, 1, 1),
            null, null);

        List<EventDetailDto> result = eventService.search(searchDto).toList();

        assertEquals(0, result.size(), "Should return no events");
        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, never()).eventToEventDetailDto(any(Event.class));
    }

    @Test
    void searchEventByCategoryReturnsMatchingEvent() {
        EventDetailDto event1DetailDto = new EventDetailDto(1L, "Matching Title", "Description1",
            "Category1", LocalDate.now(), LocalDate.now().plusDays(1));
        Event event1 = new Event("Matching Title", "Description1", LocalDate.now(),
            LocalDate.now().plusDays(1), "Category1", List.of(1L));
        Event event2 = new Event("Other Title", "Description2", LocalDate.now(),
            LocalDate.now().plusDays(1), "Category2", List.of(1L));
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        when(eventMapper.eventToEventDetailDto(event1)).thenReturn(event1DetailDto);

        EventSearchDto searchDto = new EventSearchDto(null, "Category1", null, null, null, null);

        List<EventDetailDto> result = eventService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return only one event");
        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).eventToEventDetailDto(event1);
        verify(eventMapper, never()).eventToEventDetailDto(event2);
    }

    @Test
    void searchEventByDateEarliestReturnsMatchingEvent() {
        LocalDate now = LocalDate.now();
        EventDetailDto event1DetailDto = new EventDetailDto(1L, "Matching Title", "Description1",
            "Category1", now, now.plusDays(1));
        Event event1 = new Event("Matching Title", "Description1", now,
            LocalDate.now().plusDays(1), "Category1", List.of(1L));
        Event event2 = new Event("Other Title", "Description2", now.minusDays(10),
            LocalDate.now().plusDays(1), "Category2", List.of(1L));
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        when(eventMapper.eventToEventDetailDto(event1)).thenReturn(event1DetailDto);

        EventSearchDto searchDto = new EventSearchDto(null, null, now.minusDays(1), null, null, null);

        List<EventDetailDto> result = eventService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return only one event");
        verify(eventRepository, times(1)).findAll();
        verify(eventMapper, times(1)).eventToEventDetailDto(event1);
        verify(eventMapper, never()).eventToEventDetailDto(event2);
    }

    @Test
    void getEventsByArtistId_ShouldReturnEventList() {
        Long artistId = 1L;
        Event event = new Event("Event1", "Description1", null, null, "Category1", List.of(artistId));
        EventDetailDto eventDetailDto = new EventDetailDto(1L, "Event1", "Description1", "Category1", null, null);

        when(eventRepository.findEventsByArtistId(artistId)).thenReturn(List.of(event));
        when(eventMapper.eventToEventDetailDto(event)).thenReturn(eventDetailDto);

        List<EventDetailDto> result = eventService.getEventsByArtistId(artistId);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one event");
        assertEquals(eventDetailDto.getTitle(), result.getFirst().getTitle(), "Event title should match");

        verify(eventRepository, times(1)).findEventsByArtistId(artistId);
        verify(eventMapper, times(1)).eventToEventDetailDto(event);
    }

    void getTop10Events_ShouldReturnTopEventsList() {
        Integer year = 2023;
        Integer month = 1;
        String category = "Music";

        Object[] event1 = {1L, "Event1", 100L, 200L, 300.50};
        Object[] event2 = {2L, "Event2", 150L, 250L, 400.75};
        when(eventRepository.findTop10EventsAsObjects(year, month, category)).thenReturn(List.of(event1, event2));

        List<EventSalesDto> result = eventService.getTop10Events(year, month, category);

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return two events");
        assertEquals(1L, result.getFirst().getEventId(), "First event ID should match");
        assertEquals("Event1", result.getFirst().getEventTitle(), "First event title should match");

        verify(eventRepository, times(1)).findTop10EventsAsObjects(year, month, category);
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() {
        List<String> categories = List.of("Music", "Art", "Sports");
        when(eventRepository.findAllCategories()).thenReturn(categories);

        List<String> result = eventService.getAllCategories();

        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.size(), "Should return three categories");
        assertTrue(result.contains("Music"), "Result should contain 'Music'");

        verify(eventRepository, times(1)).findAllCategories();
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenIdExists() {
        Long eventId = 1L;
        Event event = new Event("Event1", "Description1", LocalDate.now(), LocalDate.now().plusDays(1), "Category1", List.of(1L));
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventDetailDto result = eventService.getEventById(eventId);

        assertNotNull(result, "Result should not be null");
        assertEquals(eventId, result.getEventId(), "Event ID should match");
        assertEquals("Event1", result.getTitle(), "Event title should match");
        assertEquals("Description1", result.getDescription(), "Event description should match");

        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void deleteEvent_ShouldDeleteEvent_WhenIdExists() {
        Long eventId = 1L;

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

}
