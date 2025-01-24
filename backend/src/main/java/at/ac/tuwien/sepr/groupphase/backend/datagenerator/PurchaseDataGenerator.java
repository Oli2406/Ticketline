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
import java.util.Collections;
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

    public PurchaseDataGenerator(PurchaseRepository purchaseRepository,
                                 TicketRepository ticketRepository, MerchandiseRepository merchandiseRepository) {
        this.purchaseRepository = purchaseRepository;
        this.ticketRepository = ticketRepository;
        this.merchandiseRepository = merchandiseRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        int userCount = 100;

        if (purchaseRepository.count() > 0) {
            return;
        }

        // Pre-fetch sold tickets and merchandise
        List<Ticket> soldTickets = ticketRepository.findSoldTickets();
        List<Merchandise> allMerchandise = merchandiseRepository.findAll();

        if (soldTickets.size() < 2 || allMerchandise.isEmpty()) {
            LOGGER.warn("Not enough sold tickets or merchandise to create purchases.");
            return;
        }

        // Split tickets into two groups: before and after cutoff date
        LocalDate cutoffDate = LocalDate.of(2025, 6, 23);
        List<Ticket> ticketsBeforeCutoff = soldTickets.stream()
            .filter(ticket -> ticket.getDate().isBefore(cutoffDate.atStartOfDay()))
            .collect(Collectors.toList());
        List<Ticket> ticketsAfterCutoff = soldTickets.stream()
            .filter(ticket -> ticket.getDate().isAfter(cutoffDate.atStartOfDay()))
            .collect(Collectors.toList());

        // Create purchases for each user
        for (long userId = 1; userId <= userCount; userId++) {
            createPurchasesForUser(userId, ticketsBeforeCutoff, ticketsAfterCutoff, allMerchandise);
        }
    }

    private void createPurchasesForUser(Long userId, List<Ticket> ticketsBeforeCutoff,
                                        List<Ticket> ticketsAfterCutoff, List<Merchandise> allMerchandise) {
        for (int i = 0; i < 4; i++) { // 4 purchases per user
            // Randomly select a group: before or after cutoff
            List<Ticket> selectedGroup = random.nextBoolean() ? ticketsBeforeCutoff : ticketsAfterCutoff;

            if (selectedGroup.size() < 2) {
                LOGGER.warn("Not enough tickets for user {}.", userId);
                continue;
            }

            // Select 2 random tickets
            List<Ticket> selectedTickets = getRandomSubset(selectedGroup, 2);

            // Select 2 random merchandise items
            List<Merchandise> selectedMerchandise = getRandomSubset(allMerchandise, 2);

            // Random quantities for merchandise
            List<Long> merchandiseQuantities = selectedMerchandise.stream()
                .map(item -> ThreadLocalRandom.current().nextLong(1, 6))
                .collect(Collectors.toList());

            // Calculate total price
            long ticketTotal = selectedTickets.stream()
                .mapToLong(ticket -> ticket.getPrice().longValue())
                .sum();
            long merchandiseTotal = selectedMerchandise.stream()
                .mapToLong(item -> item.getPrice().longValue())
                .sum();
            long totalPrice = ticketTotal + merchandiseTotal;

            // Create purchase
            Purchase purchase = new Purchase(
                userId,
                selectedTickets.stream().map(Ticket::getTicketId).collect(Collectors.toList()),
                selectedMerchandise.stream().map(Merchandise::getMerchandiseId).collect(Collectors.toList()),
                totalPrice,
                getRandomPastDate(),
                merchandiseQuantities,
                "ExampleStreet",
                "ExamplePostalCode",
                "ExampleCity"
            );

            purchaseRepository.save(purchase);
        }
        LOGGER.info("Purchases for user {} created", userId);
    }

    private <T> List<T> getRandomSubset(List<T> items, int count) {
        List<T> copy = new ArrayList<>(items);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(count, copy.size()));
    }

    private LocalDateTime getRandomPastDate() {
        return LocalDateTime.now()
            .minusDays(ThreadLocalRandom.current().nextInt(365 * 3))
            .minusHours(ThreadLocalRandom.current().nextInt(24))
            .minusMinutes(ThreadLocalRandom.current().nextInt(60));
    }
}
