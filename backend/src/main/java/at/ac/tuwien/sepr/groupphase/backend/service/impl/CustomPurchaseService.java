package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomPurchaseService implements PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final RandomStringGenerator generator;
    private final TicketService ticketService;

    public CustomPurchaseService(PurchaseRepository purchaseRepository,
        TicketRepository ticketRepository, MerchandiseRepository merchandiseRepository,
        RandomStringGenerator generator, TicketService ticketService) {
        this.purchaseRepository = purchaseRepository;
        this.ticketRepository = ticketRepository;
        this.merchandiseRepository = merchandiseRepository;
        this.generator = generator;
        this.ticketService = ticketService;
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
            purchaseCreateDto.getMerchandiseQuantities()
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
            purchase.getMerchandiseQuantities()
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
                purchase.getMerchandiseQuantities()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public PurchaseDetailDto getPurchaseById(Long id) {
        logger.info("Fetching purchase with ID: {}", id);

        // Lade das Purchase-Objekt
        Purchase purchase = purchaseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

        logger.debug("Fetched purchase: {}", purchase);

        // Lade die Tickets und Merchandise-Objekte basierend auf den IDs
        List<Ticket> tickets = ticketRepository.findAllById(purchase.getTicketIds());
        List<Merchandise> merchandise = merchandiseRepository.findAllById(
            purchase.getMerchandiseIds());

        // Erstelle und gebe ein PurchaseDetailDto zurück
        return new PurchaseDetailDto(
            purchase.getPurchaseId(),
            purchase.getUserId(),
            tickets,
            merchandise,
            purchase.getTotalPrice(),
            purchase.getPurchaseDate(),
            purchase.getMerchandiseQuantities()
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
        Purchase existingPurchase = purchaseRepository.findById(purchaseDetailDto.getPurchaseId())
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        List<Long> ticketIds = new java.util.ArrayList<>(List.of()); // the tickets after cancel
        List<Long> oldTickets = existingPurchase.getTicketIds();//the tickets before cancel
        List<Ticket> tickets = purchaseDetailDto.getTickets();

        for (Ticket ticket : tickets) {
            ticketIds.add(ticket.getTicketId());
        }

        List<Long> cancelledTickets = new ArrayList<>();

        for (int i = 0; i < oldTickets.size(); i++) {
            for (int j = i + 1; j < ticketIds.size(); j++) {
                if (!ticketIds.contains(oldTickets.get(i))) {
                    cancelledTickets.add(oldTickets.get(i));
                    this.ticketService.updateTicketStatusList(cancelledTickets, "AVAILABLE");
                }
            }
        }

        existingPurchase.setTicketIds(ticketIds);
        existingPurchase.setTotalPrice(purchaseDetailDto.getTotalPrice());
        purchaseRepository.save(existingPurchase);
        logger.info("Updated purchase: {}", existingPurchase);

        if (ticketIds.isEmpty()) {
            cancelledTickets.add(oldTickets.getFirst());
            this.ticketService.updateTicketStatusList(cancelledTickets, "AVAILABLE");
            purchaseRepository.deleteById(existingPurchase.getPurchaseId());
        } else {
            existingPurchase.setTicketIds(ticketIds);
            existingPurchase.setTotalPrice(purchaseDetailDto.getTotalPrice());
            purchaseRepository.save(existingPurchase);
        }
    }

}
