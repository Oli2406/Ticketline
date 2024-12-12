package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PerformanceRepositoryUnitTest {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private LocationRepository locationRepository;

    private Performance testPerformance;
    private Artist testArtist;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Create and save Artist
        testArtist = new Artist("firstname", "lastname", "artistname");
        testArtist = artistRepository.save(testArtist);

        // Create and save Location
        testLocation = new Location("name", "street", "city", "1221", "country");
        testLocation = locationRepository.save(testLocation);

        // Create Performance
        testPerformance = new Performance();
        testPerformance.setName("Test Performance");
        testPerformance.setArtistId(testArtist.getArtistId());
        testPerformance.setLocationId(testLocation.getLocationId());
        testPerformance.setDate(LocalDate.now());
        testPerformance.setPrice(BigDecimal.valueOf(100));
        testPerformance.setTicketNumber(100L);
        testPerformance.setHall("Main Hall");
        testPerformance.setArtist(testArtist);
        testPerformance.setLocation(testLocation);
    }

    @Test
    void saveAndRetrievePerformance() {
        Performance savedPerformance = performanceRepository.save(testPerformance);
        Optional<Performance> retrievedPerformance = performanceRepository.findById(savedPerformance.getPerformanceId());
        assertTrue(retrievedPerformance.isPresent(), "Performance should be saved and retrieved successfully");
        assertEquals("Test Performance", retrievedPerformance.get().getName(), "Performance name should match");
    }

    @Test
    void existsByHall_ReturnsTrueIfExists() {
        performanceRepository.save(testPerformance);
        boolean exists = performanceRepository.existsByHall("Main Hall");
        assertTrue(exists, "Performance in hall 'Main Hall' should exist");
    }

    @Test
    void existsByNameAndLocationIdAndDate_ReturnsTrueIfExists() {
        performanceRepository.save(testPerformance);
        boolean exists = performanceRepository.existsByNameAndLocationIdAndDate("Test Performance", testLocation.getLocationId(), LocalDate.now());
        assertTrue(exists, "Performance with matching name, location, and date should exist");
    }
}
