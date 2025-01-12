package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseItemDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.InsufficientStockException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface MerchandiseService {

    /**
     * Saves the given merchandise information and returns the saved merchandise details.
     *
     * @param merchandiseCreateDto the DTO containing the details of the merchandise to be saved, including price,
     *                             name, category, stock, and image URL
     * @return the DTO containing the details of the saved merchandise
     */
    MerchandiseCreateDto createMerchandise(MerchandiseCreateDto merchandiseCreateDto) throws ValidationException, ConflictException;

    /**
     * Retrieves a list of all available merchandises.
     *
     * @return a list of Merchandise objects representing all the merchandises
     */
    List<MerchandiseDetailDto> getAllMerchandise();

    /**
     * Processes a list of purchase items and updates the stock accordingly.
     *
     * @param purchaseItems the list of purchase items to be processed, each containing an item ID and the quantity to be purchased
     * @throws InsufficientStockException if there is insufficient stock for any of the purchase items
     */
    void processPurchase(List<PurchaseItemDto> purchaseItems);

    /**
     * Reduces the stock of multiple merchandise items based on the provided lists of merchandise IDs and quantities.
     * This operation assumes that the provided lists are of the same size and that each ID corresponds to the respective quantity at the same index.
     *
     * @param merchandiseIds a list of IDs representing the merchandise items whose stock will be reduced
     * @param quantityList a list of quantities to reduce from the stock of the respective merchandise items
     * @throws IllegalArgumentException if the lists are not of the same size or if any of the quantities are negative
     * @throws InsufficientStockException if the stock for any merchandise item is insufficient to fulfill the reduction
     */
    void reduceStockOfMerchandiseList(List<Long> merchandiseIds, List<Long> quantityList);
}
