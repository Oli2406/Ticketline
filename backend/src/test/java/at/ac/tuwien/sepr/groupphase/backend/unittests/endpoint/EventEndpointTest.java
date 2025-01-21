package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.EventEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSalesDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventEndpointTest {

    private final EventDetailDto event1 = new EventDetailDto(1L, "Event1", "Descr1", "Category1", LocalDate.now(), LocalDate.now().plusDays(1));
    private final EventDetailDto event2 = new EventDetailDto(2L, "Event2", "Descr2", "Category2", LocalDate.now(), LocalDate.now().plusDays(1));
    private final List<EventDetailDto> mockEvents = List.of(event1, event2);
    private final List<EventSalesDto> mockEventSalesDtos = List.of(
        new EventSalesDto(1L, "Event1", 100L, 600L, 100.0/600.0),
        new EventSalesDto(2L, "Event2", 200L, 600L, 200.0/600.0)
    );
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventEndpoint eventEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEventWhenValidInputReturnsSuccess() throws ValidationException, ConflictException {
        EventCreateDto eventCreateDto = new EventCreateDto("Event1", "Descr1", "Category1", LocalDate.now(), LocalDate.now().plusDays(1), List.of(1L, 2L));

        when(eventService.createEvent(any(EventCreateDto.class))).thenReturn(event1);

        ResponseEntity<EventDetailDto> response = eventEndpoint.createOrUpdateEvent(eventCreateDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(event1, response.getBody());
    }

    @Test
    void createEventWhenInvalidInputThrowsValidationException() throws ValidationException, ConflictException {
        EventCreateDto eventCreateDto = new EventCreateDto("Event1", "Descr1", "Category1", LocalDate.now(), LocalDate.now().plusDays(1), List.of(1L, 2L));

        List<String> validationErrors = List.of("Name is required", "Name cannot exceed 64 Characters");
        doThrow(new ValidationException("Invalid input", validationErrors)).when(eventService).createEvent(any());

        ValidationException exception = assertThrows(ValidationException.class, () -> eventEndpoint.createOrUpdateEvent(eventCreateDto));

        assertTrue(exception.getMessage().contains("Invalid input"));
        assertEquals(validationErrors.toString(), exception.getErrors());
    }

    @Test
    void getAllEventsReturnsAllEvents() {
        when(eventService.getAllEvents()).thenReturn(mockEvents);

        ResponseEntity<List<EventDetailDto>> response = eventEndpoint.getAllEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvents, response.getBody());
    }

    @Test
    void getEventByIdReturnsEventWithGivenId() {
        Long eventId = 1L;
        when(eventService.getEventById(eventId)).thenReturn(event1);

        ResponseEntity<EventDetailDto> response = eventEndpoint.getEventById(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(event1, response.getBody());
    }

    @Test
    void deleteEventDeletesEventWithGivenId() {
        Long eventId = 1L;

        ResponseEntity<Void> response = eventEndpoint.deleteEvent(eventId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    void searchEventReturnsMatchingEvent() {
        EventSearchDto eventSearchDto = new EventSearchDto("","Category1", null, null, null, null);

        when(eventService.search(eventSearchDto)).thenReturn(Stream.of(event1));

        ResponseEntity<Stream<EventDetailDto>> response = eventEndpoint.search(eventSearchDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<EventDetailDto> result = Objects.requireNonNull(response.getBody()).toList();
        assertEquals(1, result.size());
        assertEquals(event1, result.getFirst());
    }

    @Test
    void getEventsByArtistIdReturnsEventsByArtistId() {
        Long artistId = 1L;
        when(eventService.getEventsByArtistId(artistId)).thenReturn(mockEvents);

        ResponseEntity<List<EventDetailDto>> response = eventEndpoint.getEventsByArtistId(artistId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvents, response.getBody());
    }

    @Test
    void getTop10EventsWithValidMonthParsesMonthAndYear() {
        String validMonth = "2023-05";
        when(eventService.getTop10Events(2023, 5, null)).thenReturn(mockEventSalesDtos);

        ResponseEntity<List<EventSalesDto>> response = eventEndpoint.getTop10Events(validMonth, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEventSalesDtos, response.getBody());
        verify(eventService, times(1)).getTop10Events(2023, 5, null);
    }

    @Test
    void getTop10EventsWithInvalidMonthThrowsException() {
        String invalidMonth = "invalid-month";
        assertThrows(Exception.class, () -> YearMonth.parse(invalidMonth));
    }

    @Test
    void getTop10EventsWithEmptyMonthDefaultsToNullYearAndMonth() {
        when(eventService.getTop10Events(null, null, null)).thenReturn(mockEventSalesDtos);

        ResponseEntity<List<EventSalesDto>> response = eventEndpoint.getTop10Events("", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEventSalesDtos, response.getBody());
        verify(eventService, times(1)).getTop10Events(null, null, null);
    }

    @Test
    void getTop10EventsWithNonEmptyCategory() {
        String category = "Music";
        when(eventService.getTop10Events(null, null, category)).thenReturn(mockEventSalesDtos);

        ResponseEntity<List<EventSalesDto>> response = eventEndpoint.getTop10Events(null, category);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEventSalesDtos, response.getBody());
        verify(eventService, times(1)).getTop10Events(null, null, category);
    }

    @Test
    void getTop10EventsWithEmptyCategoryDefaultsToNull() {
        String emptyCategory = "";
        when(eventService.getTop10Events(null, null, null)).thenReturn(mockEventSalesDtos);

        ResponseEntity<List<EventSalesDto>> response = eventEndpoint.getTop10Events(null, emptyCategory);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEventSalesDtos, response.getBody());
        verify(eventService, times(1)).getTop10Events(null, null, null);
    }

    @Test
    void getTop10EventsWithAllNullParams() {
        when(eventService.getTop10Events(null, null, null)).thenReturn(mockEventSalesDtos);

        ResponseEntity<List<EventSalesDto>> response = eventEndpoint.getTop10Events(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEventSalesDtos, response.getBody());
        verify(eventService, times(1)).getTop10Events(null, null, null);
    }

    @Test
    void getAllCategoriesReturnsAllCategories() {
        when(eventService.getAllCategories()).thenReturn(List.of("Category1", "Category2"));

        ResponseEntity<List<String>> response = eventEndpoint.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of("Category1", "Category2"), response.getBody());
        verify(eventService, times(1)).getAllCategories();
    }
}
