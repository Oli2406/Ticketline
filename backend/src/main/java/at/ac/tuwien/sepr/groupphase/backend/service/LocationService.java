package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;

import java.util.List;

/**
 * Service interface for managing locations.
 */
public interface LocationService {

    /**
     * Creates or updates a location.
     *
     * @param locationCreateDto the data for creating or updating the location
     * @return the detailed representation of the created or updated location
     */
    LocationDetailDto createOrUpdateLocation(LocationCreateDto locationCreateDto);

    /**
     * Retrieves all locations.
     *
     * @return a list of detailed representations of all locations
     */
    List<LocationDetailDto> getAllLocations();

    /**
     * Retrieves a specific location by its ID.
     *
     * @param id the ID of the location to retrieve
     * @return the detailed representation of the requested location
     */
    LocationDetailDto getLocationById(Long id);

    /**
     * Deletes a specific location by its ID.
     *
     * @param id the ID of the location to delete
     */
    void deleteLocation(Long id);
}
