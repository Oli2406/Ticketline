package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PurchaseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Profile("generateData")
@DependsOn({"ticketDataGenerator"})
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

        if (purchaseRepository.count() > 0) {
            return;
        }

        for (long userId = 1; userId <= userCount; userId++) {
            createPurchasesForUser(userId);
        }
    }

    private void createPurchasesForUser(Long userId) {
        List<Ticket> purchasedTickets = ticketRepository.findAll().stream()
            .filter(ticket -> ticket.getStatus().equals("SOLD"))
            .collect(Collectors.toList());

        List<Merchandise> allMerchandise = merchandiseRepository.findAll();

        if (purchasedTickets.size() < 2 || allMerchandise.isEmpty()) {
            LOGGER.warn("Not enough purchased tickets or merchandise to create purchases.");
            return;
        }

        for (int i = 0; i < 4; i++) { // 2 purchases per user
            // Je 2 gekaufte und reservierte Tickets
            LocalDate cutoffDate = LocalDate.of(2024, 12, 21);
            boolean validSelection = false;

            List<Long> ticketIds = new ArrayList<>();
            int attempts = 0;
            while (!validSelection && attempts < 30) {
                ticketIds.clear();
                ticketIds.addAll(getRandomIds(purchasedTickets, 2));

                boolean allAfterCutoff = ticketIds.stream()
                    .map(ticketRepository::findByTicketId)
                    .allMatch(ticket -> ticket.getDate().isAfter(cutoffDate.atStartOfDay()));

                boolean allBeforeCutoff = ticketIds.stream()
                    .map(ticketRepository::findByTicketId)
                    .allMatch(ticket -> ticket.getDate().isBefore(cutoffDate.atStartOfDay()));

                validSelection = allAfterCutoff || allBeforeCutoff;
                attempts++;
            }

            if (!validSelection) {
                LOGGER.info("Could not find valid tickets for user {} after 10 attempts.", userId);
                return; // Überspringe diesen Benutzer
            }

            // 2 Merchandise-Artikel je Kauf
            List<Long> merchandiseIds = getRandomIds(allMerchandise, 2);

            List<Long> merchandiseQuantities = new ArrayList<>();
            merchandiseQuantities.add(ThreadLocalRandom.current().nextLong(1, 6));
            merchandiseQuantities.add(ThreadLocalRandom.current().nextLong(1, 6));

            // Berechnung des Gesamtpreises
            Long totalPrice = calculateTotalPrice(ticketIds, merchandiseIds);

            // Erstellung des Kaufs
            Purchase purchase = new Purchase(
                userId,
                ticketIds,
                merchandiseIds,
                totalPrice,
                getRandomPastDate(),
                merchandiseQuantities
            );

            purchaseRepository.save(purchase);
            LOGGER.info("Created purchase for user {}: {}", userId, purchase);
        }
    }


    private List<Long> getRandomIds(List<?> items, int count) {
        return random.ints(0, items.size())
            .distinct()
            .limit(count)
            .mapToObj(i -> {
                if (items.get(0) instanceof Ticket) { // Prüfe das erste Element
                    return ((Ticket) items.get(i)).getTicketId();
                } else if (items.get(0) instanceof Merchandise) {
                    return ((Merchandise) items.get(i)).getMerchandiseId();
                }
                throw new IllegalArgumentException("Unsupported item type in list.");
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

    private LocalDateTime getRandomPastDate() {
        return LocalDateTime.now().minusDays(random.nextInt(365 * 3)).minusHours(random.nextInt(24 * 60 * 60)).minusMinutes(random.nextInt(60 * 60));
    }
}
