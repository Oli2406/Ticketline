package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomLocationService implements LocationService {

    private final LocationRepository locationRepository;

    public CustomLocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public LocationDetailDto createOrUpdateLocation(LocationCreateDto locationCreateDto) {
        Location location = new Location(
            locationCreateDto.getName(),
            locationCreateDto.getStreet(),
            locationCreateDto.getCity(),
            locationCreateDto.getPostalCode(),
            locationCreateDto.getCountry()
        );
        location = locationRepository.save(location);
        return new LocationDetailDto(location.getId(), location.getName(), location.getStreet(), location.getCity(), location.getPostalCode(), location.getCountry());
    }

    @Override
    public List<LocationDetailDto> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(location -> new LocationDetailDto(location.getId(), location.getName(), location.getStreet(), location.getCity(), location.getPostalCode(), location.getCountry()))
            .collect(Collectors.toList());
    }

    @Override
    public LocationDetailDto getLocationById(Long id) {
        Location location = locationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Location not found"));
        return new LocationDetailDto(location.getId(), location.getName(), location.getStreet(), location.getCity(), location.getPostalCode(), location.getCountry());
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
