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

        if (artistCreateDto.getArtistName() == null || artistCreateDto.getArtistName().trim().isEmpty()) {
            validationErrors.add("Artist name is required");
        }
        if (artistCreateDto.getArtistName().length() > 64) {
            validationErrors.add("Artist name must be less than 64 characters");
        }

        if (artistCreateDto.getFirstName() != null && artistCreateDto.getFirstName().length() > 64) {
            validationErrors.add("First name must be less than 64 characters");
        }

        if (artistCreateDto.getLastName() != null && artistCreateDto.getLastName().length() > 64) {
            validationErrors.add("Surname must be less than 64 characters");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Artist validation failed with errors: {}", validationErrors);
            throw new ValidationException("Artist validation failed", validationErrors);
        }

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
