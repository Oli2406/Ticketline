package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.LocationEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationEndpointTest {

    private final LocationDetailDto location1 = new LocationDetailDto(1L, "Location1", "Street1", "City1", "Postal1", "Country1");
    private final LocationDetailDto location2 = new LocationDetailDto(2L, "Location2", "Street2", "City2", "Postal2", "Country2");
    private final List<LocationDetailDto> mockLocations = List.of(location1, location2);
    @Mock
    private LocationService locationService;
    @InjectMocks
    private LocationEndpoint locationEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLocationWhenValidInputReturnsSuccess() throws ValidationException, ConflictException {
        LocationCreateDto locationCreateDto = new LocationCreateDto("Location1", "Street1", "City1", "Postal2", "Country2");

        when(locationService.createLocation(any(LocationCreateDto.class))).thenReturn(location1);

        ResponseEntity<LocationDetailDto> response = locationEndpoint.createOrUpdateLocation(locationCreateDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(location1, response.getBody());
    }

    @Test
    void createLocationWhenInvalidInputThrowsValidationException() throws ValidationException, ConflictException {
        LocationCreateDto locationCreateDto = new LocationCreateDto("Location1", "Street1", "City1", "Postal2", "Country2");

        List<String> validationErrors = List.of("Name is required", "Name cannot exceed 64 Characters");
        doThrow(new ValidationException("Invalid input", validationErrors)).when(locationService).createLocation(any());

        ValidationException exception = assertThrows(ValidationException.class, () -> locationEndpoint.createOrUpdateLocation(locationCreateDto));

        assertTrue(exception.getMessage().contains("Invalid input"));
        assertEquals(validationErrors.toString(), exception.getErrors());
    }

    @Test
    void getAllLocationsReturnsAllLocations() {
        when(locationService.getAllLocations()).thenReturn(mockLocations);

        ResponseEntity<List<LocationDetailDto>> response = locationEndpoint.getAllLocations();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLocations, response.getBody());
    }

    @Test
    void getLocationByIdReturnsLocationWithGivenId() {
        Long locationId = 1L;
        when(locationService.getLocationById(locationId)).thenReturn(location1);

        ResponseEntity<LocationDetailDto> response = locationEndpoint.getLocationById(locationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(location1, response.getBody());
    }

    @Test
    void deleteLocationDeletesLocationWithGivenId() {
        Long locationId = 1L;

        ResponseEntity<Void> response = locationEndpoint.deleteLocation(locationId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(locationService, times(1)).deleteLocation(locationId);
    }

    @Test
    void searchLocationReturnsMatchingLocation() {
        LocationSearchDto locationSearchDto = new LocationSearchDto("Location1", "", "", "", "");

        when(locationService.search(locationSearchDto)).thenReturn(Stream.of(location1));

        ResponseEntity<Stream<LocationDetailDto>> response = locationEndpoint.search(locationSearchDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<LocationDetailDto> result = Objects.requireNonNull(response.getBody()).toList();
        assertEquals(1, result.size());
        assertEquals(location1, result.getFirst());
    }
}
