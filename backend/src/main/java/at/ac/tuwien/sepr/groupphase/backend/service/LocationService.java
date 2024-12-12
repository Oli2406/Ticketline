package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

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
     * @throws ValidationException if the input data fails validation (e.g., missing or invalid fields)
     * @throws ConflictException if there are conflicts, such as an artist with the same name already existing
     */
    LocationDetailDto createLocation(LocationCreateDto locationCreateDto) throws ValidationException, ConflictException;

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
