package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SearchPerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;

import at.ac.tuwien.sepr.groupphase.backend.service.validators.PerformanceValidator;
import jakarta.transaction.Transactional;
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
public class PerformanceServiceImpl implements PerformanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final PerformanceRepository performanceRepository;
    private final PerformanceValidator performanceValidator;
    private final SearchPerformanceRepository searchPerformanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;
    private final TicketRepository ticketRepository;
    private final PerformanceMapper performanceMapper;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository,
                                  PerformanceValidator performanceValidator,
                                  SearchPerformanceRepository searchPerformanceRepository,
                                  ArtistRepository artistRepository, LocationRepository locationRepository,
                                  PerformanceMapper performanceMapper, TicketRepository ticketRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceValidator = performanceValidator;
        this.searchPerformanceRepository = searchPerformanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
        this.ticketRepository = ticketRepository;
        this.performanceMapper = performanceMapper;
    }

    @Override
    public PerformanceDetailDto createPerformance(PerformanceCreateDto performanceCreateDto)
        throws ValidationException, ConflictException {
        LOGGER.info("Creating or updating performance: {}", performanceCreateDto);
        performanceValidator.validatePerformance(performanceCreateDto);
        Artist artist = artistRepository.findArtistByArtistId(performanceCreateDto.getArtistId());
        Location location = locationRepository.findByLocationId(
            performanceCreateDto.getLocationId());
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
        LOGGER.debug("Saved performance to database: {}", performance);
        return new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(),
            performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(),
            performance.getTicketNumber(), performance.getHall(),
            artistRepository.findArtistByArtistId(performance.getArtistId()),
            locationRepository.findByLocationId(performance.getLocationId()),
            performance.getDuration());
    }

    @Override
    public List<PerformanceDetailDto> getAllPerformances() {
        LOGGER.info("Fetching all performances");
        List<PerformanceDetailDto> performances = performanceRepository.findAll().stream()
            .map(performance -> new PerformanceDetailDto(performance.getPerformanceId(),
                performance.getName(), performance.getArtistId(),
                performance.getLocationId(), performance.getDate(), performance.getPrice(),
                performance.getTicketNumber(), performance.getHall(),
                artistRepository.findArtistByArtistId(performance.getArtistId()),
                locationRepository.findByLocationId(performance.getLocationId()),
                performance.getDuration()))
            .collect(Collectors.toList());
        LOGGER.debug("Fetched {} performances: {}", performances.size(), performances);
        return performances;
    }

    @Override
    public PerformanceDetailDto getPerformanceById(Long id) {
        LOGGER.info("Fetching performance with ID: {}", id);
        Performance performance = performanceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Performance not found"));
        LOGGER.debug("Fetched performance: {}", performance);
        return new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(),
            performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(),
            performance.getTicketNumber(), performance.getHall(),
            artistRepository.findArtistByArtistId(performance.getArtistId()),
            locationRepository.findByLocationId(performance.getLocationId()),
            performance.getDuration());
    }

    @Override
    @Transactional
    public void deletePerformance(Long id) {
        LOGGER.info("Deleting performance with ID: {}", id);
        int deletedTickets = ticketRepository.deleteByPerformanceId(id);
        if (deletedTickets > 0) {
            LOGGER.info("Tickets were deleted: {}", deletedTickets);
        } else {
            LOGGER.info("Tickets were not deleted: {}", deletedTickets);
        }
        performanceRepository.deleteById(id);
        LOGGER.debug("Deleted performance with ID: {}", id);
    }

    public List<PerformanceDetailDto> performAdvancedSearch(String term) {
        return searchPerformanceRepository.findByAdvancedSearch(term);
    }

    @Override
    public Stream<PerformanceDetailDto> search(PerformanceSearchDto dto) {
        LOGGER.info("Searching performances with data: {}", dto);
        var query = performanceRepository.findAll().stream();

        if (dto.getDate() != null) {
            query = query.filter(performance -> {
                LocalDateTime performanceEndDate = performance.getDate()
                    .plusMinutes(performance.getDuration());
                LocalDateTime dtoDate = dto.getDate();
                return !dtoDate.isBefore(performance.getDate()) && !dtoDate.isAfter(
                    performanceEndDate);
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
            query = query.filter(performance -> performance.getHall().toLowerCase()
                .contains(dto.getHall().toLowerCase()));
        }

        return query.map(performance -> {
            Artist artist = artistRepository.findArtistByArtistId(performance.getArtistId());
            Location location = locationRepository.findByLocationId(performance.getLocationId());
            return performanceMapper.toPerformanceDetailDto(performance, artist, location);
        });
    }

    @Override
    public List<PerformanceDetailDto> getByEventId(Long id) {
        LOGGER.info("Getting performances by event id: {}", id);
        List<Performance> result = performanceRepository.findByEventId(id);

        return result.stream()
            .map(performance -> {
                Artist artist = artistRepository.findArtistByArtistId(performance.getArtistId());
                Location location = locationRepository.findByLocationId(
                    performance.getLocationId());
                return performanceMapper.toPerformanceDetailDto(performance, artist, location);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<PerformanceDetailDto> getByLocationId(Long id) {
        LOGGER.info("Getting performances by location id: {}", id);
        List<Performance> result = performanceRepository.findByLocationId(id);

        return result.stream()
            .map(performance -> {
                Artist artist = artistRepository.findArtistByArtistId(performance.getArtistId());
                Location location = locationRepository.findByLocationId(
                    performance.getLocationId());
                return performanceMapper.toPerformanceDetailDto(performance, artist, location);
            })
            .collect(Collectors.toList());
    }

    @Override
    public PerformanceDetailDto updateTicketNumberById(Long id, Long ticketNumber)
        throws NotFoundException {
        LOGGER.info(
            "Creating a new performance with an updated ticket number for performance ID: {}", id);

        // Fetch the existing performance
        Performance existingPerformance = performanceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Performance with ID " + id + " not found."));

        // Validate the ticket number
        if (ticketNumber < 0) {
            throw new IllegalArgumentException("Ticket number cannot be negative.");
        }

        existingPerformance.setTicketNumber(ticketNumber);

        // Save the new performance to the database
        Performance savedPerformance = performanceRepository.save(existingPerformance);

        // Retrieve associated artist and location for the DTO
        Artist artist = artistRepository.findArtistByArtistId(savedPerformance.getArtistId());
        Location location = locationRepository.findByLocationId(savedPerformance.getLocationId());

        // Map the new performance to a PerformanceDetailDto
        PerformanceDetailDto newPerformanceDto = performanceMapper.toPerformanceDetailDto(
            savedPerformance, artist, location);

        LOGGER.debug("New performance created successfully: {}", newPerformanceDto);

        // Return the DTO of the new performance
        return newPerformanceDto;
    }


}