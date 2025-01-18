package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.LocationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


class LocationValidatorTest {

    private LocationValidator locationValidator;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationValidator = new LocationValidator(locationRepository);
    }

    private LocationCreateDto createValidLocationDto() {
        LocationCreateDto location = new LocationCreateDto();
        location.setName("Valid Location");
        location.setStreet("Valid Street");
        location.setCity("Valid City");
        location.setPostalCode("12345");
        location.setCountry("Valid Country");
        return location;
    }

    @Test
    void validateLocation_validInput_noExceptionsThrown() {
        LocationCreateDto location = createValidLocationDto();

        when(locationRepository.existsByNameAndCity(location.getName(), location.getCity()))
            .thenReturn(false);

        assertDoesNotThrow(() -> locationValidator.validateLocation(location));
    }

    @Test
    void validateLocation_missingName_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setName("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Location name is required"));
    }

    @Test
    void validateLocation_nameTooLong_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setName("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Location name must be less than 255 characters"));
    }

    @Test
    void validateLocation_missingStreet_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setStreet("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Street is required"));
    }

    @Test
    void validateLocation_missingCity_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setCity("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("City is required"));
    }

    @Test
    void validateLocation_missingPostalCode_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setPostalCode("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Postal code is required"));
    }

    @Test
    void validateLocation_postalCodeNotNumeric_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setPostalCode("123AB");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Postal code must contain only numbers."));
    }

    @Test
    void validateLocation_missingCountry_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setCountry("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Country is required"));
    }

    @Test
    void validateLocation_locationExists_throwsConflictException() {
        LocationCreateDto location = createValidLocationDto();

        when(locationRepository.existsByNameAndCity(location.getName(), location.getCity()))
            .thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains(
            "Location with the name '" + location.getName() + "' already exists in the city '" + location.getCity() + "'"
        ));
    }

    @Test
    void validateLocation_streetTooLong_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setStreet("A".repeat(51));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Location street must be less than 255 characters"));
    }

    @Test
    void validateLocation_cityTooLong_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setCity("A".repeat(51));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Location city must be less than 50 characters"));
    }

    @Test
    void validateLocation_countryTooLong_throwsValidationException() {
        LocationCreateDto location = createValidLocationDto();
        location.setCountry("A".repeat(256));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            locationValidator.validateLocation(location)
        );

        assertTrue(exception.getErrors().contains("Location name must be less than 255 characters"));
    }
}
