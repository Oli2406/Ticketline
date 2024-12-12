package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CustomLocationServiceTest {

    private CustomLocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationValidator locationValidator;

    @Mock
    private LocationMapper locationMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        locationService = new CustomLocationService(locationRepository, locationValidator, locationMapper);
    }

    @Test
    void createOrUpdateLocation_ShouldSaveLocation_WhenValidInput() throws ValidationException, ConflictException {
        LocationCreateDto dto = new LocationCreateDto("LocationName", "Street", "City", "12345", "Country");

        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location l = invocation.getArgument(0);
            l.setLocationId(1L);
            return l;
        });

        LocationDetailDto created = locationService.createOrUpdateLocation(dto);

        assertNotNull(created, "Created location DTO should not be null");
        assertAll(
            () -> assertNotNull(created.getLocationId(), "Location ID should not be null"),
            () -> assertEquals(dto.getName(), created.getName(), "Name should match"),
            () -> assertEquals(dto.getStreet(), created.getStreet(), "Street should match")
        );

        verify(locationValidator, times(1)).validateLocation(dto);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void getAllLocations_ShouldReturnLocationList() {
        List<Location> locations = List.of(new Location("LocationName", "Street", "City", "12345", "Country"));
        when(locationRepository.findAll()).thenReturn(locations);

        List<LocationDetailDto> result = locationService.getAllLocations();

        assertFalse(result.isEmpty(), "Resulting location list should not be empty");
        assertEquals(1, result.size(), "Result list size should match");
        assertEquals("LocationName", result.get(0).getName(), "Location name should match");

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void getLocationById_ShouldThrowException_WhenLocationNotFound() {
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> locationService.getLocationById(1L),
            "Should throw exception for non-existent ID");

        assertEquals("Location not found", exception.getMessage(), "Exception message should match");
        verify(locationRepository, times(1)).findById(1L);
    }
}
