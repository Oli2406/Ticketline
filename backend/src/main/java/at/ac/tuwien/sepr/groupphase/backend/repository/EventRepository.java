package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Checks if an event with the specified title exists in the repository.
     *
     * @param title the title of the event to check for existence
     * @return true if an event with the specified title exists, false otherwise
     */
    boolean existsByTitle(String title);

    /**
     * Checks if an event with the specified title and date of event exists in the repository.
     *
     * @param title the title of the event to check for
     * @param dateOfEvent the date of the event to check for
     * @return true if an event with the given title and date exists, false otherwise
     */
    boolean existsByTitleAndDateOfEvent(String title, LocalDate dateOfEvent);
}
