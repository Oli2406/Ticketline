package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
