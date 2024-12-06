package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
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

    public CustomPerformanceService(PerformanceRepository performanceRepository, PerformanceValidator performanceValidator) {
        this.performanceRepository = performanceRepository;
        this.performanceValidator = performanceValidator;
    }

    @Override
    public PerformanceDetailDto createOrUpdatePerformance(PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException {
        logger.info("Creating or updating performance: {}", performanceCreateDto);
        performanceValidator.validatePerformance(performanceCreateDto);
        Performance performance = new Performance(
            performanceCreateDto.getName(),
            performanceCreateDto.getArtistId(),
            performanceCreateDto.getLocationId(),
            performanceCreateDto.getDate(),
            performanceCreateDto.getTicketNumber(),
            performanceCreateDto.getHall()
        );
        performance = performanceRepository.save(performance);
        logger.debug("Saved performance to database: {}", performance);
        return new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getTicketNumber(), performance.getHall());
    }

    @Override
    public List<PerformanceDetailDto> getAllPerformances() {
        logger.info("Fetching all performances");
        List<PerformanceDetailDto> performances = performanceRepository.findAll().stream()
            .map(performance -> new PerformanceDetailDto(performance.getPerformanceId(), performance.getName(), performance.getArtistId(),
                performance.getLocationId(), performance.getDate(), performance.getTicketNumber(), performance.getHall()))
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
            performance.getLocationId(), performance.getDate(), performance.getTicketNumber(), performance.getHall());
    }

    @Override
    public void deletePerformance(Long id) {
        logger.info("Deleting performance with ID: {}", id);
        performanceRepository.deleteById(id);
        logger.debug("Deleted performance with ID: {}", id);
    }
}
