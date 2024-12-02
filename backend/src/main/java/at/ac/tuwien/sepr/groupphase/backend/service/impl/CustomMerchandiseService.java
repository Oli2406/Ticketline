package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class CustomMerchandiseService implements MerchandiseService {

    MerchandiseRepository merchandiseRepository;
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CustomMerchandiseService(MerchandiseRepository merchandiseRepository) {
        this.merchandiseRepository = merchandiseRepository;
    }

    @Override
    public MerchandiseCreateDto saveMerchandise(MerchandiseCreateDto merchandiseCreateDto) {
        LOGGER.info("Save merchandise {}", merchandiseCreateDto);

        Merchandise toAdd = new Merchandise();
        toAdd.setPrice(merchandiseCreateDto.getPrice());
        toAdd.setName(merchandiseCreateDto.getName());
        toAdd.setCategory(merchandiseCreateDto.getCategory());
        toAdd.setImageUrl(merchandiseCreateDto.getImageUrl());
        merchandiseRepository.save(toAdd);

        return merchandiseCreateDto;
    }
}
