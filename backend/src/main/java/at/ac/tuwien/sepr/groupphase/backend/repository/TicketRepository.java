package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find all tickets by the performance ID.
     *
     * @param performanceId the ID of the performance
     * @return a list of tickets associated with the performance
     */
    List<Ticket> findByPerformanceId(Long performanceId);

    /**
     * Check if a ticket exists for a specific seat and performance.
     *
     * @param performanceId the ID of the performance
     * @param rowNumber the row number of the seat
     * @param seatNumber the seat number
     * @return true if the ticket exists, false otherwise
     */
    boolean existsByPerformanceIdAndRowNumberAndSeatNumber(Long performanceId, Integer rowNumber, Integer seatNumber);

    /**
     * Check if there are any tickets for a specific performance.
     *
     * @param performanceId the ID of the performance
     * @return true if tickets exist for the performance, false otherwise
     */
    boolean existsByPerformanceId(Long performanceId);

    /**
     * Finds a ticket by its ticket ID.
     *
     * @return the ticket corresponding to the given ticket ID
     */
    Ticket findByTicketId(Long ticketId);

    /**
     * Finds a ticket by its ID and applies a pessimistic write lock to prevent
     * concurrent modifications.
     *
     * @param id the ID of the ticket to be retrieved
     * @return an {@code Optional} containing the ticket if it exists, or an empty
     *         {@code Optional} if no ticket with the specified ID exists
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.ticketId = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Long id);

    /**
     * Finds tickets by a list of ticket IDs with a pessimistic write lock.
     * This ensures that the selected tickets are locked for updates
     * until the end of the current transaction.
     *
     * @param ids the list of ticket IDs to fetch with a lock
     * @return a list of tickets corresponding to the provided IDs
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.ticketId IN :ids")
    List<Ticket> findByIdsWithLock(@Param("ids") List<Long> ids);
}
