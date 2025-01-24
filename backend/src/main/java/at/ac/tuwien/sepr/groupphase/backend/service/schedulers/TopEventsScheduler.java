package at.ac.tuwien.sepr.groupphase.backend.service.schedulers;

import at.ac.tuwien.sepr.groupphase.backend.entity.TopEvents;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TopEventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class TopEventsScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopEventsScheduler.class);

    @Autowired
    private EventRepository eventPerformanceRepository;

    @Autowired
    private TopEventsRepository topEventsRepository;

    @Scheduled(cron = "0 25 15 * * *")
    @Transactional
    public void updateTopTenEvents() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        LOGGER.info("Starting update of top ten events. Current date: {}, Year: {}, Month: {}", now, currentYear, currentMonth);

        // Fetch all categories from the event performance repository
        List<String> categories = eventPerformanceRepository.findAllCategories();
        LOGGER.info("Found {} categories: {}", categories.size(), categories);

        for (String category : categories) {
            LOGGER.info("Processing category: {}", category);

            for (int month = 1; month <= 12; month++) {
                int year = currentMonth < month ? currentYear - 1 : currentYear;

                // Fetch top 10 events for the category, year, and month
                List<Object[]> topEvents = eventPerformanceRepository.findTop10EventsAsObjects(year, month, category);

                // Delete old records for this category, year, and month
                topEventsRepository.deleteByCategoryAndYearAndMonth(category, year, month);

                // Save new top events
                for (Object[] eventData : topEvents) {
                    TopEvents event = new TopEvents();
                    event.setEventId((Long) eventData[0]);
                    event.setEventTitle((String) eventData[1]);
                    event.setSoldTickets((Long) eventData[2]);
                    event.setCategory(category);
                    event.setYear(year);
                    event.setMonth(month);
                    event.setUpdateDate(now);

                    topEventsRepository.save(event);
                    LOGGER.debug("Saved top event: {} - {} ({} tickets)", event.getEventId(), event.getEventTitle(), event.getSoldTickets());
                }
            }
        }
        LOGGER.info("Finished updating top ten events.");
    }
}