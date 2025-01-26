package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.CancelPurchase;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseCancelRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final RandomStringGenerator generator;
    private final TicketService ticketService;
    private final PerformanceRepository performanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;
    private final PurchaseCancelRepository purchaseCancelRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
        TicketRepository ticketRepository, MerchandiseRepository merchandiseRepository,
        RandomStringGenerator generator, TicketService ticketService,
        PerformanceRepository performanceRepository, ArtistRepository artistRepository,
        LocationRepository locationRepository, PurchaseCancelRepository purchaseCancelRepository) {
        this.purchaseRepository = purchaseRepository;
        this.ticketRepository = ticketRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.generator = generator;
        this.ticketService = ticketService;
        this.performanceRepository = performanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
        this.purchaseCancelRepository = purchaseCancelRepository;
    }

    @Override
    public PurchaseDetailDto createPurchase(PurchaseCreateDto purchaseCreateDto)
        throws ValidationException {
        logger.info("Creating or updating purchase: {}", purchaseCreateDto);
        Optional<Long> optionalL = generator.retrieveOriginalId(purchaseCreateDto.getUserId());
        Long value = optionalL.orElseThrow(() -> new ValidationException("Invalid user ID", List.of(
            "User ID could not be resolved.",
            "Ensure that the encrypted ID is correct."
        )));

        Purchase purchase = new Purchase(
            value,
            purchaseCreateDto.getTicketIds(),
            purchaseCreateDto.getMerchandiseIds(),
            purchaseCreateDto.getTotalPrice(),
            purchaseCreateDto.getPurchaseDate().plusHours(1),
            purchaseCreateDto.getMerchandiseQuantities(),
            purchaseCreateDto.getStreet(),
            purchaseCreateDto.getPostalCode(),
            purchaseCreateDto.getCity()
        );

        logger.debug("Mapped Purchase entity: {}", purchase);

        purchase = purchaseRepository.save(purchase);

        List<Ticket> tickets = ticketRepository.findAllById(purchase.getTicketIds());
        List<Merchandise> merchandise = merchandiseRepository.findAllById(
            purchase.getMerchandiseIds());

        logger.info("Saved purchase to database: {}", purchase);

        return new PurchaseDetailDto(
            purchase.getPurchaseId(),
            purchase.getUserId(),
            tickets,
            merchandise,
            purchase.getTotalPrice(),
            purchase.getPurchaseDate(),
            purchase.getMerchandiseQuantities(),
            purchase.getStreet(),
            purchase.getPostalCode(),
            purchase.getCity()
        );
    }

    @Override
    public List<PurchaseDetailDto> getPurchasesByUserId(Long userId) {
        logger.info("Fetching purchases for user with ID: {}", userId);

        List<Purchase> purchases = purchaseRepository.findByUserId(userId);

        return purchases.stream().map(purchase -> {
            List<Ticket> tickets = ticketRepository.findAllById(purchase.getTicketIds());
            List<Merchandise> merchandise = merchandiseRepository.findAllById(
                purchase.getMerchandiseIds());

            return new PurchaseDetailDto(
                purchase.getPurchaseId(),
                purchase.getUserId(),
                tickets,
                merchandise,
                purchase.getTotalPrice(),
                purchase.getPurchaseDate(),
                purchase.getMerchandiseQuantities(),
                purchase.getStreet(),
                purchase.getPostalCode(),
                purchase.getCity()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public PurchaseDetailDto getPurchaseById(Long id) {
        logger.info("Fetching purchase with ID: {}", id);

        Purchase purchase = purchaseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

        logger.debug("Fetched purchase: {}", purchase);

        List<Ticket> tickets = ticketRepository.findAllById(purchase.getTicketIds());
        List<Merchandise> merchandise = merchandiseRepository.findAllById(
            purchase.getMerchandiseIds());

        return new PurchaseDetailDto(
            purchase.getPurchaseId(),
            purchase.getUserId(),
            tickets,
            merchandise,
            purchase.getTotalPrice(),
            purchase.getPurchaseDate(),
            purchase.getMerchandiseQuantities(),
            purchase.getStreet(),
            purchase.getPostalCode(),
            purchase.getCity()
        );
    }


    @Override
    public void deletePurchase(Long id) {
        logger.info("Deleting purchase with ID: {}", id);
        if (!purchaseRepository.existsById(id)) {
            throw new IllegalArgumentException("Purchase not found");
        }
        purchaseRepository.deleteById(id);
        logger.debug("Deleted purchase with ID: {}", id);
    }

    @Override
    public void updatePurchase(PurchaseDetailDto purchaseDetailDto) {
        logger.info("updating purchase: {}", purchaseDetailDto);
        Purchase existingPurchase = purchaseRepository.findById(purchaseDetailDto.getPurchaseId())
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

        List<Long> alreadyCancelledTickets = new ArrayList<>();

        if (purchaseCancelRepository.existsByPurchaseId(purchaseDetailDto.getPurchaseId())) {
            CancelPurchase existingCancellation = purchaseCancelRepository.findById(
                    purchaseDetailDto.getPurchaseId())
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
            alreadyCancelledTickets = (existingCancellation.getTicketIds());
        }

        List<Long> ticketIds = new java.util.ArrayList<>(List.of()); // the tickets after cancel
        List<Long> oldTickets = existingPurchase.getTicketIds(); //the tickets before cancel
        List<Ticket> tickets = purchaseDetailDto.getTickets();

        for (Ticket ticket : tickets) {
            ticketIds.add(ticket.getTicketId());
        }

        if (ticketIds.isEmpty()) {
            alreadyCancelledTickets.addAll(alreadyCancelledTickets.size()-1,existingPurchase.getTicketIds());
        }

        List<Long> cancelledTickets = new ArrayList<>();

        for (int i = 0; i < oldTickets.size(); i++) {
            for (int j = i + 1; j < ticketIds.size(); j++) {
                if (!ticketIds.contains(oldTickets.get(i))) {
                    cancelledTickets.add(oldTickets.get(i));
                    alreadyCancelledTickets.add(oldTickets.get(i));
                    this.ticketService.updateTicketStatusList(cancelledTickets, "AVAILABLE");
                    break;
                }
            }
        }

        double cancelledTotalPrice = 0;
        for (Long cancelTicket : alreadyCancelledTickets) {
            double price = this.ticketService.getTicketById(cancelTicket).getPrice().floatValue();
            cancelledTotalPrice += price;
        }

        existingPurchase.setTicketIds(ticketIds);
        existingPurchase.setTotalPrice(purchaseDetailDto.getTotalPrice());
        purchaseRepository.save(existingPurchase);
        logger.info("Updated purchase: {}", existingPurchase);

        CancelPurchase cancelPurchase = new CancelPurchase(
            purchaseDetailDto.getPurchaseId(),
            existingPurchase.getUserId(),
            alreadyCancelledTickets,
            existingPurchase.getMerchandiseIds(),
            existingPurchase.getMerchandiseQuantities(),
            cancelledTotalPrice,
            existingPurchase.getPurchaseDate(),
            existingPurchase.getStreet(),
            existingPurchase.getPostalCode(),
            existingPurchase.getCity()
        );

        if (ticketIds.isEmpty()) {
            cancelledTickets.add(oldTickets.getFirst());
            this.ticketService.updateTicketStatusList(cancelledTickets, "AVAILABLE");
            purchaseRepository.deleteById(existingPurchase.getPurchaseId());
        } else {
            existingPurchase.setTicketIds(ticketIds);
            existingPurchase.setTotalPrice(purchaseDetailDto.getTotalPrice());
            purchaseRepository.save(existingPurchase);
        }

        logger.info("Saving cancelled purchase: {}", cancelPurchase);
        purchaseCancelRepository.save(cancelPurchase);
    }

    @Override
    public List<PurchaseOverviewDto> getPurchaseDetailsByUser(Long userId) {
        List<Purchase> purchases = purchaseRepository.findByUserId(userId);

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
                purchase.getTotalPrice(),
                purchase.getPurchaseDate(),
                purchase.getStreet(),
                purchase.getPostalCode(),
                purchase.getCity(),
                performanceDetails
            );
        }).collect(Collectors.toList());
    }
}
