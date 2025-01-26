package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import at.ac.tuwien.sepr.groupphase.backend.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        closeable.close();
    }

    private void setUpSecurityContext(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testUnlockUserSelfUnlockShouldThrowException() {
        ApplicationUser currentUser = new ApplicationUser();
        currentUser.setId(1L);
        currentUser.setEmail("admin@example.com");
        setUpSecurityContext("admin@example.com");
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.unlockUser(1L);
        });
        assertEquals("Admins cannot unlock themselves.", exception.getMessage());
    }

    @Test
    void testUnlockUserUserNotFoundShouldThrowNotFoundException() {
        ApplicationUser currentUser = new ApplicationUser();
        currentUser.setId(1L);
        currentUser.setEmail("admin@example.com");
        setUpSecurityContext("admin@example.com");
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            adminService.unlockUser(2L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUnlockUserSuccess() {
        ApplicationUser currentUser = new ApplicationUser();
        currentUser.setId(1L);
        currentUser.setEmail("admin@example.com");
        setUpSecurityContext("admin@example.com");
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));

        ApplicationUser targetUser = new ApplicationUser();
        targetUser.setId(2L);
        targetUser.setLocked(true);
        targetUser.setLoginAttempts(3);

        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        adminService.unlockUser(2L);

        assertFalse(targetUser.isLocked());
        assertEquals(0, targetUser.getLoginAttempts());
        verify(userRepository).save(targetUser);
    }

    @Test
    void testLockUserSelfLockShouldThrowException() {
        ApplicationUser currentUser = new ApplicationUser();
        currentUser.setId(1L);
        currentUser.setEmail("admin@example.com");
        setUpSecurityContext("admin@example.com");
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.lockUser(1L);
        });
        assertEquals("Admins cannot lock themselves.", exception.getMessage());
    }

    @Test
    void testLockUserUserNotFoundShouldThrowNotFoundException() {
        ApplicationUser currentUser = new ApplicationUser();
        currentUser.setId(1L);
        currentUser.setEmail("admin@example.com");
        setUpSecurityContext("admin@example.com");
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            adminService.lockUser(2L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLockUserSuccess() {

        ApplicationUser currentUser = new ApplicationUser();
        currentUser.setId(1L);
        currentUser.setEmail("admin@example.com");
        setUpSecurityContext("admin@example.com");
        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));

        ApplicationUser targetUser = new ApplicationUser();
        targetUser.setId(2L);
        targetUser.setLocked(false);  // Initially unlocked
        targetUser.setLoggedIn(true); // Currently logged in

        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        // Act: attempt to lock target user.
        adminService.lockUser(2L);

        // Assert: the target user is locked and logged out, and the user is saved.
        assertTrue(targetUser.isLocked());
        assertFalse(targetUser.isLoggedIn());
        verify(userRepository).save(targetUser);
    }

    @Test
    void testGetAllUsersSuccess() {
        ApplicationUser user1 = new ApplicationUser();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@example.com");
        user1.setLocked(false);
        user1.setLoggedIn(true);
        user1.setPoints(100);
        user1.setAdmin(false);

        ApplicationUser user2 = new ApplicationUser();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane@example.com");
        user2.setLocked(true);
        user2.setLoggedIn(false);
        user2.setPoints(200);
        user2.setAdmin(true);

        List<ApplicationUser> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDetailDto> userDetails = adminService.getAllUsers();

        assertEquals(2, userDetails.size());

        UserDetailDto dto1 = userDetails.getFirst();
        assertEquals(user1.getId(), dto1.getId());
        assertEquals(user1.getFirstName(), dto1.getFirstName());
        assertEquals(user1.getLastName(), dto1.getLastName());
        assertEquals(user1.getEmail(), dto1.getEmail());
        assertEquals(user1.isLocked(), dto1.isLocked());
        assertEquals(user1.isLoggedIn(), dto1.isLoggedIn());
        assertEquals(user1.getPoints(), dto1.getPoints());
        assertEquals(user1.isAdmin(), dto1.isAdmin());

        UserDetailDto dto2 = userDetails.get(1);
        assertEquals(user2.getId(), dto2.getId());
        assertEquals(user2.getFirstName(), dto2.getFirstName());
        assertEquals(user2.getLastName(), dto2.getLastName());
        assertEquals(user2.getEmail(), dto2.getEmail());
        assertEquals(user2.isLocked(), dto2.isLocked());
        assertEquals(user2.isLoggedIn(), dto2.isLoggedIn());
        assertEquals(user2.getPoints(), dto2.getPoints());
        assertEquals(user2.isAdmin(), dto2.isAdmin());
    }
}
