package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SearchPerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomPerformanceService implements PerformanceService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceRepository performanceRepository;
    private final PerformanceValidator performanceValidator;
    private final SearchPerformanceRepository searchPerformanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;

    public CustomPerformanceService(PerformanceRepository performanceRepository, PerformanceValidator performanceValidator, SearchPerformanceRepository searchPerformanceRepository,
                                    ArtistRepository artistRepository, LocationRepository locationRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceValidator = performanceValidator;
        this.searchPerformanceRepository = searchPerformanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public PerformanceDetailDto createOrUpdatePerformance(PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException {
        logger.info("Creating or updating performance: {}", performanceCreateDto);
        performanceValidator.validatePerformance(performanceCreateDto);
        Artist artist = artistRepository.findArtistByArtistId(performanceCreateDto.getArtistId());
        Location location = locationRepository.findByLocationId(performanceCreateDto.getLocationId());
        Performance performance = new Performance(
            performanceCreateDto.getName(),
            performanceCreateDto.getArtistId(),
            performanceCreateDto.getLocationId(),
            performanceCreateDto.getDate(),
            performanceCreateDto.getPrice(),
            performanceCreateDto.getTicketNumber(),
            performanceCreateDto.getHall(),
            artist,
            location
        );
        performance = performanceRepository.save(performance);
        logger.debug("Saved performance to database: {}", performance);
        return new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall(),
            artistRepository.findArtistByArtistId(performance.getArtistId()), locationRepository.findByLocationId(performance.getLocationId()));
    }

    @Override
    public List<PerformanceDetailDto> getAllPerformances() {
        logger.info("Fetching all performances");
        List<PerformanceDetailDto> performances = performanceRepository.findAll().stream()
            .map(performance -> new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
                performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall(),
                artistRepository.findArtistByArtistId(performance.getArtistId()), locationRepository.findByLocationId(performance.getLocationId())))
            .collect(Collectors.toList());
        logger.debug("Fetched {} performances: {}", performances.size(), performances);
        return performances;
    }

    @Override
    public PerformanceDetailDto getPerformanceById(Long id) {
        logger.info("Fetching performance with ID: {}", id);
        Performance performance = performanceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Performance not found"));
        logger.debug("Fetched performance: {}", performance);
        return new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall(),
            artistRepository.findArtistByArtistId(performance.getArtistId()), locationRepository.findByLocationId(performance.getLocationId()));
    }

    @Override
    public void deletePerformance(Long id) {
        logger.info("Deleting performance with ID: {}", id);
        performanceRepository.deleteById(id);
        logger.debug("Deleted performance with ID: {}", id);
    }

    public List<PerformanceDetailDto> performAdvancedSearch(String term) {
        return searchPerformanceRepository.findByAdvancedSearch(term);
    }
}