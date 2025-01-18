package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


class UserValidatorTest {

    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userValidator = new UserValidator(userRepository);
    }

    /**
     * Utility method to create a valid UserRegistrationDto.
     */
    private UserRegistrationDto createValidRegistrationDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("Password123!");
        return dto;
    }

    /**
     * Utility method to create a valid UserUpdateDto.
     */
    private UserUpdateDto createValidUpdateDto() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("Password123!");
        dto.setConfirmedPassword("Password123!");
        return dto;
    }

    @Test
    void validateRegister_validInput_noExceptionsThrown() {
        UserRegistrationDto dto = createValidRegistrationDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateRegister(dto));
    }

    @Test
    void validateRegister_emailAlreadyExists_throwsConflictException() {
        UserRegistrationDto dto = createValidRegistrationDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            userValidator.validateRegister(dto)
        );

        assertTrue(exception.getErrors().contains("email is already registered"));
    }

    @Test
    void validateRegister_invalidEmailFormat_throwsValidationException() {
        UserRegistrationDto dto = createValidRegistrationDto();
        dto.setEmail("invalid-email");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateRegister(dto)
        );

        assertTrue(exception.getErrors().contains("Invalid email format"));
    }

    @Test
    void validateRegister_invalidPassword_throwsValidationException() {
        UserRegistrationDto dto = createValidRegistrationDto();
        dto.setPassword("short");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateRegister(dto)
        );

        List<String> errors = exception.errors();
        assertTrue(errors.stream().anyMatch(error -> error.contains("Password must be at least 8 characters long")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("Password must contain at least one uppercase letter")));
    }

    @Test
    void validateUserForUpdate_emailChangedAndExists_throwsConflictException() {
        UserUpdateDto dto = createValidUpdateDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            userValidator.validateUserForUpdate(dto, true)
        );

        assertTrue(exception.getErrors().contains("email is already registered"));
    }

    @Test
    void validateUserForUpdate_passwordsDoNotMatch_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setPassword("Password123!");
        dto.setConfirmedPassword("Password456!");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("Passwords do not match"));
    }

    @Test
    void validateUserForUpdate_invalidFirstName_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setFirstName("!nv@lidName");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("First name contains illegal characters"));
    }

    @Test
    void validateUserForUpdate_tooLongFirstName_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setFirstName("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("First name must be less than 255 characters"));
    }

    @Test
    void validateUserForUpdate_invalidLastName_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setLastName("!nvalid");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("Last name contains illegal characters"));
    }

    @Test
    void validateUserForUpdate_tooLongLastName_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setLastName("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("Last name must be less than 255 characters"));
    }

    @Test
    void validateUserForUpdate_validInput_noExceptionsThrown() {
        UserUpdateDto dto = createValidUpdateDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUserForUpdate(dto, true));
    }

    @Test
    void validateUser_emptyEmail_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setEmail("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("Email must not be empty"));
    }

    @Test
    void validateUser_allUppercasePassword_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setPassword("<PASSWORD>!");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("Password must contain at least one lowercase letter"));
    }

    @Test
    void validateUser_emptyPassword_throwsValidationException() {
        UserUpdateDto dto = createValidUpdateDto();
        dto.setPassword(" ");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            userValidator.validateUserForUpdate(dto, false)
        );

        assertTrue(exception.getErrors().contains("Password must not be empty"));
    }
}
