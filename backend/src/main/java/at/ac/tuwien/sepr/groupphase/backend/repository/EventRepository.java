package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
     * @param title    the title of the event to check for
     * @param dateFrom the start date of the event to check for
     * @param dateTo   the end date of the event to check for
     * @return true if an event with the given title and date exists, false otherwise
     */
    boolean existsByTitleAndDateFromAndDateTo(String title, LocalDate dateFrom, LocalDate dateTo);


    /**
     * Returns a list of all Events where an assigned performance includes the artist
     * with {@code artistId}.
     *
     * @param artistId the artist that is searched in performances
     * @return list of events
     */
    @Query("SELECT e FROM Event e "
        + "JOIN Performance p ON p.performanceId IN elements(e.performanceIds) "
        + "WHERE p.artistId = :artistId")
    List<Event> findEventsByArtistId(@Param("artistId") Long artistId);

}
