package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventValidator.class);

    private final EventRepository eventRepository;

    public EventValidator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void validateEvent(EventCreateDto eventCreateDto) throws ValidationException, ConflictException {
        LOGGER.trace("Validating event: {}", eventCreateDto);
        List<String> validationErrors = new ArrayList<>();

        if (eventCreateDto.getTitle() == null || eventCreateDto.getTitle().trim().isEmpty()) {
            validationErrors.add("Event title is required");
        }

        if (eventCreateDto.getTitle().length() > 50) {
            validationErrors.add("Event title must be less than 50 characters");
        }

        if (eventCreateDto.getDescription() == null || eventCreateDto.getDescription().trim().isEmpty()) {
            validationErrors.add("Event description is required");
        }

        if (eventCreateDto.getDescription().length() > 255) {
            validationErrors.add("Event description must be less than 255 characters");
        }

        if (eventCreateDto.getCategory() == null || eventCreateDto.getCategory().trim().isEmpty()) {
            validationErrors.add("Event category is required");
        }

        if (eventCreateDto.getCategory().length() > 255) {
            validationErrors.add("Event category must be less than 255 characters");
        }

        if (eventCreateDto.getDateOfEvent() == null) {
            validationErrors.add("Event date is required");
        }

        if (eventCreateDto.getDateOfEvent().isBefore(LocalDate.now())) {
            validationErrors.add("Event date cannot be in the past");
        }

        if (eventCreateDto.getDuration() <= 0) {
            validationErrors.add("Event duration must be greater than 0");
        }

        if (eventCreateDto.getPerformanceIds() == null || eventCreateDto.getPerformanceIds().isEmpty()) {
            validationErrors.add("At least one performance id must be provided");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Event validation failed with errors: {}", validationErrors);
            throw new ValidationException("Event validation failed", validationErrors);
        }
        checkEventUnique(eventCreateDto.getTitle(), eventCreateDto.getDateOfEvent());

        LOGGER.info("Event validation passed for: {}", eventCreateDto);
    }

    public void checkEventUnique(String title, LocalDate dateOfEvent) throws ConflictException {
        if (eventRepository.existsByTitleAndDateOfEvent(title, dateOfEvent)) {
            List<String> conflictErrors = new ArrayList<>();
            conflictErrors.add("Event with the title '" + title + "' already exists on the date '" + dateOfEvent + "'");
            LOGGER.warn("Conflict detected for event: {}, dateOfEvent: {}", title, dateOfEvent);
            throw new ConflictException("Event creation conflict detected", conflictErrors);
        }
    }

}

