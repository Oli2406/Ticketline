package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LocationRepositoryUnitTest {

    @Autowired
    private LocationRepository locationRepository;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setName("Test Location");
        testLocation.setStreet("Test Street");
        testLocation.setCity("Test City");
        testLocation.setPostalCode("12345");
        testLocation.setCountry("Test Country");
    }

    @Test
    void saveAndRetrieveLocation() {
        Location savedLocation = locationRepository.save(testLocation);
        Optional<Location> retrievedLocation = locationRepository.findById(savedLocation.getLocationId());
        assertTrue(retrievedLocation.isPresent(), "Location should be saved and retrieved successfully");
        assertEquals("Test Location", retrievedLocation.get().getName(), "Location name should match");
    }

    @Test
    void existsByName_ReturnsTrueIfExists() {
        locationRepository.save(testLocation);
        boolean exists = locationRepository.existsByName("Test Location");
        assertTrue(exists, "Location with name 'Test Location' should exist");
    }

    @Test
    void existsByNameAndCity_ReturnsTrueIfExists() {
        locationRepository.save(testLocation);
        boolean exists = locationRepository.existsByNameAndCity("Test Location", "Test City");
        assertTrue(exists, "Location with matching name and city should exist");
    }
}

