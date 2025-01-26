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

    /**
     * Retrieves the top 10 events based on the percentage of tickets sold.
     * Results are sorted by sold percentage in descending order.
     *
     * @param year     the year to filter events
     * @param month    the month to filter events (1-12)
     * @param category the category to filter events
     * @return a list of object arrays, where each array contains:
     *         eventId (Long): ID of the event
     *         eventTitle (String): name of the event
     *         soldTickets (Long): number of tickets sold
     *         totalTickets (Long): total number of tickets available
     *         soldPercentage (Double): percentage of tickets sold
     */
    @Query(value = """
        SELECT
            ep.EVENT_ID AS eventId,
            e.TITLE AS eventTitle,
            SUM(CASE WHEN t.STATUS = 'SOLD' THEN 1 ELSE 0 END) AS soldTickets
        FROM
            EVENT_PERFORMANCE_IDS ep
        JOIN
            TICKET t ON ep.PERFORMANCE_ID = t.PERFORMANCE_ID
        JOIN
            EVENT e ON ep.EVENT_ID = e.EVENT_ID
        WHERE
            (:category IS NULL OR e.CATEGORY = :category)
            AND (
                (:year IS NULL OR :month IS NULL)
                OR
                (YEAR(e.DATE_FROM) = :year AND MONTH(e.DATE_FROM) = :month)
                OR
                (YEAR(e.DATE_TO) = :year AND MONTH(e.DATE_TO) = :month)
            )
        GROUP BY
            ep.EVENT_ID, e.TITLE
        ORDER BY
            soldTickets DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> findTop10EventsAsObjects(@Param("year") Integer year, @Param("month") Integer month, @Param("category") String category);

    /**
     * Retrieves all unique categories from the events table.
     *
     * @return a list of distinct categories found in the events table
     */
    @Query(value = "SELECT DISTINCT e.CATEGORY FROM EVENT e", nativeQuery = true)
    List<String> findAllCategories();
}