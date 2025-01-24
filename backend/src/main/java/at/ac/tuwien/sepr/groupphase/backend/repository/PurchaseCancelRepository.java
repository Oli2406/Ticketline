package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CancelPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseCancelRepository extends JpaRepository<CancelPurchase, Long>{

    /**
     * Find all purchases for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of purchases associated with the user
     */
    List<CancelPurchase> findByUserId(Long userId);

    /**
     * Check if a purchase exists by its ID.
     *
     * @param purchaseId the ID of the purchase
     * @return true if the purchase exists, false otherwise
     */
    boolean existsByPurchaseId(Long purchaseId);

}
