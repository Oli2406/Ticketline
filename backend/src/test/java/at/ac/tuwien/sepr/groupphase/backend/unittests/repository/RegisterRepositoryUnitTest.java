package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RegisterRepositoryUnitTest {

    @Autowired
    private UserRepository userRepository;

    private ApplicationUser testUser;
    private ApplicationUser testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new ApplicationUser();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("securePassword123");
        testUser.setAdmin(false);
        testUser.setLocked(false);
        testUser.setPoints(100);

        testAdmin = new ApplicationUser();
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("Mustermann");
        testAdmin.setEmail("admin.user@example.com");
        testAdmin.setPassword("adminPassword123");
        testAdmin.setAdmin(true);
        testAdmin.setLocked(false);
        testAdmin.setPoints(0);
    }

    @Test
    void saveAndRetrieveUser() {
        ApplicationUser savedUser = userRepository.save(testUser);
        Optional<ApplicationUser> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
    }

    @Test
    void updateUser() {
        ApplicationUser savedUser = userRepository.save(testUser);
        savedUser.setLocked(true);
        ApplicationUser updatedUser = userRepository.save(savedUser);
        Optional<ApplicationUser> retrievedUser = userRepository.findById(updatedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertTrue(retrievedUser.get().isLocked());
    }

    @Test
    void deleteUser() {
        ApplicationUser savedUser = userRepository.save(testUser);
        userRepository.delete(savedUser);
        Optional<ApplicationUser> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void existsByEmail_ReturnsTrueIfExists() {
        userRepository.save(testUser);
        boolean exists = userRepository.existsByEmail("john.doe@example.com");
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ReturnsFalseIfNotExists() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists);
    }

    @Test
    void loyaltyPoints_DefaultIsZero() {
        ApplicationUser userWithoutPoints = new ApplicationUser();
        userWithoutPoints.setFirstName("Jane");
        userWithoutPoints.setLastName("Doe");
        userWithoutPoints.setEmail("jane.doe@example.com");
        userWithoutPoints.setPassword("securePassword123");
        userWithoutPoints.setAdmin(false);
        ApplicationUser savedUser = userRepository.save(userWithoutPoints);
        assertEquals(0, savedUser.getPoints());
    }

    @Test
    void saveAndRetrieveAdmin() {
        ApplicationUser savedUser = userRepository.save(testAdmin);
        Optional<ApplicationUser> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
        assertTrue(retrievedUser.get().isAdmin());
    }
}
