package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
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

    private Performance testPerformance;

    @BeforeEach
    void setUp() {
        testPerformance = new Performance();
        testPerformance.setName("Test Performance");
        testPerformance.setArtistId(1L);
        testPerformance.setLocationId(1L);
        testPerformance.setDate(LocalDate.now());
        testPerformance.setPrice(new BigDecimal("50.00"));
        testPerformance.setTicketNumber(100L);
        testPerformance.setHall("Main Hall");
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
        boolean exists = performanceRepository.existsByNameAndLocationIdAndDate("Test Performance", 1L, LocalDate.now());
        assertTrue(exists, "Performance with matching name, location, and date should exist");
    }
}

