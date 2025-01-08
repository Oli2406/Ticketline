package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseItemDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.InsufficientStockException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;

import at.ac.tuwien.sepr.groupphase.backend.service.impl.MerchandiseServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MerchandiseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MerchandiseServiceImplTest {

    @Mock
    private MerchandiseRepository merchandiseRepository;

    @Mock
    private MerchandiseValidator merchandiseValidator;

    @InjectMocks
    private MerchandiseServiceImpl merchandiseServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMerchandise_ValidInput() throws ValidationException, ConflictException {
        MerchandiseCreateDto createDto = new MerchandiseCreateDto();
        createDto.setName("T-Shirt");
        createDto.setPrice(BigDecimal.valueOf(20.00));
        createDto.setCategory("Clothing");
        createDto.setImageUrl("http://example.com/tshirt.jpg");

        doNothing().when(merchandiseValidator).validateCreate(createDto);

        ArgumentCaptor<Merchandise> captor = ArgumentCaptor.forClass(Merchandise.class);

        MerchandiseCreateDto result = merchandiseServiceImpl.createMerchandise(createDto);

        verify(merchandiseRepository).save(captor.capture());
        Merchandise savedEntity = captor.getValue();

        assertEquals("T-Shirt", savedEntity.getName());
        assertEquals(BigDecimal.valueOf(20.00), savedEntity.getPrice());
        assertEquals("Clothing", savedEntity.getCategory());
        assertEquals("http://example.com/tshirt.jpg", savedEntity.getImageUrl());
        assertEquals(createDto, result);
    }

    @Test
    void testGetAllMerchandise() {
        Merchandise merchandise1 = new Merchandise();
        merchandise1.setMerchandiseId(1L);
        merchandise1.setName("T-Shirt");
        merchandise1.setPrice(BigDecimal.valueOf(20.00));
        merchandise1.setCategory("Clothing");
        merchandise1.setStock(10);
        merchandise1.setPoints(200);
        merchandise1.setImageUrl("http://example.com/tshirt.jpg");

        Merchandise merchandise2 = new Merchandise();
        merchandise2.setMerchandiseId(2L);
        merchandise2.setName("Mug");
        merchandise2.setPrice(BigDecimal.valueOf(10.00));
        merchandise2.setCategory("Accessories");
        merchandise2.setStock(15);
        merchandise2.setPoints(100);
        merchandise2.setImageUrl("http://example.com/mug.jpg");

        when(merchandiseRepository.findAll()).thenReturn(Arrays.asList(merchandise1, merchandise2));

        List<MerchandiseDetailDto> result = merchandiseServiceImpl.getAllMerchandise();

        assertEquals(2, result.size());
        assertEquals("T-Shirt", result.get(0).getName());
        assertEquals(BigDecimal.valueOf(20.00), result.get(0).getPrice());
        assertEquals("Mug", result.get(1).getName());
    }

    @Test
    void testProcessPurchase_Success() throws InsufficientStockException {
        PurchaseItemDto purchaseItem = new PurchaseItemDto();
        purchaseItem.setItemId(1L);
        purchaseItem.setQuantity(5);

        Merchandise merchandise = new Merchandise();
        merchandise.setMerchandiseId(1L);
        merchandise.setName("T-Shirt");
        merchandise.setStock(10);

        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(merchandise));

        merchandiseServiceImpl.processPurchase(List.of(purchaseItem));

        verify(merchandiseRepository).save(merchandise);
        assertEquals(5, merchandise.getStock());
    }

    @Test
    void testProcessPurchase_InsufficientStock() {
        PurchaseItemDto purchaseItem = new PurchaseItemDto();
        purchaseItem.setItemId(1L);
        purchaseItem.setQuantity(15);

        Merchandise merchandise = new Merchandise();
        merchandise.setMerchandiseId(1L);
        merchandise.setName("T-Shirt");
        merchandise.setStock(10);

        when(merchandiseRepository.findById(1L)).thenReturn(Optional.of(merchandise));

        assertThrows(InsufficientStockException.class, () ->
            merchandiseServiceImpl.processPurchase(List.of(purchaseItem))
        );

        verify(merchandiseRepository, never()).save(merchandise);
    }
}
