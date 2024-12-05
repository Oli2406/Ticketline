package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ArtistRepositoryUnitTest {

    @Autowired
    private ArtistRepository artistRepository;

    private Artist testArtist;

    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setFirstName("John");
        testArtist.setSurname("Doe");
        testArtist.setArtistName("JohnDoe");
    }

    @Test
    void saveAndRetrieveArtist() {
        Artist savedArtist = artistRepository.save(testArtist);
        Optional<Artist> retrievedArtist = artistRepository.findById(savedArtist.getArtistId());
        assertTrue(retrievedArtist.isPresent(), "Artist should be saved and retrieved successfully");
        assertEquals("JohnDoe", retrievedArtist.get().getArtistName(), "Artist name should match");
    }

    @Test
    void existsByArtistName_ReturnsTrueIfExists() {
        artistRepository.save(testArtist);
        boolean exists = artistRepository.existsByArtistName("JohnDoe");
        assertTrue(exists, "Artist with name 'JohnDoe' should exist");
    }

    @Test
    void existsByArtistName_ReturnsFalseIfNotExists() {
        boolean exists = artistRepository.existsByArtistName("NonExistentArtist");
        assertFalse(exists, "Artist with name 'NonExistentArtist' should not exist");
    }
}
