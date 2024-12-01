package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArtistValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistValidator.class);

    private final ArtistRepository artistRepository;

    public ArtistValidator(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public void validateArtist(ArtistCreateDto artistCreateDto) throws ValidationException, ConflictException {
        LOGGER.trace("Validating artist: {}", artistCreateDto);
        List<String> validationErrors = new ArrayList<>();

        // Check if first name is valid
        if (artistCreateDto.getFirstName() == null || artistCreateDto.getFirstName().trim().isEmpty()) {
            validationErrors.add("First name is required");
        } else if (artistCreateDto.getFirstName().length() > 255) {
            validationErrors.add("First name must be less than 255 characters");
        }

        // Check if surname is valid
        if (artistCreateDto.getSurname() == null || artistCreateDto.getSurname().trim().isEmpty()) {
            validationErrors.add("Surname is required");
        } else if (artistCreateDto.getSurname().length() > 255) {
            validationErrors.add("Surname must be less than 255 characters");
        }

        // Check if artistName is valid
        if (artistCreateDto.getArtistName() == null || artistCreateDto.getArtistName().trim().isEmpty()) {
            validationErrors.add("Artist name is required");
        } else if (artistCreateDto.getArtistName().length() > 255) {
            validationErrors.add("Artist name must be less than 255 characters");
        }

        // If there are validation errors, log and throw ValidationException
        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Artist validation failed with errors: {}", validationErrors);
            throw new ValidationException("Artist validation failed", validationErrors);
        }

        // Check for conflict: artistName uniqueness
        checkArtistNameUnique(artistCreateDto.getArtistName());

        LOGGER.info("Artist validation passed for: {}", artistCreateDto);
    }

    private void checkArtistNameUnique(String artistName) throws ConflictException {
        if (artistRepository.existsByArtistName(artistName)) {
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("Artist with the name '" + artistName + "' already exists");
            LOGGER.warn("Conflict detected for artistName: {}", artistName);
            throw new ConflictException("Artist creation conflict detected", conflictErrors);
        }
    }
}
