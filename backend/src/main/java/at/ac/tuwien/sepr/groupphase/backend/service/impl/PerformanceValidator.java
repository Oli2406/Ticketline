package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PerformanceValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceValidator.class);

    private final PerformanceRepository performanceRepository;

    public PerformanceValidator(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public void validatePerformance(PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException {
        LOGGER.trace("Validating performance: {}", performanceCreateDto);
        List<String> validationErrors = new ArrayList<>();

        if (performanceCreateDto.getName() == null || performanceCreateDto.getName().trim().isEmpty()) {
            validationErrors.add("Performance name is required");
        } else if (performanceCreateDto.getName().length() > 255) {
            validationErrors.add("Performance name must be less than 255 characters");
        }

        if (performanceCreateDto.getArtistId() == null) {
            validationErrors.add("Artist ID is required");
        }

        if (performanceCreateDto.getLocationId() == null) {
            validationErrors.add("Location ID is required");
        }

        if (performanceCreateDto.getDate() == null) {
            validationErrors.add("Performance date is required");
        }

        if (performanceCreateDto.getPrice() == null || performanceCreateDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            validationErrors.add("Performance price must be greater than 0");
        }

        if (performanceCreateDto.getTicketNumber() == null || performanceCreateDto.getTicketNumber() <= 0) {
            validationErrors.add("Number of tickets must be greater than 0");
        }

        if (performanceCreateDto.getHall() == null || performanceCreateDto.getHall().trim().isEmpty()) {
            validationErrors.add("Hall is required");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Performance validation failed with errors: {}", validationErrors);
            throw new ValidationException("Performance validation failed", validationErrors);
        }
        checkPerformanceUnique(performanceCreateDto.getName(), performanceCreateDto.getLocationId(), performanceCreateDto.getDate());

        LOGGER.info("Performance validation passed for: {}", performanceCreateDto);
    }

    public void checkPerformanceUnique(String name, Long locationId, LocalDate date) throws ConflictException {
        if (performanceRepository.existsByNameAndLocationIdAndDate(name, locationId, date)) {
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("Performance with the name '" + name + "' already exists at this location on the given date");
            LOGGER.warn("Conflict detected for performance: {}, locationId: {}, date: {}", name, locationId, date);
            throw new ConflictException("Performance creation conflict detected", conflictErrors);
        }
    }

}
