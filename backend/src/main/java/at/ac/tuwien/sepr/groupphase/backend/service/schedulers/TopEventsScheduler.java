package at.ac.tuwien.sepr.groupphase.backend.service.schedulers;

import at.ac.tuwien.sepr.groupphase.backend.entity.TopEvent;
import at.ac.tuwien.sepr.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TopEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class TopEventsScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopEventsScheduler.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TopEventRepository topEventsRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void updateTopTenEvents() {
        LocalDate now = LocalDate.now();
        YearMonth currentYearMonth = YearMonth.now();

        topEventsRepository.deleteAll();

        // Saving top events without month and without category
        List<Object[]> noFilter = eventRepository.findTop10EventsAsObjects(null, null, null);
        saveRetrievedEvents(noFilter, null, null, null);

        LOGGER.info("Starting update of top ten events. Current date: {}, YearMonth: {}", now, currentYearMonth);

        // Fetch all categories from the event performance repository
        List<String> categories = eventRepository.findAllCategories();
        LOGGER.info("Found {} categories: {}", categories.size(), categories);

        for (String category : categories) {
            // Saving top events for each category without month
            List<Object[]> noDate = eventRepository.findTop10EventsAsObjects(null, null, category);
            saveRetrievedEvents(noDate, category, null, null);

            // Saving top events with month and with category for the next 12 months
            for (int i = 0; i < 12; i++) {
                YearMonth targetYearMonth = currentYearMonth.plusMonths(i);
                int year = targetYearMonth.getYear();
                int month = targetYearMonth.getMonthValue();
                LOGGER.debug("Processing category: {} year: {} month: {}", category, year, month);

                List<Object[]> topEvents = eventRepository.findTop10EventsAsObjects(year, month, category);
                saveRetrievedEvents(topEvents, category, year, month);
            }
        }

        // Saving top events for each month without a category for the next 12 months
        for (int i = 0; i < 12; i++) {
            YearMonth targetYearMonth = currentYearMonth.plusMonths(i);
            int year = targetYearMonth.getYear();
            int month = targetYearMonth.getMonthValue();

            List<Object[]> noCategory = eventRepository.findTop10EventsAsObjects(year, month, null);
            saveRetrievedEvents(noCategory, null, year, month);
        }

        LOGGER.info("Finished updating top ten events.");
    }

    private void saveRetrievedEvents(List<Object[]>  topEvents, String category, Integer year, Integer month) {
        for (Object[] topEvent : topEvents) {
            TopEvent event = new TopEvent();
            event.setEventId((Long) topEvent[0]);
            event.setEventTitle((String) topEvent[1]);
            event.setSoldTickets((Long) topEvent[2]);
            event.setCategory(category);
            event.setYear(year);
            event.setMonth(month);
            event.setUpdateDate(LocalDate.now());

            topEventsRepository.save(event);
            LOGGER.debug("Saved top event: {} - {} ({} tickets)", event.getEventId(), event.getEventTitle(), event.getSoldTickets());
        }
    }
}