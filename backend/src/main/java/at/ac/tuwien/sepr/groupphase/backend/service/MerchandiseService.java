package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
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
    MerchandiseCreateDto saveMerchandise(MerchandiseCreateDto merchandiseCreateDto) throws ValidationException, ConflictException;

    /**
     * Retrieves a list of all available merchandises.
     *
     * @return a list of Merchandise objects representing all the merchandises
     */
    List<MerchandiseDetailDto> getAllMerchandise();

}
