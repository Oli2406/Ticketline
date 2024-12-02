package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import org.springframework.stereotype.Component;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationValidator.class);

    private final LocationRepository locationRepository;

    public LocationValidator(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void validateLocation(LocationCreateDto locationCreateDto) throws ValidationException, ConflictException {
        LOGGER.trace("Validating location: {}", locationCreateDto);
        List<String> validationErrors = new ArrayList<>();

        if (locationCreateDto.getName() == null || locationCreateDto.getName().trim().isEmpty()) {
            validationErrors.add("Location name is required");
        } else if (locationCreateDto.getName().length() > 255) {
            validationErrors.add("Location name must be less than 255 characters");
        }

        if (locationCreateDto.getStreet() == null || locationCreateDto.getStreet().trim().isEmpty()) {
            validationErrors.add("Street is required");
        }

        if (locationCreateDto.getCity() == null || locationCreateDto.getCity().trim().isEmpty()) {
            validationErrors.add("City is required");
        }

        if (locationCreateDto.getPostalCode() == null || locationCreateDto.getPostalCode().trim().isEmpty()) {
            validationErrors.add("Postal code is required");
        }

        if (locationCreateDto.getCountry() == null || locationCreateDto.getCountry().trim().isEmpty()) {
            validationErrors.add("Country is required");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Location validation failed with errors: {}", validationErrors);
            throw new ValidationException("Location validation failed", validationErrors);
        }
        checkLocationUnique(locationCreateDto.getName(), locationCreateDto.getCity());
        LOGGER.info("Location validation passed for: {}", locationCreateDto);
    }

    public void checkLocationUnique(String name, String city) throws ConflictException {
        if (locationRepository.existsByNameAndCity(name, city)) {
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("Location with the name '" + name + "' already exists in the city '" + city + "'");
            LOGGER.warn("Conflict detected for location: {}, {}", name, city);
            throw new ConflictException("Location creation conflict detected", conflictErrors);
        }
    }
}

