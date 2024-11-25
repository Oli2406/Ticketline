package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.RegisterUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.RegisterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RegisterRepositoryUnitTest {

    @Autowired
    private RegisterRepository registerRepository;

    private RegisterUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new RegisterUser();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("securePassword123");
        testUser.setAdmin(false);
        testUser.setBanned(false);
        testUser.setLoyaltyPoints(100);
    }

    @Test
    void saveAndRetrieveUser() {
        RegisterUser savedUser = registerRepository.save(testUser);
        Optional<RegisterUser> retrievedUser = registerRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
    }

    @Test
    void updateUser() {
        RegisterUser savedUser = registerRepository.save(testUser);
        savedUser.setBanned(true);
        RegisterUser updatedUser = registerRepository.save(savedUser);
        Optional<RegisterUser> retrievedUser = registerRepository.findById(updatedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertTrue(retrievedUser.get().isBanned());
    }

    @Test
    void deleteUser() {
        RegisterUser savedUser = registerRepository.save(testUser);
        registerRepository.delete(savedUser);
        Optional<RegisterUser> deletedUser = registerRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void existsByEmail_ReturnsTrueIfExists() {
        registerRepository.save(testUser);
        boolean exists = registerRepository.existsByEmail("john.doe@example.com");
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ReturnsFalseIfNotExists() {
        boolean exists = registerRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists);
    }

    @Test
    void loyaltyPoints_DefaultIsZero() {
        RegisterUser userWithoutPoints = new RegisterUser();
        userWithoutPoints.setFirstName("Jane");
        userWithoutPoints.setLastName("Doe");
        userWithoutPoints.setEmail("jane.doe@example.com");
        userWithoutPoints.setPassword("securePassword123");
        RegisterUser savedUser = registerRepository.save(userWithoutPoints);
        assertEquals(0, savedUser.getLoyaltyPoints());
    }
}
