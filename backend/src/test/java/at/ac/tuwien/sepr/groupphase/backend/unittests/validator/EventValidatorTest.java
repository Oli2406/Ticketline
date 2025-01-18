package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.EventValidator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class EventValidatorTest {

    private EventValidator eventValidator;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventValidator = new EventValidator(eventRepository, performanceRepository);
    }

    private EventCreateDto createValidEventDto() {
        EventCreateDto event = new EventCreateDto();
        event.setTitle("Valid Event");
        event.setDescription("A valid description.");
        event.setCategory("Concert");
        event.setDateFrom(LocalDate.now().plusDays(1));
        event.setDateTo(LocalDate.now().plusDays(10));
        event.setPerformanceIds(List.of(1L, 2L));
        return event;
    }

    @Test
    void validateEvent_validInput_noExceptionsThrown() {
        EventCreateDto event = createValidEventDto();

        Performance performance1 = new Performance();
        performance1.setDate(LocalDate.now().plusDays(2).atStartOfDay());
        performance1.setName("Performance 1");

        Performance performance2 = new Performance();
        performance2.setDate(LocalDate.now().plusDays(4).atStartOfDay());
        performance2.setName("Performance 2");

        when(performanceRepository.findAllById(event.getPerformanceIds()))
            .thenReturn(List.of(performance1, performance2));
        when(eventRepository.existsByTitleAndDateFromAndDateTo(any(), any(), any())).thenReturn(false);

        assertDoesNotThrow(() -> eventValidator.validateEvent(event));
    }

    @Test
    void validateEvent_missingTitle_throwsValidationException() {
        EventCreateDto event = createValidEventDto();
        event.setTitle("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        assertTrue(exception.getErrors().contains("Event title is required"));
    }

    @Test
    void validateEvent_titleTooLong_throwsValidationException() {
        EventCreateDto event = createValidEventDto();
        event.setTitle("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        assertTrue(exception.getErrors().contains("Event title must be less than 255 characters"));
    }

    @Test
    void validateEvent_dateFromInPast_throwsValidationException() {
        EventCreateDto event = createValidEventDto();
        event.setDateFrom(LocalDate.now().minusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        assertTrue(exception.getErrors().contains("Event date cannot be in the past"));
    }

    @Test
    void validateEvent_dateToBeforeDateFrom_throwsValidationException() {
        EventCreateDto event = createValidEventDto();
        event.setDateFrom(LocalDate.now().plusDays(2));
        event.setDateTo(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        assertTrue(exception.getErrors().contains("Event end date cannot be before start date"));
    }

    @Test
    void validateEvent_missingPerformanceIds_throwsValidationException() {
        EventCreateDto event = createValidEventDto();
        event.setPerformanceIds(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        assertTrue(exception.getErrors().contains("At least one performance must be provided"));
    }

    @Test
    void validateEvent_performanceOutsideEventRange_throwsValidationException() {
        EventCreateDto event = createValidEventDto();

        Performance performance = new Performance();
        performance.setDate(LocalDate.now().plusDays(25).atStartOfDay());
        performance.setName("Invalid Performance");

        when(performanceRepository.findAllById(event.getPerformanceIds()))
            .thenReturn(List.of(performance));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        assertTrue(exception.getErrors().contains("Performance date " + performance.getDate().toLocalDate()));
    }

    @Test
    void checkEventUnique_duplicateEvent_throwsConflictException() {
        String title = "Event";
        LocalDate dateFrom = LocalDate.now().plusDays(1);
        LocalDate dateTo = LocalDate.now().plusDays(2);

        when(eventRepository.existsByTitleAndDateFromAndDateTo(title, dateFrom, dateTo))
            .thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            eventValidator.checkEventUnique(title, dateFrom, dateTo)
        );

        assertTrue(exception.getErrors().contains("Event with the title 'Event' already exists on the start date"));
    }

    @Test
    void validateEvent_allInvalidFields_throwsValidationException() {
        EventCreateDto event = createValidEventDto();

        event.setDescription("");
        event.setCategory("");
        event.setDateFrom(null);
        event.setDateTo(null);

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        List<String> errors = exception.errors();
        System.out.println(errors);

        assertTrue(errors.stream().anyMatch(error -> error.contains("Event description is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Event category is required")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Event date is required")));
    }

    @Test
    void validateEvent_allLengthExceedingFields_throwsValidationException() {
        EventCreateDto event = createValidEventDto();

        event.setTitle("A".repeat(256));
        event.setDescription("B".repeat(256));
        event.setCategory("C".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            eventValidator.validateEvent(event)
        );

        List<String> errors = exception.errors();
        System.out.println(errors);

        assertTrue(errors.stream().anyMatch(error -> error.contains("Event title must be less than 255 characters")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Event description must be less than 255 characters")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Event category must be less than 255 characters")));
    }

}
