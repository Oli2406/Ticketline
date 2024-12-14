package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.RegisterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RegisterRepository registerRepository;

    public UserValidator(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public void validateRegister(UserRegistrationDto registerDto)
        throws ValidationException, ConflictException {
        LOGGER.trace("validateCreate({})", registerDto);
        List<String> validationErrors = new ArrayList<>();

        isEmailUnique(registerDto.getEmail());

        if (registerDto.getFirstName() == null || registerDto.getFirstName().trim().isEmpty()) {
            validationErrors.add("First name is required");
        } else if (registerDto.getFirstName().length() > 255) {
            validationErrors.add("First name must be less than 255 characters");
        } else if (!registerDto.getFirstName().matches("^[\\p{L}]+(?:[' -][\\p{L}]+)*$")) {
            validationErrors.add(
                "First name contains illegal characters");
        }

        if (registerDto.getLastName() == null || registerDto.getLastName().trim().isEmpty()) {
            validationErrors.add("Last name is required");
        } else if (registerDto.getLastName().length() > 255) {
            validationErrors.add("Last name must be less than 255 characters");
        } else if (!registerDto.getLastName().matches("^[\\p{L}]+(?:[' -][\\p{L}]+)*$")) {
            validationErrors.add(
                "Last name contains illegal characters");
        }

        if (registerDto.getEmail() == null || registerDto.getEmail().trim().isEmpty()) {
            validationErrors.add("Email must not be empty");
        } else if (!registerDto
            .getEmail()
            .matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,63}$")) {
            validationErrors.add("Invalid email format");
        }

        validationErrors.addAll(validatePassword(registerDto.getPassword()));

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("User data validation failed");
            throw new ValidationException("User data validation registration failed: ",
                validationErrors);
        }
    }

    public void isEmailUnique(String email) throws ConflictException {
        if (registerRepository.existsByEmail(email)) {
            List<String> error = new ArrayList<>();
            error.add("email is already registered");
            LOGGER.warn("conflict error in create : {}", error);
            throw new ConflictException("Update for customer has a conflict: ", error);
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
