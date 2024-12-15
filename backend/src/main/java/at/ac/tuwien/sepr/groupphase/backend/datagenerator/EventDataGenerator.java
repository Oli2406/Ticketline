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

        for (int i = 1; i <= 10; i++) {
            LocalDate dateFrom = getRandomFutureDate();
            LocalDate dateTo = dateFrom.plusDays(random.nextInt(3) + 1);

            List<Performance> eventPerformances = performances.stream()
                .filter(performance -> {
                    LocalDate performanceDate = performance.getDate().toLocalDate();
                    return !performanceDate.isBefore(dateFrom) && !performanceDate.isAfter(dateTo);
                })
                .limit(random.nextInt(3) + 1)
                .toList();

            if (eventPerformances.isEmpty()) {
                i--;
                continue;
            }

            List<Long> performanceIds = eventPerformances.stream()
                .map(Performance::getPerformanceId)
                .collect(Collectors.toList());

            Event event = new Event(
                "Event " + i,
                "Description for Event " + i,
                dateFrom,
                dateTo,
                getRandomCategory(),
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
        String[] categories = {"Music", "Theater", "Dance", "Comedy", "Film"};
        return categories[random.nextInt(categories.length)];
    }
}
