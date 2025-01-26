package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.PurchaseEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;

class PurchaseEndpointTest {

    @Mock
    private PurchaseService purchaseService;

    @Mock
    private RandomStringGenerator randomStringGenerator;

    @Mock
    private MerchandiseService merchandiseService;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private PurchaseEndpoint purchaseEndpoint;

    private final PurchaseDetailDto purchaseDetailDto = new PurchaseDetailDto(
        1L, 1L, List.of(), List.of(), 100L, LocalDateTime.now(), List.of(), "Main Street", "12345", "Cityville"
    );

    private final PurchaseCreateDto purchaseCreateDto = new PurchaseCreateDto(
        "ENC123", List.of(1L, 2L), List.of(1L), 100L, LocalDateTime.now(), List.of(1L), "Main Street", "12345", "Cityville"
    );

    private final List<PurchaseDetailDto> mockPurchases = List.of(purchaseDetailDto);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPurchaseByIdReturnsPurchase() {
        when(purchaseService.getPurchaseById(1L)).thenReturn(purchaseDetailDto);

        ResponseEntity<PurchaseDetailDto> response = purchaseEndpoint.getPurchaseById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchaseDetailDto, response.getBody());
        verify(purchaseService, times(1)).getPurchaseById(1L);
    }

    @Test
    void getPurchasesByUserReturnsPurchases() {
        when(randomStringGenerator.retrieveOriginalId("ENC123")).thenReturn(Optional.of(1L));
        when(purchaseService.getPurchasesByUserId(1L)).thenReturn(mockPurchases);

        ResponseEntity<List<PurchaseDetailDto>> response = purchaseEndpoint.getPurchasesByUser("ENC123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPurchases, response.getBody());
        verify(purchaseService, times(1)).getPurchasesByUserId(1L);
    }

    @Test
    void createPurchaseReturnsCreatedPurchase() throws ValidationException {
        doNothing().when(merchandiseService).reduceStockOfMerchandiseList(any(), any());
        doNothing().when(ticketService).updateTicketStatusList(any(), anyString());
        when(purchaseService.createPurchase(any())).thenReturn(purchaseDetailDto);

        ResponseEntity<PurchaseDetailDto> response = purchaseEndpoint.createPurchase(purchaseCreateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchaseDetailDto, response.getBody());
        verify(merchandiseService, times(1)).reduceStockOfMerchandiseList(purchaseCreateDto.getMerchandiseIds(), purchaseCreateDto.getMerchandiseQuantities());
        verify(ticketService, times(1)).updateTicketStatusList(purchaseCreateDto.getTicketIds(), "SOLD");
        verify(purchaseService, times(1)).createPurchase(purchaseCreateDto);
    }

    @Test
    void updatePurchaseWithValidInput() throws ValidationException {
        doNothing().when(purchaseService).updatePurchase(any(PurchaseDetailDto.class));

        ResponseEntity<Void> response = purchaseEndpoint.updatePurchase(1L, purchaseDetailDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(purchaseService, times(1)).updatePurchase(purchaseDetailDto);
    }

    @Test
    void updatePurchaseWithIdMismatchThrowsValidationException() {
        PurchaseDetailDto mismatchedPurchaseDto = new PurchaseDetailDto(
            2L, 1L, List.of(), List.of(), 100L, LocalDateTime.now(), List.of(), "Main Street", "12345", "Cityville"
        );

        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> purchaseEndpoint.updatePurchase(1L, mismatchedPurchaseDto)
        );

        assertTrue(exception.getMessage().contains("ID mismatch"));
    }

    @Test
    void getPurchaseDetailsByUserReturnsOverviewWithPerformanceDetails() {
        Map<Long, Map<String, String>> performanceDetails = Map.of(
            101L, Map.of("performanceName", "Performance A", "artistName", "Artist 1"),
            102L, Map.of("performanceName", "Performance B", "artistName", "Artist 2")
        );

        List<Ticket> tickets = List.of(
            new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.A, BigDecimal.valueOf(50), "SOLD", Hall.A, 101L, LocalDateTime.now()),
            new Ticket(2L, 2, 2, PriceCategory.STANDARD, TicketType.STANDING, SectorType.B, BigDecimal.valueOf(30), "SOLD", Hall.B, 102L, LocalDateTime.now())
        );

        List<Merchandise> merchandises = List.of(
            new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500)
        );

        List<PurchaseOverviewDto> mockOverview = List.of(
            new PurchaseOverviewDto(
                1L,
                1L,
                tickets,
                merchandises,
                100L,
                LocalDateTime.now(),
                "Main Street",
                "12345",
                "Cityville",
                performanceDetails
            )
        );

        when(randomStringGenerator.retrieveOriginalId("ENC123")).thenReturn(Optional.of(1L));
        when(purchaseService.getPurchaseDetailsByUser(1L)).thenReturn(mockOverview);

        ResponseEntity<List<PurchaseOverviewDto>> response = purchaseEndpoint.getPurchaseDetailsByUser("ENC123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        PurchaseOverviewDto overview = response.getBody().get(0);
        assertEquals(1L, overview.getPurchaseId());
        assertEquals(1L, overview.getUserId());
        assertEquals(2, overview.getTickets().size());
        assertEquals(1, overview.getMerchandises().size());
        assertEquals(100L, overview.getTotalPrice());
        assertEquals("Main Street", overview.getStreet());
        assertEquals("12345", overview.getPostalCode());
        assertEquals("Cityville", overview.getCity());
        assertEquals(performanceDetails, overview.getPerformanceDetails());

        verify(randomStringGenerator, times(1)).retrieveOriginalId("ENC123");
        verify(purchaseService, times(1)).getPurchaseDetailsByUser(1L);
    }
}