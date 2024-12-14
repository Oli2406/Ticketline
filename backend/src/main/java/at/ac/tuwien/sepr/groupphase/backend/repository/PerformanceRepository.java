package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    /**
     * Checks if a performance exists with the specified hall.
     *
     * @param hall the name of the hall to be checked
     * @return true if a performance exists with the specified hall, false otherwise
     */
    boolean existsByHall(String hall);

    /**
     * Checks if a performance with the specified name, location ID, and date exists.
     *
     * @param name       the name of the performance to check for existence
     * @param locationId the ID of the location where the performance is to be checked
     * @param date       the date of the performance to be checked
     * @return true if a performance with the specified name, location, and date exists, false otherwise
     */
    boolean existsByNameAndLocationIdAndDate(String name, Long locationId, LocalDateTime date);

    /**
     * Finds all performances assoicated with event {@code eventId}.
     *
     * @param eventId the event to find the performances by
     * @return list of performances associated with the event
     */
    @Query("SELECT p from Performance p WHERE p.performanceId IN (SELECT e.performanceIds from Event e WHERE e.eventId = :eventId)")
    List<Performance> findByEventId(Long eventId);
}
