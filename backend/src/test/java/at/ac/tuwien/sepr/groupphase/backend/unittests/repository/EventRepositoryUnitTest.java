package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryUnitTest {

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Description for Test Event");
        testEvent.setDateOfEvent(LocalDate.now());
        testEvent.setCategory("Category1");
        testEvent.setDuration(120);
    }

    @Test
    void saveAndRetrieveEvent() {
        Event savedEvent = eventRepository.save(testEvent);
        Optional<Event> retrievedEvent = eventRepository.findById(savedEvent.getEventId());
        assertTrue(retrievedEvent.isPresent(), "Event should be saved and retrieved successfully");
        assertEquals("Test Event", retrievedEvent.get().getTitle(), "Event title should match");
    }

    @Test
    void existsByTitle_ReturnsTrueIfExists() {
        eventRepository.save(testEvent);
        boolean exists = eventRepository.existsByTitle("Test Event");
        assertTrue(exists, "Event with title 'Test Event' should exist");
    }

    @Test
    void existsByTitle_ReturnsFalseIfNotExists() {
        boolean exists = eventRepository.existsByTitle("NonExistentTitle");
        assertFalse(exists, "Event with title 'NonExistentTitle' should not exist");
    }

    @Test
    void existsByTitleAndDateOfEvent_ReturnsTrueIfExists() {
        eventRepository.save(testEvent);
        boolean exists = eventRepository.existsByTitleAndDateOfEvent("Test Event", LocalDate.now());
        assertTrue(exists, "Event with matching title and date should exist");
    }

    @Test
    void existsByTitleAndDateOfEvent_ReturnsFalseIfNotExists() {
        boolean exists = eventRepository.existsByTitleAndDateOfEvent("Test Event", LocalDate.of(2000, 1, 1));
        assertFalse(exists, "Event with mismatched title or date should not exist");
    }
}

