package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.*;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.LocationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class LocationServiceImplTest {

    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationValidator locationValidator;

    @Mock
    private LocationMapper locationMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        locationService = new LocationServiceImpl(locationRepository, locationValidator,
            locationMapper);
    }

    @Test
    void createOrUpdateLocation_ShouldSaveLocation_WhenValidInput()
        throws ValidationException, ConflictException {
        LocationCreateDto dto = new LocationCreateDto("LocationName", "Street", "City", "12345",
            "Country");

        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location l = invocation.getArgument(0);
            l.setLocationId(1L);
            return l;
        });

        LocationDetailDto created = locationService.createLocation(dto);

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
        List<Location> locations = List.of(
            new Location("LocationName", "Street", "City", "12345", "Country"));
        when(locationRepository.findAll()).thenReturn(locations);

        List<LocationDetailDto> result = locationService.getAllLocations();

        assertFalse(result.isEmpty(), "Resulting location list should not be empty");
        assertEquals(1, result.size(), "Result list size should match");
        assertEquals("LocationName", result.getFirst().getName(), "Location name should match");

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void getLocationById_ShouldThrowException_WhenLocationNotFound() {
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> locationService.getLocationById(1L),
            "Should throw exception for non-existent ID");

        assertEquals("Location not found", exception.getMessage(),
            "Exception message should match");
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void searchLocationByNameReturnsMatchingEvent() {
        LocationDetailDto location1DetailDto = new LocationDetailDto(1L, "Matching Name", "Street1",
            "City1", "12345", "Country1");
        Location location1 = new Location("Matching Name", "Street1", "City1", "12345", "Country1");
        Location location2 = new Location("Name", "Street2", "City2", "12345", "Country2");
        when(locationRepository.findAll()).thenReturn(List.of(location1, location2));
        when(locationMapper.locationToLocationDetailDto(location1)).thenReturn(location1DetailDto);

        LocationSearchDto searchDto = new LocationSearchDto("Matching", null, null, null, null);

        List<LocationDetailDto> result = locationService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return only one event");
        verify(locationRepository, times(1)).findAll();
        verify(locationMapper, times(1)).locationToLocationDetailDto(location1);
        verify(locationMapper, never()).locationToLocationDetailDto(location2);
    }

    @Test
    void searchLocationByNameReturnsNoEventsWhenNoMatch() {
        Location location1 = new Location("Matching Name", "Street1", "City1", "12345", "Country1");
        Location location2 = new Location("Matching Name", "Street2", "City2", "12345", "Country2");
        when(locationRepository.findAll()).thenReturn(List.of(location1, location2));

        LocationSearchDto searchDto = new LocationSearchDto("Non-matching", null, null, null, null);

        List<LocationDetailDto> result = locationService.search(searchDto).toList();

        assertEquals(0, result.size(), "Should return no events");
        verify(locationRepository, times(1)).findAll();
        verify(locationMapper, never()).locationToLocationDetailDto(any(Location.class));
    }

    @Test
    void getLocationById_ShouldReturnLocation_WhenValidId() {
        Location location = new Location("LocationName", "Street", "City", "12345", "Country");
        location.setLocationId(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationMapper.locationToLocationDetailDto(location)).thenReturn(
            new LocationDetailDto(1L, "LocationName", "Street", "City", "12345", "Country")
        );

        LocationDetailDto result = locationService.getLocationById(1L);

        assertNotNull(result, "Location should not be null");
        assertEquals("LocationName", result.getName(), "Location name should match");

        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void deleteLocation_ShouldRemoveLocation_WhenValidId() {
        Long locationId = 1L;
        doNothing().when(locationRepository).deleteById(locationId);

        locationService.deleteLocation(locationId);

        verify(locationRepository, times(1)).deleteById(locationId);
    }

    @Test
    void deleteLocation_ShouldNotThrowError_WhenIdDoesNotExist() {
        Long nonExistentId = 999L;
        doNothing().when(locationRepository).deleteById(nonExistentId);

        locationService.deleteLocation(nonExistentId);

        verify(locationRepository, times(1)).deleteById(nonExistentId);
    }

    @Test
    void searchLocationByStreetReturnsMatchingLocation() {
        Location location = new Location("Name", "Matching Street", "City", "12345", "Country");
        when(locationRepository.findAll()).thenReturn(List.of(location));
        when(locationMapper.locationToLocationDetailDto(location)).thenReturn(
            new LocationDetailDto(1L, "Name", "Matching Street", "City", "12345", "Country")
        );

        LocationSearchDto searchDto = new LocationSearchDto(null, "Matching Street", null, null, null);
        List<LocationDetailDto> result = locationService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return one matching location");
        assertEquals("Matching Street", result.getFirst().getStreet(), "Street should match");

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void searchLocationByCityReturnsMatchingLocation() {
        Location location = new Location("Name", "Street", "Matching City", "12345", "Country");
        when(locationRepository.findAll()).thenReturn(List.of(location));
        when(locationMapper.locationToLocationDetailDto(location)).thenReturn(
            new LocationDetailDto(1L, "Name", "Street", "Matching City", "12345", "Country")
        );

        LocationSearchDto searchDto = new LocationSearchDto(null, null, "Matching City", null, null);
        List<LocationDetailDto> result = locationService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return one matching location");
        assertEquals("Matching City", result.getFirst().getCity(), "City should match");

        verify(locationRepository, times(1)).findAll();
    }
}
