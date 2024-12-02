package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.RegisterUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.RegisterRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomUserDetailServiceTest {

    private CustomUserDetailService userService;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService =
            new CustomUserDetailService(
                null, passwordEncoder, jwtTokenizer, registerRepository, userValidator, jwt, auth);
    }

    @Test
    void register_InvalidUser_ThrowsValidationException()
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
    void register_ExistingEmail_ThrowsConflictException()
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
}
