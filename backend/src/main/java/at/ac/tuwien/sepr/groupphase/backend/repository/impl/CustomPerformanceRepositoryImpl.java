package at.ac.tuwien.sepr.groupphase.backend.repository.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.SearchPerformanceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomPerformanceRepositoryImpl implements SearchPerformanceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public List<PerformanceDetailDto> findByAdvancedSearch(String query) {

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] terms = query.split("\\s+");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PerformanceDetailDto> cq = cb.createQuery(PerformanceDetailDto.class);

        Root<Event> event = cq.from(Event.class);

        Join<Event, Long> eventPerformanceJoin = event.join("performanceIds", JoinType.INNER);

        Root<Performance> performance = cq.from(Performance.class);

        Predicate joinCondition = cb.equal(eventPerformanceJoin, performance.get("performanceId"));

        Join<Performance, Artist> artist = performance.join("artist", JoinType.INNER);
        Join<Performance, Location> location = performance.join("location", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        for (String term : terms) {
            if (term.contains(".")) {
                LocalDate date = parseDate(term);
                if (date != null) {
                    predicates.add(cb.equal(performance.get("date"), date));
                    continue;
                }
            }

            String likePattern = "%" + term.toLowerCase() + "%";
            predicates.add(cb.or(
                cb.like(cb.lower(event.get("title")), likePattern),
                cb.like(cb.lower(event.get("description")), likePattern),
                cb.like(cb.lower(event.get("category")), likePattern),
                cb.like(cb.lower(performance.get("name")), likePattern),
                cb.like(cb.lower(performance.get("hall")), likePattern),
                cb.like(cb.lower(artist.get("firstName")), likePattern),
                cb.like(cb.lower(artist.get("lastName")), likePattern),
                cb.like(cb.lower(artist.get("artistName")), likePattern),
                cb.like(cb.lower(location.get("name")), likePattern),
                cb.like(cb.lower(location.get("street")), likePattern),
                cb.like(cb.lower(location.get("city")), likePattern),
                cb.like(cb.lower(location.get("country")), likePattern),
                cb.like(cb.lower(location.get("postalCode")), likePattern)
            ));
        }

        if (predicates.isEmpty()) {
            return new ArrayList<>();
        }

        Predicate combinedPredicate = cb.and(joinCondition, cb.and(predicates.toArray(new Predicate[0])));

        cq.select(cb.construct(
            PerformanceDetailDto.class,
            performance.get("performanceId"),
            performance.get("name"),
            performance.get("artistId"),
            performance.get("locationId"),
            performance.get("date"),
            performance.get("price"),
            performance.get("ticketNumber"),
            performance.get("hall"),
            performance.get("artist"),
            performance.get("location")
        )).distinct(true).where(combinedPredicate);

        List<PerformanceDetailDto> results = entityManager.createQuery(cq).getResultList();
        LOGGER.info("Fetched {} PerformanceDetailDto results.", results.size());
        LOGGER.info(results.toString());

        return results;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.warn("Failed to parse date: {}", dateStr);
            return null;
        }
    }
}
