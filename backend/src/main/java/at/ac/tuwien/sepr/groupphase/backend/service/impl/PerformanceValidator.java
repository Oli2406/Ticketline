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
import java.time.LocalDateTime;
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
        }

        if (performanceCreateDto.getName().length() > 255) {
            validationErrors.add("Performance name must be less than 255 characters");
        }

        if (performanceCreateDto.getArtistId() == null) {
            validationErrors.add("Artist id is required");
        }

        if (performanceCreateDto.getLocationId() == null) {
            validationErrors.add("Location id is required");
        }

        if (performanceCreateDto.getDate() == null) {
            validationErrors.add("Performance date is required");
        } else if (performanceCreateDto.getDate().isBefore(LocalDateTime.now())) {
            validationErrors.add("Performance date cannot be in the past");
        }

        if (performanceCreateDto.getPrice() == null) {
            validationErrors.add("Price must not be null");
        }

        if (performanceCreateDto.getPrice() != null && !(performanceCreateDto.getPrice() instanceof BigDecimal)) {
            validationErrors.add("Price must be a valid number");
        }

        if (performanceCreateDto.getPrice() != null && performanceCreateDto.getPrice() instanceof BigDecimal) {
            BigDecimal price = performanceCreateDto.getPrice();

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                validationErrors.add("Price must be greater than 0");
            }

            if (price.compareTo(new BigDecimal("500")) > 0) {
                validationErrors.add("Price must not exceed 500");
            }
        }


        if (performanceCreateDto.getTicketNumber() == null || performanceCreateDto.getTicketNumber() <= 0) {
            validationErrors.add("Number of tickets must be greater than 0");
        }

        if (performanceCreateDto.getHall() == null || performanceCreateDto.getHall().trim().isEmpty()) {
            validationErrors.add("Hall is required");
        }

        if (performanceCreateDto.getHall().length() > 50) {
            validationErrors.add("Performance hall must be less than 50 characters");
        }

        if (performanceCreateDto.getDuration() <= 0) {
            validationErrors.add("Event duration must be greater than 0 minutes");
        }

        if (performanceCreateDto.getDuration() >= 600) {
            validationErrors.add("Event duration must be less than 600 minutes or 10 hours");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Performance validation failed with errors: {}", validationErrors);
            throw new ValidationException("Performance validation failed", validationErrors);
        }
        checkPerformanceUnique(performanceCreateDto.getName(), performanceCreateDto.getLocationId(), performanceCreateDto.getDate());

        LOGGER.info("Performance validation passed for: {}", performanceCreateDto);
    }

    public void checkPerformanceUnique(String name, Long locationId, LocalDateTime date) throws ConflictException {
        if (performanceRepository.existsByNameAndLocationIdAndDate(name, locationId, date)) {
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("Performance with the name '" + name + "' already exists at this location on the given date");
            LOGGER.warn("Conflict detected for performance: {}, locationId: {}, date: {}", name, locationId, date);
            throw new ConflictException("Performance creation conflict detected", conflictErrors);
        }
    }

}
