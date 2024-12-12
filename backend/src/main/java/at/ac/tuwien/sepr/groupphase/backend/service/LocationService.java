package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;
import java.util.stream.Stream;

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
     * @throws ConflictException   if there are conflicts, such as an artist with the same name already existing
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

    /**
     * Search for locations in the persistent data store matching all provided fields.
     * The name, street, city and country are considered a match, if the search string is a substring of the field in event.
     *
     * @param dto the search parameters to use in filtering.
     * @return the locations where the given fields match.
     */
    Stream<LocationDetailDto> search(LocationSearchDto dto);
}
