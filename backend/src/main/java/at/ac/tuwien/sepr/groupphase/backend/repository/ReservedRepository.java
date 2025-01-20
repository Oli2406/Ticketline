package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservedRepository extends JpaRepository<Reservation, Long> {

    /**
     * Find all reservations for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of reservations associated with the user
     */
    List<Reservation> findByUserId(Long userId);

}