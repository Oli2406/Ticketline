package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Profile("generateData")
@DependsOn("performanceDataGenerator")
public class EventDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDataGenerator.class);

    private final EventRepository eventRepository;
    private final PerformanceRepository performanceRepository;

    private final Random random = new Random();

    public EventDataGenerator(EventRepository eventRepository, PerformanceRepository performanceRepository) {
        this.eventRepository = eventRepository;
        this.performanceRepository = performanceRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        if (eventRepository.count() > 0) {
            LOGGER.info("Events already exist in the database. Skipping initial data generation.");
            return;
        }

        List<Performance> performances = performanceRepository.findAll();

        if (performances.isEmpty()) {
            LOGGER.warn("No performances available to create events.");
            return;
        }

        List<Event> events = createEvents(performances);
        eventRepository.saveAll(events);
        LOGGER.info("Generated {} events.", events.size());
    }

    private List<Event> createEvents(List<Performance> performances) {
        List<Event> events = new ArrayList<>();

        String[] eventTitleFormats = {
            "The Ultimate %s Festival",
            "%s: A Weekend to Remember",
            "An Extravaganza of %s",
            "%s Festival at Its Best",
            "Discover the World of %s",
            "Exclusive %s Showcase",
            "The Spectacular %s Event",
            "Experience the Best of %s",
            "A Celebration of %s",
            "The Grand %s Affair",
            "Unleashing the Magic of %s",
            "The %s Experience",
            "Dive Deep into %s",
            "A %s Odyssey",
            "The Pinnacle of %s",
            "Feast of %s Wonders",
            "The Legendary %s Gathering",
            "Journey Through %s",
            "The Artistry of %s",
            "%s: A Cultural Delight",
            "The %s Revolution",
            "Masters of %s",
            "Celebrate %s in Style",
            "The %s Extravaganza Continues",
            "Vibrant %s Adventures",
            "The Essence of %s",
            "A World-Class %s Experience",
            "The %s Gala",
            "Unforgettable %s Nights",
            "The %s Spectacle"
        };

        String[] eventDescriptionFormats = {
            "Get ready for an amazing weekend with '%s' and others, featuring renowned artists at the stunning %s. This extraordinary event is filled with energy and excitement.",
            "Step into a world of wonder with '%s' and others, an unforgettable event with various artists. Hosted at %s, this experience will captivate you.",
            "Don't miss '%s' and others at %s! This event promises unmatched entertainment and a lively atmosphere.",
            "'%s' and others are your ticket to an enchanting experience. Held at %s, immerse yourself in the magic.",
            "Mark your calendar for '%s', and others, a sensational event with multiple artists. Happening at %s, this is entertainment at its finest.",
            "Prepare to be amazed by '%s' and others at the incredible %s. This event will be a night to remember.",
            "Join us for an unforgettable evening with '%s' and others at the spectacular %s. Get ready for a show like no other!",
            "Experience the thrill of '%s' and others live at %s! This is one event you won't want to miss.",
            "Immerse yourself in the sounds of '%s' and others at the iconic %s. It's a celebration of music and entertainment.",
            "Get your tickets now for '%s' and others at the renowned %s. This event will leave you breathless."
        };

        for (int i = 0; i < performances.size(); i += 3) { // Increment by 3 to get groups of 3
            List<Performance> selectedPerformances = performances.subList(i, Math.min(i + 3, performances.size()));

            // Handle the case where there are less than 3 performances left
            String performance1 = !selectedPerformances.isEmpty() ? selectedPerformances.get(0).getName() : "Amazing Act";
            String performance2 = selectedPerformances.size() > 1 ? selectedPerformances.get(1).getName() : "Another Great Show";
            String performance3 = selectedPerformances.size() > 2 ? selectedPerformances.get(2).getName() : "Fantastic Performance";
            String location = !selectedPerformances.isEmpty() ? selectedPerformances.get(0).getLocation() : "Our Main Stage";

            // Determine date range based on selected performances
            LocalDate minDate = selectedPerformances.stream()
                .map(p -> p.getDate().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(getRandomFutureDate());
            LocalDate maxDate = selectedPerformances.stream()
                .map(p -> p.getDate().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(getRandomFutureDate());
            LocalDate dateFrom = minDate.minusDays(2);
            LocalDate dateTo = maxDate.plusDays(2);

            String titleFormat = eventTitleFormats[random.nextInt(eventTitleFormats.length)];
            String descriptionFormat = eventDescriptionFormats[random.nextInt(eventDescriptionFormats.length)];

            String category = getRandomCategory();

            // Generate title and description with the selected formats
            String title = String.format(titleFormat, category); // Use category in title
            String description = String.format(
                descriptionFormat,
                performance1,
                location,
                dateFrom,
                dateTo
            );

            // Collect performance IDs
            List<Long> performanceIds = selectedPerformances.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

            Event event = new Event(
                title,
                description,
                dateFrom,
                dateTo,
                category,
                performanceIds
            );

            events.add(event);
        }

        return events;
    }

    private LocalDate getRandomFutureDate() {
        return LocalDate.of(2023, 3, 1).plusDays(random.nextInt(365));
    }

    private String getRandomCategory() {
        String[] categories = {
            "Music",
            "Theater",
            "Dance",
            "Comedy",
            "Film",
            "Circus",
            "Jazz",
            "Rock",
            "Classical",
            "Pop",
            "Folk",
            "Hip-hop",
        };
        return categories[random.nextInt(categories.length)];
    }
}