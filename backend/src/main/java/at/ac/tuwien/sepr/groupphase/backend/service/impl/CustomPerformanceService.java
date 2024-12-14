package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomPerformanceService implements PerformanceService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceRepository performanceRepository;
    private final PerformanceValidator performanceValidator;
    private final SearchPerformanceRepository searchPerformanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;
    private final PerformanceMapper performanceMapper;

    public CustomPerformanceService(PerformanceRepository performanceRepository, PerformanceValidator performanceValidator, SearchPerformanceRepository searchPerformanceRepository,
                                    ArtistRepository artistRepository, LocationRepository locationRepository, PerformanceMapper performanceMapper) {
        this.performanceRepository = performanceRepository;
        this.performanceValidator = performanceValidator;
        this.searchPerformanceRepository = searchPerformanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
        this.performanceMapper = performanceMapper;
    }

    @Override
    public PerformanceDetailDto createPerformance(PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException {
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
            location,
            performanceCreateDto.getDuration()
        );
        performance = performanceRepository.save(performance);
        logger.debug("Saved performance to database: {}", performance);
        return new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall(),
            artistRepository.findArtistByArtistId(performance.getArtistId()), locationRepository.findByLocationId(performance.getLocationId()), performance.getDuration());
    }

    @Override
    public List<PerformanceDetailDto> getAllPerformances() {
        logger.info("Fetching all performances");
        List<PerformanceDetailDto> performances = performanceRepository.findAll().stream()
            .map(performance -> new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
                performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall(),
                artistRepository.findArtistByArtistId(performance.getArtistId()), locationRepository.findByLocationId(performance.getLocationId()), performance.getDuration()))
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
            artistRepository.findArtistByArtistId(performance.getArtistId()), locationRepository.findByLocationId(performance.getLocationId()), performance.getDuration());
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

    @Override
    public Stream<PerformanceDetailDto> search(PerformanceSearchDto dto) {
        logger.info("Searching performances with data: {}", dto);
        var query = performanceRepository.findAll().stream();

        if (dto.getDate() != null) {
            query = query.filter(performance -> {
                LocalDateTime performanceEndDate = performance.getDate().plusMinutes(performance.getDuration());
                LocalDateTime dtoDate = dto.getDate();
                return !dtoDate.isBefore(performance.getDate()) && !dtoDate.isAfter(performanceEndDate);
            });
        }
        if (dto.getPrice() != null) {
            query = query.filter(performance -> {
                BigDecimal price = performance.getPrice();
                BigDecimal dtoPrice = dto.getPrice();
                BigDecimal lowerBound = dtoPrice.subtract(BigDecimal.TEN);
                BigDecimal upperBound = dtoPrice.add(BigDecimal.TEN);

                return price.compareTo(lowerBound) >= 0 && price.compareTo(upperBound) <= 0;
            });
        }
        if (dto.getHall() != null) {
            query = query.filter(performance -> performance.getHall().toLowerCase().contains(dto.getHall().toLowerCase()));
        }

        return query.map(performance -> {
            Artist artist = artistRepository.findArtistByArtistId(performance.getArtistId());
            Location location = locationRepository.findByLocationId(performance.getLocationId());
            return performanceMapper.toPerformanceDetailDto(performance, artist, location);
        });
    }

    @Override
    public List<PerformanceDetailDto> getByEventId(Long id) {
        logger.info("Getting performances by event id: {}", id);
        List<Performance> result = performanceRepository.findByEventId(id);

        return result.stream()
            .map(performance -> {
                Artist artist = artistRepository.findArtistByArtistId(performance.getArtistId());
                Location location = locationRepository.findByLocationId(performance.getLocationId());
                return performanceMapper.toPerformanceDetailDto(performance, artist, location);
            })
            .collect(Collectors.toList());
    }
}