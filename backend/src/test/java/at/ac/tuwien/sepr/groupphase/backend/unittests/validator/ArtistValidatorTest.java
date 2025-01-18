package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.ArtistValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class ArtistValidatorTest {

    private ArtistValidator artistValidator;

    @Mock
    private ArtistRepository artistRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        artistValidator = new ArtistValidator(artistRepository);
    }

    @Test
    void validateArtist_validInput_noExceptionsThrown() {
        ArtistCreateDto artist = new ArtistCreateDto();
        artist.setArtistName("Valid Artist Name");
        artist.setFirstName("John");
        artist.setLastName("Doe");

        when(artistRepository.existsByArtistName("Valid Artist Name")).thenReturn(false);

        assertDoesNotThrow(() -> artistValidator.validateArtist(artist));

        verify(artistRepository, times(1)).existsByArtistName("Valid Artist Name");
    }

    @Test
    void validateArtist_emptyArtistName_throwsValidationException() {
        ArtistCreateDto artist = new ArtistCreateDto();
        artist.setArtistName("   ");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            artistValidator.validateArtist(artist)
        );

        assertTrue(exception.getErrors().contains("Artist name is required"));
    }

    @Test
    void validateArtist_artistNameTooLong_throwsValidationException() {
        ArtistCreateDto artist = new ArtistCreateDto();
        artist.setArtistName("A".repeat(65));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            artistValidator.validateArtist(artist)
        );

        assertTrue(exception.getErrors().contains("Artist name must be less than 64 characters"));
    }

    @Test
    void validateArtist_firstNameTooLong_throwsValidationException() {
        ArtistCreateDto artist = new ArtistCreateDto();
        artist.setArtistName("Valid Artist Name");
        artist.setFirstName("A".repeat(65));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            artistValidator.validateArtist(artist)
        );

        assertTrue(exception.getErrors().contains("First name must be less than 64 characters"));
    }

    @Test
    void validateArtist_lastNameTooLong_throwsValidationException() {
        ArtistCreateDto artist = new ArtistCreateDto();
        artist.setArtistName("Valid Artist Name");
        artist.setLastName("A".repeat(65));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            artistValidator.validateArtist(artist)
        );

        assertTrue(exception.getErrors().contains("Surname must be less than 64 characters"));
    }

    @Test
    void validateArtist_artistNameNotUnique_throwsConflictException() {
        ArtistCreateDto artist = new ArtistCreateDto();
        artist.setArtistName("Duplicate Artist Name");

        when(artistRepository.existsByArtistName("Duplicate Artist Name")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            artistValidator.validateArtist(artist)
        );

        assertTrue(exception.getErrors().contains("Artist with the name 'Duplicate Artist Name' already exists"));
        verify(artistRepository, times(1)).existsByArtistName("Duplicate Artist Name");
    }
}

