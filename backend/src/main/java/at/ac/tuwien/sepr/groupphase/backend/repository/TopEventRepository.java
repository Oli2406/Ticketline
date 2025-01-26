package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.TopEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopEventRepository extends JpaRepository<TopEvent, Long> {

    /**
     * Finds a list of {@link TopEvent} entities filtered by category, month, and year.
     * Matches null parameters with null fields in the database.
     *
     * @param category the event category to filter by, or null to match null categories.
     * @param month    the event month to filter by, or null to match null months.
     * @param year     the event year to filter by, or null to match null years.
     * @return a list of {@link TopEvent} entities matching the given criteria.
     */
    @Query("SELECT t FROM TopEvent t "
        + "WHERE (:category IS NULL AND t.category IS NULL OR :category IS NOT NULL AND t.category = :category) "
        + "AND (:month IS NULL AND t.month IS NULL OR :month IS NOT NULL AND t.month = :month) "
        + "AND (:year IS NULL AND t.year IS NULL OR :year IS NOT NULL AND t.year = :year)")
    List<TopEvent> findByCategoryMonthYear(
        @Param("category") String category,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}
