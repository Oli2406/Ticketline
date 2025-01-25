package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCancelDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.CancelPurchase;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseCancelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseCancelService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PurchaseCancelServiceImpl implements PurchaseCancelService {

    private static final Logger logger = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final PurchaseRepository purchaseRepository;
    private final PurchaseCancelRepository purchaseCancelRepository;
    private final TicketRepository ticketRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final RandomStringGenerator generator;
    private final TicketService ticketService;
    private final PerformanceRepository performanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;

    public PurchaseCancelServiceImpl(PurchaseRepository purchaseRepository,
        PurchaseCancelRepository purchaseCancelRepository,
        TicketRepository ticketRepository, MerchandiseRepository merchandiseRepository,
        RandomStringGenerator generator, TicketService ticketService,
        PerformanceRepository performanceRepository, ArtistRepository artistRepository,
        LocationRepository locationRepository) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseCancelRepository = purchaseCancelRepository;
        this.ticketRepository = ticketRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.generator = generator;
        this.ticketService = ticketService;
        this.performanceRepository = performanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public PurchaseCancelDetailDto getCancelPurchaseById(Long id) {
        logger.info("Fetching cancelled purchase with ID: {}", id);

        CancelPurchase cancelPurchase = purchaseCancelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

        logger.debug("Fetched purchase: {}", cancelPurchase);

        List<Ticket> tickets = ticketRepository.findAllById(cancelPurchase.getTicketIds());
        List<Merchandise> merchandise = merchandiseRepository.findAllById(
            cancelPurchase.getMerchandiseIds());

        return new PurchaseCancelDetailDto(
            cancelPurchase.getPurchaseId(),
            cancelPurchase.getUserId(),
            tickets,
            merchandise,
            cancelPurchase.getTotalPrice(),
            cancelPurchase.getPurchaseDate(),
            cancelPurchase.getMerchandiseQuantities(),
            cancelPurchase.getStreet(),
            cancelPurchase.getPostalCode(),
            cancelPurchase.getCity()
        );
    }

    @Override
    public List<PurchaseCancelDetailDto> getCancelPurchasesByUserId(Long userId) {
        logger.info("Fetching purchases for user with ID: {}", userId);

        List<CancelPurchase> cancelPurchases = purchaseCancelRepository.findByUserId(userId);

        return cancelPurchases.stream().map(cancelPurchase -> {
            List<Ticket> tickets = ticketRepository.findAllById(cancelPurchase.getTicketIds());
            List<Merchandise> merchandise = merchandiseRepository.findAllById(
                cancelPurchase.getMerchandiseIds());

            return new PurchaseCancelDetailDto(
                cancelPurchase.getPurchaseId(),
                cancelPurchase.getUserId(),
                tickets,
                merchandise,
                cancelPurchase.getTotalPrice(),
                cancelPurchase.getPurchaseDate(),
                cancelPurchase.getMerchandiseQuantities(),
                cancelPurchase.getStreet(),
                cancelPurchase.getPostalCode(),
                cancelPurchase.getCity()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public void updateCancelPurchase(PurchaseCancelDetailDto purchaseCancelDetailDto) {

        List<Long> ticketIds = new java.util.ArrayList<>(List.of()); // the tickets after cancel
        List<Ticket> tickets = purchaseCancelDetailDto.getTickets();
        List<Long> merchandiseIds = new java.util.ArrayList<>(List.of());

        for (Ticket ticket : tickets) {
            ticketIds.add(ticket.getTicketId());
        }

        for (Merchandise merchandise : purchaseCancelDetailDto.getMerchandises()) {
            merchandiseIds.add(merchandise.getMerchandiseId());
        }

        CancelPurchase cancelPurchase = new CancelPurchase(
            purchaseCancelDetailDto.getPurchaseId(),
            purchaseCancelDetailDto.getUserId(),
            ticketIds,
            merchandiseIds,
            purchaseCancelDetailDto.getMerchandiseQuantities(),
            purchaseCancelDetailDto.getTotalPrice(),
            purchaseCancelDetailDto.getPurchaseDate(),
            purchaseCancelDetailDto.getStreet(),
            purchaseCancelDetailDto.getPostalCode(),
            purchaseCancelDetailDto.getCity()
        );

        purchaseCancelRepository.save(cancelPurchase);
        //it should merge it when the entity already exist with the primary key
        logger.info("Updated purchase: {}", cancelPurchase);
    }

    @Override
    public List<PurchaseOverviewDto> getCancelPurchaseDetailsByUser(Long userId) {
        List<CancelPurchase> purchases = purchaseCancelRepository.findByUserId(userId);

        return purchases.stream().map(purchase -> {
            List<Ticket> tickets = ticketRepository.findAllById(purchase.getTicketIds());
            List<Merchandise> merchandises = merchandiseRepository.findAllById(
                purchase.getMerchandiseIds());

            Map<Long, Map<String, String>> performanceDetails = new HashMap<>();
            for (Ticket ticket : tickets) {
                Long performanceId = ticket.getPerformanceId();
                if (!performanceDetails.containsKey(performanceId)) {
                    Performance performance = performanceRepository.findById(performanceId)
                        .orElseThrow(() -> new IllegalArgumentException("Performance not found"));
                    Artist artist = artistRepository.findById(performance.getArtistId())
                        .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
                    Location location = locationRepository.findById(performance.getLocationId())
                        .orElseThrow(() -> new IllegalArgumentException("Location not found"));

                    Map<String, String> details = new HashMap<>();
                    details.put("name", performance.getName());
                    details.put("artistName", artist.getArtistName());
                    details.put("locationName", location.getName());

                    performanceDetails.put(performanceId, details);
                }
            }

            return new PurchaseOverviewDto(
                purchase.getPurchaseId(),
                purchase.getUserId(),
                tickets,
                merchandises,
                purchase.getTotalPrice().longValue(),
                purchase.getPurchaseDate(),
                purchase.getStreet(),
                purchase.getPostalCode(),
                purchase.getCity(),
                performanceDetails
            );
        }).collect(Collectors.toList());
    }
}
