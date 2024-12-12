package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
     * @param name the name of the performance to check for existence
     * @param locationId the ID of the location where the performance is to be checked
     * @param date the date of the performance to be checked
     * @return true if a performance with the specified name, location, and date exists, false otherwise
     */
    boolean existsByNameAndLocationIdAndDate(String name, Long locationId, LocalDateTime date);

}
