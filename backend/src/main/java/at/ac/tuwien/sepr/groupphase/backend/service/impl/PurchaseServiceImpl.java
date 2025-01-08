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
import at.ac.tuwien.sepr.groupphase.backend.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;
    private final MerchandiseRepository merchandiseRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
        TicketRepository ticketRepository, MerchandiseRepository merchandiseRepository) {
        this.purchaseRepository = purchaseRepository;
        this.ticketRepository = ticketRepository;
        this.merchandiseRepository = merchandiseRepository;
    }

    @Override
    public PurchaseDetailDto createPurchase(PurchaseCreateDto purchaseCreateDto)
        throws ValidationException {
        logger.info("Creating or updating purchase: {}", purchaseCreateDto);

        Purchase purchase = new Purchase(
            purchaseCreateDto.getUserId(),
            purchaseCreateDto.getTicketIds(),
            purchaseCreateDto.getMerchandiseIds(),
            purchaseCreateDto.getTotalPrice(),
            purchaseCreateDto.getPurchaseDate()
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
            purchase.getPurchaseDate()
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
                purchase.getPurchaseDate()
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
            purchase.getPurchaseDate()
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
}
