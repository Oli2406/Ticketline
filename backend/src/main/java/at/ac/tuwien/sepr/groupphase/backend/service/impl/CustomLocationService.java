package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomLocationService implements LocationService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationRepository locationRepository;
    private final LocationValidator locationValidator;
    private final LocationMapper locationMapper;


    public CustomLocationService(LocationRepository locationRepository, LocationValidator locationValidator, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationValidator = locationValidator;
        this.locationMapper = locationMapper;
    }

    @Override
    public LocationDetailDto createLocation(LocationCreateDto locationCreateDto) throws ValidationException, ConflictException {
        logger.info("Creating or updating location: {}", locationCreateDto);
        locationValidator.validateLocation(locationCreateDto);
        Location location = new Location(
            locationCreateDto.getName(),
            locationCreateDto.getStreet(),
            locationCreateDto.getCity(),
            locationCreateDto.getPostalCode(),
            locationCreateDto.getCountry()
        );
        location = locationRepository.save(location);
        logger.debug("Saved location to database: {}", location);
        return new LocationDetailDto(location.getLocationId(), location.getName(), location.getStreet(), location.getCity(), location.getPostalCode(), location.getCountry());
    }

    @Override
    public List<LocationDetailDto> getAllLocations() {
        logger.info("Fetching all locations");
        List<LocationDetailDto> locations = locationRepository.findAll().stream()
            .map(location -> new LocationDetailDto(location.getLocationId(), location.getName(), location.getStreet(), location.getCity(), location.getPostalCode(), location.getCountry()))
            .collect(Collectors.toList());
        logger.debug("Fetched {} locations: {}", locations.size(), locations);
        return locations;
    }

    @Override
    public LocationDetailDto getLocationById(Long id) {
        logger.info("Fetching location with ID: {}", id);
        Location location = locationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Location not found"));
        logger.debug("Fetched location: {}", location);
        return new LocationDetailDto(location.getLocationId(), location.getName(), location.getStreet(), location.getCity(), location.getPostalCode(), location.getCountry());
    }

    @Override
    public void deleteLocation(Long id) {
        logger.info("Deleting location with ID: {}", id);
        locationRepository.deleteById(id);
        logger.debug("Deleted location with ID: {}", id);
    }

    @Override
    public Stream<LocationDetailDto> search(LocationSearchDto dto) {
        logger.info("Searching artists with data: {}", dto);
        var query = locationRepository.findAll().stream();
        if (dto.getName() != null) {
            query = query.filter(location -> location.getName().toLowerCase().contains(dto.getName().toLowerCase()));
        }
        if (dto.getStreet() != null) {
            query = query.filter(location -> location.getStreet().toLowerCase().contains(dto.getStreet().toLowerCase()));
        }
        if (dto.getCity() != null) {
            query = query.filter(location -> location.getCity().toLowerCase().contains(dto.getCity().toLowerCase()));
        }
        if (dto.getCountry() != null) {
            query = query.filter(location -> location.getCountry().toLowerCase().contains(dto.getCountry().toLowerCase()));
        }
        if (dto.getPostalCode() != null) {
            query = query.filter(location -> location.getPostalCode().toLowerCase().contains(dto.getPostalCode().toLowerCase()));
        }

        return query.map(this.locationMapper::locationToLocationDetailDto);
    }
}
