package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateRegister(UserRegistrationDto registerDto)
        throws ValidationException, ConflictException {
        LOGGER.trace("validateCreate({})", registerDto);
        List<String> validationErrors = new ArrayList<>();

        isEmailUnique(registerDto.getEmail());

        validateName(registerDto.getFirstName(), validationErrors, "First name is required",
            "First name must be less than 255 characters",
            "First name contains illegal characters");

        validateName(registerDto.getLastName(), validationErrors, "Last name is required",
            "Last name must be less than 255 characters", "Last name contains illegal characters");

        validateEmail(registerDto.getEmail(), validationErrors);

        validationErrors.addAll(validatePassword(registerDto.getPassword()));

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("User data validation failed");
            throw new ValidationException("User data validation registration failed: ",
                validationErrors);
        }
    }

    public void validateUserForUpdate(UserUpdateDto user, boolean hasEmailChanged)
        throws ValidationException, ConflictException {
        LOGGER.trace("validateUserForUpdate({})", user);
        List<String> validationErrors = new ArrayList<>();

        if (hasEmailChanged) {
            isEmailUnique(user.getEmail());
        }

        validateName(user.getFirstName(), validationErrors, "First name is required",
            "First name must be less than 255 characters",
            "First name contains illegal characters");

        validateName(user.getLastName(), validationErrors, "Last name is required",
            "Last name must be less than 255 characters", "Last name contains illegal characters");

        validateEmail(user.getEmail(), validationErrors);

        if (!user.getPassword().isEmpty() && !user.getConfirmedPassword().isEmpty()) {
            validateNewPasswords(user.getPassword(), user.getConfirmedPassword());
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("User data validation failed");
            throw new ValidationException("User data validation registration failed: ",
                validationErrors);
        }
    }

    public void isEmailUnique(String email) throws ConflictException {
        if (userRepository.existsByEmail(email)) {
            List<String> error = new ArrayList<>();
            error.add("email is already registered");
            LOGGER.warn("conflict error in create : {}", error);
            throw new ConflictException("Update for customer has a conflict: ", error);
        }
    }

    private static void validateEmail(String email, List<String> validationErrors) {
        if (email == null || email.trim().isEmpty()) {
            validationErrors.add("Email must not be empty");
            return;
        }

        email = email.trim();

        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');

        if (atIndex < 1 || atIndex == email.length() - 1) {
            validationErrors.add("Invalid email format: missing characters before or after '@'");
            return;
        }

        if (dotIndex <= atIndex + 1 || dotIndex == email.length() - 1) {
            validationErrors.add("Invalid email format: missing characters after '@' or after '.'");
            return;
        }

        if (dotIndex == email.length() - 1) {
            validationErrors.add("Invalid email format: missing characters after '.'");
        }
    }

    private static void validateName(String registerDto, List<String> validationErrors,
        String requiredErrorMessage, String e, String containsIllegalCharacters) {
        if (registerDto == null || registerDto.trim().isEmpty()) {
            validationErrors.add(requiredErrorMessage);
        } else if (registerDto.length() > 255) {
            validationErrors.add(e);
        } else if (!registerDto.matches("^[\\p{L}]+(?:[' -][\\p{L}]+)*$")) {
            validationErrors.add(
                containsIllegalCharacters);
        }
    }

    private List<String> validatePassword(String password) {

        List<String> validationErrors = new ArrayList<>();
        if (password == null || password.isBlank()) {
            validationErrors.add("Password must not be empty");
        }

        if (password.length() < 8) {
            validationErrors.add("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            validationErrors.add("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            validationErrors.add("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            validationErrors.add("Password must contain at least one digit");
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            validationErrors.add("Password must contain at least one special character");
        }

        return validationErrors;
    }

    public void validateNewPasswords(String password, String confirmPassword)
        throws ValidationException {
        LOGGER.trace("validateNewPasswords({},{})", password, confirmPassword);

        List<String> validationErrors = new ArrayList<>();

        if (!password.equals(confirmPassword)) {
            validationErrors.add("Passwords do not match");
        }

        validationErrors.addAll(validatePassword(password));

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Password validation failed");
            throw new ValidationException("Reset password validation failed: ",
                validationErrors);
        }
    }
}
