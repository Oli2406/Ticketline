package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DeleteUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.RegisterRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.UserDetailServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import java.util.ArrayList;
import java.util.List;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailServiceImplTest {

    private UserDetailServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationUser mockUser;

    @Mock
    private RegisterRepository registerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private UserValidator userValidator;

    @Mock
    private SecurityPropertiesConfig.Auth auth;
    @Mock
    private SecurityPropertiesConfig.Jwt jwt;

    @Mock
    private RandomStringGenerator randomStringGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService =
            new UserDetailServiceImpl(
                userRepository, passwordEncoder, jwtTokenizer, registerRepository, userValidator,
                jwt, auth, randomStringGenerator);
    }

    @Test
    void registerInvalidUserThrowsValidationException()
        throws ValidationException, ConflictException {
        // Arrange
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName("John");
        userRegistrationDto.setLastName("Doe");
        userRegistrationDto.setEmail("invalidEmail");
        userRegistrationDto.setPassword("password123");

        doThrow(new ValidationException("Invalid email", new ArrayList<>()))
            .when(userValidator)
            .validateRegister(userRegistrationDto);

        ValidationException exception =
            assertThrows(ValidationException.class,
                () -> userService.register(userRegistrationDto));

        assertEquals("Invalid email. Failed validations: .", exception.getMessage());

        verify(userValidator).validateRegister(userRegistrationDto);
        verifyNoInteractions(registerRepository, passwordEncoder, jwtTokenizer);
    }

    @Test
    void registerExistingEmailThrowsConflictException()
        throws ValidationException, ConflictException {
        // Arrange
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setFirstName("John");
        userRegistrationDto.setLastName("Doe");
        userRegistrationDto.setEmail("john.doe@example.com");
        userRegistrationDto.setPassword("password123");

        doThrow(new ConflictException("Email is already registered", new ArrayList<>()))
            .when(userValidator)
            .validateRegister(userRegistrationDto);

        ConflictException exception =
            assertThrows(ConflictException.class, () -> userService.register(userRegistrationDto));

        assertEquals("Email is already registered. Conflicts: .", exception.getMessage());

        verify(userValidator).validateRegister(userRegistrationDto);

        verifyNoInteractions(passwordEncoder, jwtTokenizer);
    }

    @Test
    void testUpdateReadNewsMarksNewsAsRead() {
        String email = "testuser@example.com";
        long newsId = 123L;
        UserUpdateReadNewsDto dto = new UserUpdateReadNewsDto();
        dto.setEmail(email);
        dto.setNewsId(newsId);

        List<Long> initialReadNewsIds = new ArrayList<>();
        initialReadNewsIds.add(1L);
        mockUser = mock(ApplicationUser.class);
        when(mockUser.getReadNewsIds()).thenReturn(initialReadNewsIds);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(mockUser));

        userService.updateReadNews(dto);
        assertTrue(mockUser.getReadNewsIds().contains(newsId));
    }

    @Test
    void testUpdateUserThrowsNotFoundExceptionIfUserNotFound() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId("encryptedId");

        when(randomStringGenerator.retrieveOriginalId("encryptedId")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUser(userUpdateDto));

        assertEquals("User not found for the given encrypted ID", exception.getMessage());
        verify(randomStringGenerator).retrieveOriginalId("encryptedId");
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void testUpdateUserThrowsValidationExceptionForInvalidToken() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId("encryptedId");
        userUpdateDto.setCurrentAuthToken("invalidToken");
        userUpdateDto.setPassword("");

        ApplicationUser userToUpdate = new ApplicationUser();
        userToUpdate.setId(1L);

        when(randomStringGenerator.retrieveOriginalId("encryptedId")).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        when(jwtTokenizer.validateToken("invalidToken")).thenReturn(false);

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.updateUser(userUpdateDto));

        assertEquals("Invalid authentication token", exception.getMessage());
    }

    @Test
    void testUpdateUserSuccessfullyUpdatesUserWithPasswordChange() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId("encryptedId");
        userUpdateDto.setFirstName("NewFirstName");
        userUpdateDto.setLastName("NewLastName");
        userUpdateDto.setEmail("new.email@example.com");
        userUpdateDto.setPassword("newSecurePassword");
        userUpdateDto.setCurrentAuthToken("validToken");

        ApplicationUser userToUpdate = new ApplicationUser();
        userToUpdate.setId(1L);
        userToUpdate.setFirstName("OldFirstName");
        userToUpdate.setLastName("OldLastName");
        userToUpdate.setEmail("old.email@example.com");
        userToUpdate.setPassword("oldPassword");
        userToUpdate.setAdmin(false);
        userToUpdate.setPoints(100);

        when(randomStringGenerator.retrieveOriginalId("encryptedId")).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        when(jwtTokenizer.validateToken("validToken")).thenReturn(true);
        when(randomStringGenerator.generateRandomString(1L)).thenReturn("randomString123");

        when(jwtTokenizer.getAuthToken(
            eq("new.email@example.com"),
            eq(List.of("ROLE_USER")),
            eq("randomString123"),
            eq(100),
            eq("NewFirstName"),
            eq("NewLastName")
        )).thenReturn("newAuthToken");

        String result = userService.updateUser(userUpdateDto);

        assertEquals("newAuthToken", result);

        verify(userRepository).save(userToUpdate);
        verify(jwtTokenizer).blockToken("validToken");

        assertEquals("NewFirstName", userToUpdate.getFirstName());
        assertEquals("NewLastName", userToUpdate.getLastName());
        assertEquals("new.email@example.com", userToUpdate.getEmail());
        assertEquals("newSecurePassword",
            userToUpdate.getPassword()); // Passwort wurde aktualisiert
    }


    @Test
    void testDeleteUserSuccessfullyDeletesUser() throws ValidationException {
        DeleteUserDto deleteUserDto = new DeleteUserDto();
        deleteUserDto.setEmail("user@example.com");
        deleteUserDto.setAuthToken("validToken");

        ApplicationUser userToDelete = new ApplicationUser();
        userToDelete.setEmail("user@example.com");
        userToDelete.setAdmin(false);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(
            Optional.of(userToDelete));
        when(jwtTokenizer.validateToken("validToken")).thenReturn(true);

        userService.deleteUser(deleteUserDto);

        verify(userRepository).delete(userToDelete);
        verify(jwtTokenizer).blockToken("validToken");
    }

    @Test
    void testDeleteUserThrowsNotFoundExceptionIfUserNotFound() {
        DeleteUserDto deleteUserDto = new DeleteUserDto();
        deleteUserDto.setEmail("unknown@example.com");

        when(userRepository.findUserByEmail("unknown@example.com")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userService.deleteUser(deleteUserDto));
        assertEquals("Could not find the user with the id unknown@example.com",
            exception.getMessage());
    }

    @Test
    void testDeleteUserThrowsValidationExceptionIfLastAdmin() {
        DeleteUserDto deleteUserDto = new DeleteUserDto();
        deleteUserDto.setEmail("admin@example.com");

        ApplicationUser adminUser = new ApplicationUser();
        adminUser.setAdmin(true);

        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(
            Optional.of(adminUser));
        when(userRepository.findAllByAdmin(true)).thenReturn(List.of(adminUser));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> userService.deleteUser(deleteUserDto));
        assertTrue(exception.getMessage()
            .contains("Can not delete user because at least one admin has to exist."));
    }

    @Test
    void testDeleteUserThrowsSecurityExceptionForInvalidToken() {
        DeleteUserDto deleteUserDto = new DeleteUserDto();
        deleteUserDto.setEmail("user@example.com");
        deleteUserDto.setAuthToken("invalidToken");

        ApplicationUser userToDelete = new ApplicationUser();
        userToDelete.setAdmin(false);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(
            Optional.of(userToDelete));
        when(jwtTokenizer.validateToken("invalidToken")).thenReturn(false);

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.deleteUser(deleteUserDto));
        assertEquals("Invalid authentication token", exception.getMessage());
    }
}
