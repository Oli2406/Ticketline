package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryUnitTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ArtistRepository artistRepository;

    private Artist artist;
    private Performance performance;
    private Event event;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        artist = new Artist("John", "Doe", "JDArtist");
        artist = artistRepository.save(artist);

        // Create and save a performance for the artist
        performance = new Performance();
        performance.setName("Concert A");
        performance.setArtist(artist);
        performance.setDate(LocalDateTime.now().plusDays(1));
        performance.setPrice(BigDecimal.valueOf(50));
        performance.setHall("Main Hall");
        performance.setTicketNumber(100L);
        performance.setDuration(120);
        performance = performanceRepository.save(performance);

        event = new Event();
        event.setTitle("Event 1");
        event.setDescription("Event Description");
        event.setDateFrom(LocalDate.now());
        event.setDateTo(LocalDate.now().plusDays(1));
        event.setCategory("Music");
        event.setPerformanceIds(List.of(performance.getPerformanceId()));
        event = eventRepository.save(event);

        testEvent = new Event();
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Description for Test Event");
        testEvent.setDateFrom(LocalDate.now());
        testEvent.setDateTo(LocalDate.now().plusDays(1));
        testEvent.setCategory("Category1");
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
        boolean exists = eventRepository.existsByTitleAndDateFromAndDateTo("Test Event", LocalDate.now(), LocalDate.now().plusDays(1));
        assertTrue(exists, "Event with matching title and date should exist");
    }

    @Test
    void existsByTitleAndDateOfEvent_ReturnsFalseIfNotExists() {
        boolean exists = eventRepository.existsByTitleAndDateFromAndDateTo("Test Event", LocalDate.of(2000, 1, 1), LocalDate.now());
        assertFalse(exists, "Event with mismatched title or date should not exist");
    }

    @Test
    void testFindEventsByArtistId() {
        List<Event> events = eventRepository.findEventsByArtistId(artist.getArtistId());

        assertThat(events).isNotNull();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().getTitle()).isEqualTo("Event 1");
        assertThat(events.getFirst().getPerformanceIds()).contains(performance.getPerformanceId());
    }

    @Test
    void testFindEventsByArtistId_NoEventsReturned() {
        Long nonExistingArtistId = 999L;

        List<Event> events = eventRepository.findEventsByArtistId(nonExistingArtistId);

        assertThat(events).isNotNull();
        assertThat(events).isEmpty();
    }
}

