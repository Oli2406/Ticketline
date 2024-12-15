package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Profile("generateData")
@DependsOn({"ticketDataGenerator", "performanceDataGenerator", "merchandiseDataGenerator"})
public class PurchaseDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseDataGenerator.class);

    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;
    private final MerchandiseRepository merchandiseRepository;
    private final Random random = new Random();

    public PurchaseDataGenerator(PurchaseRepository purchaseRepository, TicketRepository ticketRepository, MerchandiseRepository merchandiseRepository) {
        this.purchaseRepository = purchaseRepository;
        this.ticketRepository = ticketRepository;
        this.merchandiseRepository = merchandiseRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        int userCount = 7;

        if(purchaseRepository.count() > 0) {
            return;
        }

        for (long userId = 1; userId <= userCount; userId++) {
            createPurchasesForUser(userId);
        }
    }

    private void createPurchasesForUser(Long userId) {
        List<Ticket> reservedTickets = ticketRepository.findAll().stream()
            .filter(ticket -> ticket.getStatus().equals("RESERVED"))
            .collect(Collectors.toList());

        List<Ticket> purchasedTickets = ticketRepository.findAll().stream()
            .filter(ticket -> ticket.getStatus().equals("PURCHASED"))
            .collect(Collectors.toList());

        List<Merchandise> allMerchandise = merchandiseRepository.findAll();

        if (reservedTickets.size() < 2 || purchasedTickets.size() < 2 || allMerchandise.isEmpty()) {
            LOGGER.warn("Not enough reserved or purchased tickets or merchandise to create purchases.");
            return;
        }

        for (int i = 0; i < 2; i++) { // 2 purchases per user
            // Je 2 gekaufte und reservierte Tickets
            List<Long> ticketIds = new ArrayList<>();
            ticketIds.addAll(getRandomIds(purchasedTickets, 2));
            ticketIds.addAll(getRandomIds(reservedTickets, 2));

            // 2 Merchandise-Artikel je Kauf
            List<Long> merchandiseIds = getRandomIds(allMerchandise, 2);

            // Berechnung des Gesamtpreises
            Long totalPrice = calculateTotalPrice(ticketIds, merchandiseIds);

            // Erstellung des Kaufs
            Purchase purchase = new Purchase(
                userId,
                ticketIds,
                merchandiseIds,
                totalPrice,
                getRandomPastDate()
            );

            purchaseRepository.save(purchase);
            LOGGER.debug("Created purchase for user {}: {}", userId, purchase);
        }
    }


    private List<Long> getRandomIds(List<?> items, int count) {
        return random.ints(0, items.size())
            .distinct()
            .limit(count)
            .mapToObj(i -> {
                if (items.getFirst() instanceof Ticket) {
                    return ((Ticket) items.get(i)).getTicketId();
                } else if (items.getFirst() instanceof Merchandise) {
                    return ((Merchandise) items.get(i)).getMerchandiseId();
                }
                return null;
            })
            .collect(Collectors.toList());
    }

    private Long calculateTotalPrice(List<Long> ticketIds, List<Long> merchandiseIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        List<Merchandise> merchandise = merchandiseRepository.findAllById(merchandiseIds);

        long ticketTotal = tickets.stream().mapToLong(ticket -> ticket.getPrice().longValue()).sum();
        long merchandiseTotal = merchandise.stream().mapToLong(item -> item.getPrice().longValue()).sum();

        return ticketTotal + merchandiseTotal;
    }

    private LocalDate getRandomPastDate() {
        return LocalDate.now().minusDays(random.nextInt(1000));
    }
}
