package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DeleteUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EncryptedIdRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.UserDetailServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserDetailServiceImplTest {

    private UserDetailServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationUser mockUser;

    @Mock
    private ApplicationUser applicationUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private UserValidator userValidator;

    @Mock
    private SecurityPropertiesConfig.Auth auth;

    @Mock
    private EncryptedIdRepository encryptedIdRepository;

    @Mock
    private SecurityPropertiesConfig.Jwt jwt;

    @Mock
    private RandomStringGenerator randomStringGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService =
            new UserDetailServiceImpl(
                userRepository, passwordEncoder, jwtTokenizer, userValidator,
                jwt, auth, randomStringGenerator);
        when(auth.getMaxLoginAttempts()).thenReturn(5);
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
        verifyNoInteractions(applicationUser, passwordEncoder, jwtTokenizer);
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
        userUpdateDto.setVersion(1);
        userUpdateDto.setEmail("test@email.com");

        ApplicationUser userToUpdate = new ApplicationUser();
        userToUpdate.setId(1L);
        userToUpdate.setVersion(1);
        userToUpdate.setEmail("te@email.com");

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
        userUpdateDto.setVersion(1);

        ApplicationUser userToUpdate = new ApplicationUser();
        userToUpdate.setId(1L);
        userToUpdate.setFirstName("OldFirstName");
        userToUpdate.setLastName("OldLastName");
        userToUpdate.setEmail("old.email@example.com");
        userToUpdate.setPassword("oldPassword");
        userToUpdate.setAdmin(false);
        userToUpdate.setPoints(100);
        userToUpdate.setVersion(1);

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

    @Test
    void testLoadUserByUsernameThrowsNotFoundException() {
        String email = "nonexistent@example.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(email));

        assertEquals("Could not find the user with the email address nonexistent@example.com", exception.getMessage());
    }

    @Test
    void testLoginWithInvalidCredentialsThrowsBadCredentialsException() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("wrongPassword");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("userser@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAdmin(false);
        user.setPoints(100);
        user.setLoginAttempts(0);
        user.setLoggedIn(false);
        user.setPassword("rightPassword");

        when(userRepository.findUserByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
            () -> userService.login(loginDto));

        assertEquals("Username or password is incorrect", exception.getMessage());
    }


    @Test
    void testLogoutWithInvalidTokenThrowsSecurityException() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("invalidToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(true);

        when(userRepository.findUserByEmail(logoutDto.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenizer.validateToken(logoutDto.getAuthToken())).thenReturn(false);

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.logout(logoutDto));

        assertEquals("Invalid authentication token", exception.getMessage());
    }

    @Test
    void testIsUserLoggedInReturnsFalseWhenUserNotLoggedIn() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("authToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(false);

        when(userRepository.findUserByEmail(logoutDto.getEmail())).thenReturn(Optional.of(user));

        boolean isLoggedIn = userService.isUserLoggedIn(logoutDto);

        assertFalse(isLoggedIn);
        verify(jwtTokenizer).blockToken(logoutDto.getAuthToken());
    }

    @Test
    void testUpdateUserPointsWithInsufficientPointsThrowsRuntimeException() {
        String encryptedId = "encryptedId";
        int pointsToDeduct = 50;

        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setPoints(30);

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.updateUserPoints(encryptedId, pointsToDeduct));

        assertEquals("Insufficient points!", exception.getMessage());
    }

    @Test
    void testUpdateUserPointsWithSufficientPointsUpdates() throws Exception {
        String encryptedId = "encryptedId";
        int pointsToDeduct = 30;

        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setPoints(50);

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = userService.updateUserPoints(encryptedId, pointsToDeduct);

        assertEquals("Points updated successfully!", result);
        assertEquals(20, user.getPoints(), "User's points should be updated correctly");

        verify(randomStringGenerator).retrieveOriginalId(encryptedId);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void testAddUserPointsSuccessfullyAddsPoints() {
        String encryptedId = "encryptedId";
        int pointsToAdd = 20;

        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setPoints(30);

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.addUserPoints(encryptedId, pointsToAdd);

        verify(userRepository).save(user);
        assertEquals(50, user.getPoints());
    }

    @Test
    void testGetUserThrowsNotFoundExceptionForInvalidEncryptedId() {
        String encryptedId = "invalidId";

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getUser(encryptedId));

        assertEquals("User not found for the given encrypted ID", exception.getMessage());
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setEmail("user@example.com");
        applicationUser.setPassword("password123");
        applicationUser.setAdmin(false);
        applicationUser.setLocked(false);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(applicationUser));

        UserDetails userDetails = userService.loadUserByUsername("user@example.com");

        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testFindApplicationUserByEmailThrowsUsernameNotFoundException() {
        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("nonexistent@example.com"));

        assertEquals("Could not find the user with the email address nonexistent@example.com", exception.getMessage());
    }

    @Test
    void testLogoutSuccessfully() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("validToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(true);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenizer.validateToken("validToken")).thenReturn(true);

        userService.logout(logoutDto);

        verify(jwtTokenizer).blockToken("validToken");
        verify(userRepository).save(user);
        assertFalse(user.isLoggedIn());
    }

    @Test
    void testLogoutThrowsNotFoundException() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("nonexistent@example.com");

        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userService.logout(logoutDto));


        assertEquals("Could not find the user with the email address nonexistent@example.com", exception.getMessage());
    }

    @Test
    void testLogoutThrowsSecurityExceptionForInvalidToken() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("invalidToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(true);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenizer.validateToken("invalidToken")).thenReturn(false);

        SecurityException exception = assertThrows(SecurityException.class,
            () -> userService.logout(logoutDto));

        assertEquals("Invalid authentication token", exception.getMessage());
    }

    @Test
    void testIsUserLoggedInReturnsTrue() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("authToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(true);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        boolean result = userService.isUserLoggedIn(logoutDto);

        assertTrue(result);
    }

    @Test
    void testIsUserLoggedInReturnsFalseAndBlocksToken() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("authToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(false);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        boolean result = userService.isUserLoggedIn(logoutDto);

        assertFalse(result);
        verify(jwtTokenizer).blockToken("authToken");
    }

    @Test
    void testLoadUserByUsernameAdmin() {
        ApplicationUser adminUser = new ApplicationUser();
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("securePassword");
        adminUser.setAdmin(true);

        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = userService.loadUserByUsername("admin@example.com");

        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoginWithLockedUser() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("locked@example.com");
        loginDto.setPassword("password123");

        ApplicationUser lockedUser = new ApplicationUser();
        lockedUser.setFirstName("John");
        lockedUser.setLastName("Doe");
        lockedUser.setAdmin(false);
        lockedUser.setPoints(100);
        lockedUser.setLoginAttempts(0);
        lockedUser.setLoggedIn(false);
        lockedUser.setEmail("locked@example.com");
        lockedUser.setPassword("password123");
        lockedUser.setLocked(true);

        when(userRepository.findUserByEmail("locked@example.com")).thenReturn(Optional.of(lockedUser));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
            () -> userService.login(loginDto));

        assertEquals("Account is locked", exception.getMessage());
    }

    @Test
    void testLoginMaxAttemptsLock() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("wrongPassword");

        ApplicationUser user = new ApplicationUser();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAdmin(false);
        user.setPoints(100);
        user.setLoginAttempts(0);
        user.setLoggedIn(false);
        user.setLocked(false);
        user.setEmail("user@example.com");
        user.setPassword("correctPassword");
        user.setLoginAttempts(4);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
            () -> userService.login(loginDto));

        assertEquals("User account has been locked because of too many incorrect attempts", exception.getMessage());
    }

    @Test
    void testDeleteLastAdminThrowsException() {
        DeleteUserDto deleteUserDto = new DeleteUserDto();
        deleteUserDto.setEmail("admin@example.com");

        ApplicationUser admin = new ApplicationUser();
        admin.setEmail("admin@example.com");
        admin.setAdmin(true);

        when(userRepository.findUserByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findAllByAdmin(true)).thenReturn(List.of(admin));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> userService.deleteUser(deleteUserDto));

        assertTrue(exception.getMessage().contains("at least one admin has to exist"));
    }

    @Test
    void testUpdateUserEmailChange() throws ValidationException, ConflictException {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId("encryptedId");
        updateDto.setEmail("new@example.com");
        updateDto.setVersion(0);
        updateDto.setPassword("!Password123");
        updateDto.setCurrentAuthToken("validToken");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("old@example.com");
        user.setVersion(0);
        user.setPassword("hashedOldPassword");
        user.setAdmin(false);

        when(randomStringGenerator.retrieveOriginalId("encryptedId")).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtTokenizer.validateToken("validToken")).thenReturn(true);
        when(passwordEncoder.encode("!Password123")).thenReturn("hashedNewPassword");

        userService.updateUser(updateDto);

        verify(userValidator).validateUserForUpdate(updateDto, true);
        verify(userRepository).save(argThat(updatedUser ->
            updatedUser.getEmail().equals("new@example.com") &&
                updatedUser.getPassword().equals("hashedNewPassword")
        ));
        verify(jwtTokenizer).validateToken("validToken");
        verify(jwtTokenizer).blockToken("validToken");
    }

    @Test
    void testRegisterUser() throws ValidationException, ConflictException {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");
        registrationDto.setEmail("john.doe@example.com");
        registrationDto.setPassword("securePassword123");
        registrationDto.setIsAdmin(false);

        ApplicationUser savedUser = new ApplicationUser();
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setPassword("hashedPassword123");
        savedUser.setAdmin(false);
        savedUser.setPoints(0);

        doNothing().when(userValidator).validateRegister(registrationDto);
        when(passwordEncoder.encode("securePassword123")).thenReturn("hashedPassword123");

        when(userRepository.save(any(ApplicationUser.class))).thenAnswer(invocation -> {
            ApplicationUser user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        when(randomStringGenerator.generateRandomString(1L)).thenReturn("randomString123");

        when(jwtTokenizer.getAuthToken(
            "john.doe@example.com",
            List.of("ROLE_USER"),
            "randomString123",
            0,
            "John",
            "Doe"
        )).thenReturn("mockAuthToken");

        String token = userService.register(registrationDto);

        assertNotNull(token, "The token should not be null");
        assertEquals("mockAuthToken", token, "The token should match the mocked value");

        verify(userValidator).validateRegister(registrationDto);
        verify(passwordEncoder).encode("securePassword123");
        verify(userRepository).save(argThat(user ->
            user.getFirstName().equals("John") &&
                user.getLastName().equals("Doe") &&
                user.getEmail().equals("john.doe@example.com") &&
                user.getPassword().equals("hashedPassword123") &&
                !user.isAdmin()
        ));
        verify(randomStringGenerator, times(2)).generateRandomString(1L);
        verify(jwtTokenizer).getAuthToken(
            "john.doe@example.com",
            List.of("ROLE_USER"),
            "randomString123",
            0,
            "John",
            "Doe"
        );
    }

    @Test
    void testGetUserSuccess() {
        String encryptedId = "encrypted123";
        Long originalId = 1L;

        ApplicationUser user = new ApplicationUser();
        user.setId(originalId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setVersion(1);

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.of(originalId));
        when(userRepository.findById(originalId)).thenReturn(Optional.of(user));

        UserUpdateDto userDto = userService.getUser(encryptedId);

        assertNotNull(userDto, "UserUpdateDto should not be null");
        assertEquals(encryptedId, userDto.getId());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("john.doe@example.com", userDto.getEmail());
        assertEquals(1, userDto.getVersion());

        verify(randomStringGenerator).retrieveOriginalId(encryptedId);
        verify(userRepository).findById(originalId);
    }

    @Test
    void testGetUserThrowsRuntimeExceptionForInvalidEncryptedId() {
        String encryptedId = "invalidEncrypted123";

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getUser(encryptedId));

        assertEquals("User not found for the given encrypted ID", exception.getMessage());

        verify(randomStringGenerator).retrieveOriginalId(encryptedId);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testGetUserThrowsNotFoundExceptionForNonexistentUser() {
        String encryptedId = "encrypted123";
        Long originalId = 1L;

        when(randomStringGenerator.retrieveOriginalId(encryptedId)).thenReturn(Optional.of(originalId));
        when(userRepository.findById(originalId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userService.getUser(encryptedId));

        assertEquals(String.format("Could not find the user with the encrypted id %s", encryptedId),
            exception.getMessage());

        verify(randomStringGenerator).retrieveOriginalId(encryptedId);
        verify(userRepository).findById(originalId);
    }

    @Test
    void testUpdateUserThrowsNotFoundExceptionForMissingUser() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId("encryptedId");

        when(randomStringGenerator.retrieveOriginalId("encryptedId")).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userService.updateUser(userUpdateDto));

        assertEquals("Could not find the user with the email address null", exception.getMessage());

        verify(randomStringGenerator).retrieveOriginalId("encryptedId");
        verify(userRepository).findById(1L);
    }

    @Test
    void testUpdateUserThrowsConflictExceptionForVersionMismatch() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId("encryptedId");
        userUpdateDto.setVersion(1);

        ApplicationUser userToUpdate = new ApplicationUser();
        userToUpdate.setVersion(2);

        when(randomStringGenerator.retrieveOriginalId("encryptedId")).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));

        ConflictException exception = assertThrows(ConflictException.class,
            () -> userService.updateUser(userUpdateDto));

        assertTrue(exception.getMessage().contains("Update for customer has a conflict"));
        assertTrue(exception.getErrors().contains(
            "The data has been modified by another session. \n You will be logged out now."));

        verify(randomStringGenerator).retrieveOriginalId("encryptedId");
        verify(userRepository).findById(1L);
    }

    @Test
    void testIsUserLoggedInThrowsNotFoundExceptionWhenUserNotFound() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("nonexistent@example.com");
        logoutDto.setAuthToken("authToken123");

        when(userRepository.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> userService.isUserLoggedIn(logoutDto));

        assertEquals("Could not find the user with the email address nonexistent@example.com", exception.getMessage());

        verify(userRepository).findUserByEmail("nonexistent@example.com");
        verifyNoInteractions(jwtTokenizer); // Token blocking should not occur
    }

    @Test
    void testLogoutThrowsIllegalStateExceptionWhenUserNotLoggedIn() {
        UserLogoutDto logoutDto = new UserLogoutDto();
        logoutDto.setEmail("user@example.com");
        logoutDto.setAuthToken("authToken123");

        ApplicationUser user = new ApplicationUser();
        user.setEmail("user@example.com");
        user.setLoggedIn(false);

        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> userService.logout(logoutDto));

        assertEquals("The user with email user@example.com is not currently logged in", exception.getMessage());

        verify(userRepository).findUserByEmail("user@example.com");
        verifyNoInteractions(jwtTokenizer); // Token blocking should not occur
    }

}
