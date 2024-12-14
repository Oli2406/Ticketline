package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping(LocationEndpoint.BASE_PATH)
public class LocationEndpoint {

    public static final String BASE_PATH = "/api/v1/location";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;

    public LocationEndpoint(LocationService locationService) {
        this.locationService = locationService;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    public ResponseEntity<LocationDetailDto> createOrUpdateLocation(@RequestBody LocationCreateDto locationCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("Received request to create or update location: {}", locationCreateDto);
        LocationDetailDto createdLocation = locationService.createLocation(locationCreateDto);
        LOGGER.debug("Location created/updated successfully: {}", createdLocation);
        return ResponseEntity.ok(createdLocation);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<LocationDetailDto>> getAllLocations() {
        LOGGER.info("Fetching all locations");
        List<LocationDetailDto> locations = locationService.getAllLocations();
        LOGGER.debug("Fetched {} locations: {}", locations.size(), locations);
        return ResponseEntity.ok(locations);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<LocationDetailDto> getLocationById(@PathVariable Long id) {
        LOGGER.info("Fetching location with ID: {}", id);
        LocationDetailDto location = locationService.getLocationById(id);
        LOGGER.debug("Fetched location: {}", location);
        return ResponseEntity.ok(location);
    }

    @PermitAll
    @GetMapping("/search")
    public ResponseEntity<Stream<LocationDetailDto>> search(LocationSearchDto dto) {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("request parameters: {}", dto.toString());
        Stream<LocationDetailDto> result = locationService.search(dto);
        return ResponseEntity.ok(result);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        LOGGER.info("Deleting location with ID: {}", id);
        locationService.deleteLocation(id);
        LOGGER.debug("Location with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}