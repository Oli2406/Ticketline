package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service interface for managing purchases.
 */
public interface PurchaseService {

    /**
     * Creates or updates a purchase.
     *
     * @param purchaseCreateDto the data for creating or updating the purchase
     * @return the detailed representation of the created or updated purchase
     * @throws ValidationException if the input data fails validation (e.g., missing or invalid fields)
     */
    PurchaseDetailDto createPurchase(PurchaseCreateDto purchaseCreateDto) throws ValidationException;

    /**
     * Retrieves a specific purchase by its ID.
     *
     * @param id the ID of the purchase to retrieve
     * @return the detailed representation of the requested purchase
     */
    PurchaseDetailDto getPurchaseById(Long id);

    /**
     * Retrieves all purchases for a specific user.
     *
     * @param userId the ID of the user whose purchases to retrieve
     * @return a list of detailed representations of the user's purchases
     */
    List<PurchaseDetailDto> getPurchasesByUserId(Long userId);

    /**
     * Deletes a specific purchase by its ID.
     *
     * @param id the ID of the purchase to delete
     */
    void deletePurchase(Long id);
}
