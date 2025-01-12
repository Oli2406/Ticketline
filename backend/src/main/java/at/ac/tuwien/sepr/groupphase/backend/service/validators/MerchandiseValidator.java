package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class MerchandiseValidator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchandiseRepository merchandiseRepository;

    public MerchandiseValidator(MerchandiseRepository merchandiseRepository) {
        this.merchandiseRepository = merchandiseRepository;
    }

    public void validateCreate(MerchandiseCreateDto dto)
        throws ValidationException, ConflictException {
        LOGGER.info("Validating merchandise data: {}", dto);
        List<String> validationErrors = new java.util.ArrayList<>();
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            validationErrors.add("Name is required");
        } else if (dto.getName().length() > 255) {
            validationErrors.add("Name must be less than 255 characters");
        }

        if (dto.getPrice().compareTo(new java.math.BigDecimal(0)) < 0) {
            validationErrors.add("Price must be greater than 0");
        }

        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            validationErrors.add("Category is required");
        } else if (dto.getCategory().length() > 255) {
            validationErrors.add("Category must be less than 255 characters");
        }

        if (dto.getStock() < 0) {
            validationErrors.add("Stock must be greater than 0");
        }

        isNameUnique(dto.getName());

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("Merchandise data creation failed");
            throw new ValidationException("Merchandise data creation failed: ",
                validationErrors);
        }

    }

    public void isNameUnique(String name) throws ConflictException {
        if (merchandiseRepository.existsByName(name)) {
            List<String> error = new java.util.ArrayList<>();
            error.add("name is already registered");
            LOGGER.warn("conflict error in create : {}", error);
            throw new ConflictException("Creation for merchandise has a conflict: ", error);
        }
    }
}
