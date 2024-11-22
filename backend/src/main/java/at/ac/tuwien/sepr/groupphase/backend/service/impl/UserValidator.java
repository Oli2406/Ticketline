package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.repository.RegisterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RegisterRepository registerRepository;

    public UserValidator(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public void validateRegister(UserRegistrationDto registerDto) throws ValidationException, ConflictException {
        LOGGER.trace("validateRegister({})", registerDto);
        List<String> validationErrors = new ArrayList<>();

        isEmailUnique(registerDto.getEmail());

        if (registerDto.getFirstName() == null || registerDto.getFirstName().trim().isEmpty()) {
            validationErrors.add("First name is required");
        } else if (registerDto.getFirstName().length() > 255) {
            validationErrors.add("First name must be less than 255 characters");
        } else if (!registerDto.getFirstName().matches("^[a-zA-Z]+(?:[' -][a-zA-Z]+)*$")) {
            validationErrors.add("First name must contain only letters, apostrophes, hyphens, and spaces");
        }

        if (registerDto.getLastName() == null || registerDto.getLastName().trim().isEmpty()) {
            validationErrors.add("Last name is required");
        } else if (registerDto.getLastName().length() > 255) {
            validationErrors.add("Last name must be less than 255 characters");
        } else if (!registerDto.getLastName().matches("^[a-zA-Z]+(?:[' -][a-zA-Z]+)*$")) {
            validationErrors.add("Last name must contain only letters, apostrophes, hyphens, and spaces");
        }

        if (registerDto.getEmail() == null || registerDto.getEmail().trim().isEmpty()) {
            validationErrors.add("Email must not be empty");
        } else if (!registerDto.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,63}$")) {
            validationErrors.add("Invalid email format");
        }

        if (registerDto.getPassword() == null || registerDto.getPassword().trim().isEmpty()) {
            validationErrors.add("Password must not be empty");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("User data validation failed");
            throw new ValidationException("User data validation registration failed: ", validationErrors);
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
}
