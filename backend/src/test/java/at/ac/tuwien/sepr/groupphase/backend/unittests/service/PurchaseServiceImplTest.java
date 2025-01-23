package at.ac.tuwien.sepr.groupphase.backend.unittests.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PurchaseServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;

public class PurchaseServiceImplTest {

    private PurchaseServiceImpl purchaseService;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private MerchandiseRepository merchandiseRepository;

    @Mock
    private RandomStringGenerator generator;

    @Mock
    private TicketService ticketService;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        purchaseService = new PurchaseServiceImpl(purchaseRepository, ticketRepository, merchandiseRepository, generator, ticketService, performanceRepository, artistRepository, locationRepository);
    }

    @Test
    void createPurchase_ShouldCreatePurchase_WhenValidInput() throws ValidationException {
        PurchaseCreateDto purchaseCreateDto = new PurchaseCreateDto("ENC123", List.of(1L, 2L), List.of(1L), 100L, LocalDateTime.now(), List.of(4L), "Main Street", "12345", "Cityville");
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.STANDING, SectorType.A, BigDecimal.valueOf(50), "AVAILABLE", Hall.A, 123L, LocalDateTime.now());
        Ticket ticket2 = new Ticket(2L, 2, 2, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(30), "AVAILABLE", Hall.B, 124L, LocalDateTime.now());
        Merchandise merchandise = new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500);
        merchandise.setMerchandiseId(1L);

        when(generator.retrieveOriginalId(anyString())).thenReturn(Optional.of(1L));
        when(ticketRepository.findAllById(purchaseCreateDto.getTicketIds())).thenReturn(List.of(ticket1, ticket2));
        when(merchandiseRepository.findAllById(purchaseCreateDto.getMerchandiseIds())).thenReturn(List.of(merchandise));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase purchase = invocation.getArgument(0);
            purchase.setPurchaseId(1L);
            return purchase;
        });

        PurchaseDetailDto result = purchaseService.createPurchase(purchaseCreateDto);

        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getPurchaseId(), "Purchase ID should match");
        assertEquals(2, result.getTickets().size(), "Two tickets should be included");
        assertEquals(1L, result.getMerchandises().getFirst().getMerchandiseId(), "Merchandise ID should match");

        verify(ticketRepository, times(1)).findAllById(purchaseCreateDto.getTicketIds());
        verify(merchandiseRepository, times(1)).findAllById(purchaseCreateDto.getMerchandiseIds());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    void getPurchasesByUserId_ShouldReturnPurchases_WhenUserHasPurchases() {
        Long userId = 1L;
        Purchase purchase = new Purchase(userId, List.of(1L, 2L), List.of(3L), 100L, LocalDateTime.now(), List.of(2L), "Main Street", "12345", "Cityville");
        purchase.setPurchaseId(1L);
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.STANDING, SectorType.A, BigDecimal.valueOf(50), "SOLD", Hall.A, 123L, LocalDateTime.now());
        Ticket ticket2 = new Ticket(2L, 2, 2, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(30), "SOLD", Hall.B, 124L, LocalDateTime.now());
        Merchandise merchandise = new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500);
        merchandise.setMerchandiseId(3L);

        when(purchaseRepository.findByUserId(userId)).thenReturn(List.of(purchase));
        when(ticketRepository.findAllById(purchase.getTicketIds())).thenReturn(List.of(ticket1, ticket2));
        when(merchandiseRepository.findAllById(purchase.getMerchandiseIds())).thenReturn(List.of(merchandise));

        List<PurchaseDetailDto> result = purchaseService.getPurchasesByUserId(userId);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one purchase");
        assertEquals(2, result.get(0).getTickets().size(), "Two tickets should be included");
        assertEquals(3L, result.get(0).getMerchandises().get(0).getMerchandiseId(), "Merchandise ID should match");

        verify(purchaseRepository, times(1)).findByUserId(userId);
        verify(ticketRepository, times(1)).findAllById(purchase.getTicketIds());
        verify(merchandiseRepository, times(1)).findAllById(purchase.getMerchandiseIds());
    }

    @Test
    void getPurchaseById_ShouldReturnPurchase_WhenPurchaseExists() {
        Long purchaseId = 1L;
        Purchase purchase = new Purchase(1L, List.of(1L, 2L), List.of(3L), 100L, LocalDateTime.now(), List.of(2L), "Main Street", "12345", "Cityville");
        purchase.setPurchaseId(1L);
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(50), "SOLD", Hall.A, 123L, LocalDateTime.now());
        Ticket ticket2 = new Ticket(2L, 2, 2, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(30), "SOLD", Hall.B, 124L, LocalDateTime.now());
        Merchandise merchandise = new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500);
        merchandise.setMerchandiseId(3L);

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(ticketRepository.findAllById(purchase.getTicketIds())).thenReturn(List.of(ticket1, ticket2));
        when(merchandiseRepository.findAllById(purchase.getMerchandiseIds())).thenReturn(List.of(merchandise));

        PurchaseDetailDto result = purchaseService.getPurchaseById(purchaseId);

        assertNotNull(result, "Result should not be null");
        assertEquals(purchaseId, result.getPurchaseId(), "Purchase ID should match");
        assertEquals(2, result.getTickets().size(), "Two tickets should be included");
        assertEquals(3L, result.getMerchandises().get(0).getMerchandiseId(), "Merchandise ID should match");

        verify(purchaseRepository, times(1)).findById(purchaseId);
        verify(ticketRepository, times(1)).findAllById(purchase.getTicketIds());
        verify(merchandiseRepository, times(1)).findAllById(purchase.getMerchandiseIds());
    }

    @Test
    void deletePurchase_ShouldDeletePurchase_WhenPurchaseExists() {
        Long purchaseId = 1L;
        when(purchaseRepository.existsById(purchaseId)).thenReturn(true);

        purchaseService.deletePurchase(purchaseId);

        verify(purchaseRepository, times(1)).existsById(purchaseId);
        verify(purchaseRepository, times(1)).deleteById(purchaseId);
    }

    @Test
    void updatePurchase_ShouldUpdatePurchase_WhenValidInput() {
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(50), "SOLD", Hall.A, 123L, LocalDateTime.now());
        ticket1.setTicketId(1L);
        List<Ticket> tickets = List.of(ticket1);
        PurchaseDetailDto purchaseDetailDto = new PurchaseDetailDto(
            1L,
            1L,
            tickets,
            List.of(new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500)),
            150L,
            LocalDateTime.now(),
            List.of(3L),
            "Main Street",
            "12345",
            "Cityville"
        );

        Purchase existingPurchase = new Purchase(
            1L,
            List.of(1L, 2L),
            List.of(3L),
            100L,
            LocalDateTime.now(),
            List.of(3L),
            "Main Street",
            "12345",
            "Cityville"
        );

        existingPurchase.setPurchaseId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(existingPurchase));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        purchaseService.updatePurchase(purchaseDetailDto);

        assertEquals(List.of(1L), existingPurchase.getTicketIds(), "Ticket IDs should match updated list");
        assertEquals(150L, existingPurchase.getTotalPrice(), "Total price should be updated");

        verify(purchaseRepository, times(1)).save(existingPurchase);
    }

    @Test
    void createPurchase_ShouldThrowValidationException_WhenInvalidUserId() {
        PurchaseCreateDto purchaseCreateDto = new PurchaseCreateDto("INVALID", List.of(1L, 2L), List.of(1L), 100L, LocalDateTime.now(), List.of(4L), "Main Street", "12345", "Cityville");

        when(generator.retrieveOriginalId(anyString())).thenReturn(Optional.empty());

        ValidationException exception = Assertions.assertThrows(
            ValidationException.class,
            () -> purchaseService.createPurchase(purchaseCreateDto),
            "Expected ValidationException to be thrown"
        );

        assertEquals("Invalid user ID. Failed validations: User ID could not be resolved., Ensure that the encrypted ID is correct..", exception.getMessage(), "Error message should match exactly");
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
    }

    @Test
    void getPurchaseById_ShouldThrowException_WhenPurchaseDoesNotExist() {
        Long purchaseId = 999L;

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> purchaseService.getPurchaseById(purchaseId),
            "Expected IllegalArgumentException to be thrown"
        );

        assertEquals("Purchase not found", exception.getMessage(), "Error message should match");
        verify(purchaseRepository, times(1)).findById(purchaseId);
    }

    @Test
    void deletePurchase_ShouldThrowException_WhenPurchaseDoesNotExist() {
        Long purchaseId = 999L;

        when(purchaseRepository.existsById(purchaseId)).thenReturn(false);

        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> purchaseService.deletePurchase(purchaseId),
            "Expected IllegalArgumentException to be thrown"
        );

        assertEquals("Purchase not found", exception.getMessage(), "Error message should match");
        verify(purchaseRepository, times(1)).existsById(purchaseId);
        verify(purchaseRepository, times(0)).deleteById(purchaseId);
    }

    @Test
    void updatePurchase_ShouldThrowException_WhenPurchaseDoesNotExist() {
        PurchaseDetailDto purchaseDetailDto = new PurchaseDetailDto(
            1L,
            1L,
            List.of(new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(50), "SOLD", Hall.A, 123L, LocalDateTime.now())),
            List.of(new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500)),
            150L,
            LocalDateTime.now(),
            List.of(3L),
            "Main Street",
            "12345",
            "Cityville"
        );

        when(purchaseRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> purchaseService.updatePurchase(purchaseDetailDto),
            "Expected IllegalArgumentException to be thrown"
        );

        assertEquals("Purchase not found", exception.getMessage(), "Error message should match");
        verify(purchaseRepository, times(1)).findById(1L);
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
    }

    @Test
    void updatePurchase_ShouldHandleNoTickets_WhenUpdated() {
        PurchaseDetailDto purchaseDetailDto = new PurchaseDetailDto(
            1L,
            1L,
            List.of(),
            List.of(),
            0L,
            LocalDateTime.now(),
            List.of(),
            "Main Street",
            "12345",
            "Cityville"
        );

        Purchase existingPurchase = new Purchase(
            1L,
            List.of(1L, 2L),
            List.of(3L),
            100L,
            LocalDateTime.now(),
            List.of(3L),
            "Main Street",
            "12345",
            "Cityville"
        );
        existingPurchase.setPurchaseId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(existingPurchase));

        purchaseService.updatePurchase(purchaseDetailDto);

        verify(ticketService, times(1)).updateTicketStatusList(List.of(1L, 2L), "AVAILABLE");
        verify(purchaseRepository, times(1)).deleteById(existingPurchase.getPurchaseId());
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
    }

    @Test
    void getPurchaseDetailsByUser_ShouldReturnCorrectPerformanceDetails() {
        Long userId = 1L;

        Purchase purchase = new Purchase(
            userId,
            List.of(1L),
            List.of(2L),
            100L,
            LocalDateTime.now(),
            List.of(2L),
            "Main Street",
            "12345",
            "Cityville"
        );
        purchase.setPurchaseId(1L);

        Ticket ticket = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(50), "SOLD", Hall.A, 123L, LocalDateTime.now());
        Performance performance = new Performance("Concert", 1L, 1L, LocalDateTime.now(), null, 100L, "Main Hall", null, null, 120);
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("Venue", "Street", "City", "12345", "Country");

        Merchandise merchandise = new Merchandise("T-Shirt", "Clothing", BigDecimal.valueOf(25.0), 5, null, 500);
        merchandise.setMerchandiseId(2L);

        when(purchaseRepository.findByUserId(userId)).thenReturn(List.of(purchase));
        when(ticketRepository.findAllById(purchase.getTicketIds())).thenReturn(List.of(ticket));
        when(merchandiseRepository.findAllById(purchase.getMerchandiseIds())).thenReturn(List.of(merchandise));
        when(performanceRepository.findById(ticket.getPerformanceId())).thenReturn(Optional.of(performance));
        when(artistRepository.findById(performance.getArtistId())).thenReturn(Optional.of(artist));
        when(locationRepository.findById(performance.getLocationId())).thenReturn(Optional.of(location));

        List<PurchaseOverviewDto> result = purchaseService.getPurchaseDetailsByUser(userId);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one purchase overview");
        assertEquals("Concert", result.get(0).getPerformanceDetails().get(ticket.getPerformanceId()).get("name"), "Performance name should match");
        assertEquals("ArtistName", result.get(0).getPerformanceDetails().get(ticket.getPerformanceId()).get("artistName"), "Artist name should match");
        assertEquals("Venue", result.get(0).getPerformanceDetails().get(ticket.getPerformanceId()).get("locationName"), "Location name should match");

        verify(purchaseRepository, times(1)).findByUserId(userId);
    }

}
