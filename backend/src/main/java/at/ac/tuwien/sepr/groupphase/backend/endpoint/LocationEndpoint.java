package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
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
import org.springframework.web.bind.annotation.DeleteMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationService locationService;

    public LocationEndpoint(LocationService locationService) {
        this.locationService = locationService;
    }

    @PutMapping
    public ResponseEntity<LocationDetailDto> createOrUpdateLocation(@RequestBody LocationCreateDto locationCreateDto) {
        logger.info("Received request to create or update location: {}", locationCreateDto);
        LocationDetailDto createdLocation = locationService.createOrUpdateLocation(locationCreateDto);
        logger.debug("Location created/updated successfully: {}", createdLocation);
        return ResponseEntity.ok(createdLocation);
    }

    @GetMapping
    public ResponseEntity<List<LocationDetailDto>> getAllLocations() {
        logger.info("Fetching all locations");
        List<LocationDetailDto> locations = locationService.getAllLocations();
        logger.debug("Fetched {} locations: {}", locations.size(), locations);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDetailDto> getLocationById(@PathVariable Long id) {
        logger.info("Fetching location with ID: {}", id);
        LocationDetailDto location = locationService.getLocationById(id);
        logger.debug("Fetched location: {}", location);
        return ResponseEntity.ok(location);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        logger.info("Deleting location with ID: {}", id);
        locationService.deleteLocation(id);
        logger.debug("Location with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}