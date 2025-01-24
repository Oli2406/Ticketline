package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCancelDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseOverviewDto;
import java.util.List;

public interface PurchaseCancelService {

    /**
     * Retrieves a specific purchase by its ID.
     *
     * @param id the ID of the purchase to retrieve
     * @return the detailed representation of the requested purchase
     */
    PurchaseCancelDetailDto getCancelPurchaseById(Long id);

    /**
     * Retrieves all purchases for a specific user.
     *
     * @param userId the ID of the user whose purchases to retrieve
     * @return a list of detailed representations of the user's purchases
     */
    List<PurchaseCancelDetailDto> getCancelPurchasesByUserId(Long userId);

    /**
     * Updates a purchase with the given data.
     *
     * @param purchaseCancelDetailDto the updated purchase
     */
    void updateCancelPurchase(PurchaseCancelDetailDto purchaseCancelDetailDto);

    /**
     * Retrieves all detailed purchases for a specific user.
     *
     * @param userId the ID of the user whose purchases to retrieve
     * @return a list of detailed representations of the user's purchases
     */
    List<PurchaseOverviewDto> getCancelPurchaseDetailsByUser(Long userId);
}
