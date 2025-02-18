package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.InsufficientStockException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.MerchandiseValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MerchandiseServiceImpl implements MerchandiseService {

    MerchandiseRepository merchandiseRepository;
    MerchandiseValidator merchandiseValidator;
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public MerchandiseServiceImpl(MerchandiseRepository merchandiseRepository,
        MerchandiseValidator merchandiseValidator) {
        this.merchandiseRepository = merchandiseRepository;
        this.merchandiseValidator = merchandiseValidator;
    }

    @Override
    public MerchandiseCreateDto createMerchandise(MerchandiseCreateDto merchandiseCreateDto)
        throws ValidationException, ConflictException {
        merchandiseValidator.validateCreate(merchandiseCreateDto);

        LOGGER.info("Save merchandise {}", merchandiseCreateDto);
        Merchandise toAdd = new Merchandise();
        toAdd.setPrice(merchandiseCreateDto.getPrice());
        toAdd.setName(merchandiseCreateDto.getName());
        toAdd.setCategory(merchandiseCreateDto.getCategory());
        toAdd.setImageUrl(merchandiseCreateDto.getImageUrl());
        if (merchandiseCreateDto.getPrice().compareTo(new BigDecimal(1)) < 0) {
            toAdd.setPoints(1);
        } else {
            toAdd.setPoints(
                merchandiseCreateDto.getPrice().multiply(BigDecimal.valueOf(10)).intValue());
        }
        toAdd.setStock(merchandiseCreateDto.getStock());
        merchandiseRepository.save(toAdd);
        return merchandiseCreateDto;
    }

    @Override
    public List<MerchandiseDetailDto> getAllMerchandise() {
        List<Merchandise> merchandises = merchandiseRepository.findAll();
        return merchandises.stream()
            .map(merchandise -> new MerchandiseDetailDto(
                merchandise.getId(),
                merchandise.getName(),
                merchandise.getPrice(),
                merchandise.getCategory(),
                merchandise.getStock(),
                merchandise.getPoints(),
                merchandise.getImageUrl()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void processPurchase(List<PurchaseItemDto> purchaseItems)
        throws InsufficientStockException {
        for (PurchaseItemDto item : purchaseItems) {
            Merchandise merchandise = merchandiseRepository.findById(item.getItemId())
                .orElseThrow(
                    () -> new IllegalArgumentException("Invalid item ID: " + item.getItemId()));
            if (merchandise.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                    "Not enough stock for item: " + merchandise.getName());
            }
            merchandise.setStock(merchandise.getStock() - item.getQuantity());
            merchandiseRepository.save(merchandise);
        }
    }

    @Override
    public void reduceStockOfMerchandiseList(List<Long> merchandiseIds, List<Long> quantityList) {
        if (merchandiseIds.size() != quantityList.size()) {
            throw new IllegalArgumentException("Merchandise IDs and quantity lists must have the same size.");
        }
        IntStream.range(0, merchandiseIds.size()).forEach(idx -> {
            Long merchandiseId = merchandiseIds.get(idx);
            Long quantity = quantityList.get(idx);
            Merchandise merch = merchandiseRepository.findById(merchandiseId)
                .orElseThrow(() -> new IllegalArgumentException("Merchandise not found: " + merchandiseId));
            if (merch.getStock() < quantity) {
                throw new InsufficientStockException("Not enough stock for item: " + merch.getName());
            }
            merch.setStock(merch.getStock() - quantity.intValue());
            merchandiseRepository.save(merch);
        });
    }

}
