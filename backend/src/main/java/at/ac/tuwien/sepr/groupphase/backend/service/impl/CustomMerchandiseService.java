package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomMerchandiseService implements MerchandiseService {

    MerchandiseRepository merchandiseRepository;
    MerchandiseValidator merchandiseValidator;
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CustomMerchandiseService(MerchandiseRepository merchandiseRepository, MerchandiseValidator merchandiseValidator) {
        this.merchandiseRepository = merchandiseRepository;
        this.merchandiseValidator = merchandiseValidator;
    }

    @Override
    public MerchandiseCreateDto saveMerchandise(MerchandiseCreateDto merchandiseCreateDto) throws ValidationException, ConflictException {
        merchandiseValidator.validateCreate(merchandiseCreateDto);

        LOGGER.info("Save merchandise {}", merchandiseCreateDto);
        Merchandise toAdd = new Merchandise();
        toAdd.setPrice(merchandiseCreateDto.getPrice());
        toAdd.setName(merchandiseCreateDto.getName());
        toAdd.setCategory(merchandiseCreateDto.getCategory());
        toAdd.setImageUrl(merchandiseCreateDto.getImageUrl());
        toAdd.setPoints(merchandiseCreateDto.getPrice().multiply(BigDecimal.valueOf(10)).intValue());
        merchandiseRepository.save(toAdd);

        return merchandiseCreateDto;
    }

    @Override
    public List<Merchandise> getAllMerchandise() {
        return merchandiseRepository.findAll();
    }
}
