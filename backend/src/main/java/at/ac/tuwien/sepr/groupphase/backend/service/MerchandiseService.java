package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;

public interface MerchandiseService {

    /**
     * Saves the given merchandise information and returns the saved merchandise details.
     *
     * @param merchandiseCreateDto the DTO containing the details of the merchandise to be saved, including price,
     *                             name, category, stock, and image URL
     * @return the DTO containing the details of the saved merchandise
     */
    public MerchandiseCreateDto saveMerchandise(MerchandiseCreateDto merchandiseCreateDto);

}
