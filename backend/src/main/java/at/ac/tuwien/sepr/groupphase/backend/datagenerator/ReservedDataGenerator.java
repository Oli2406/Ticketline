package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservedRepository;
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
public class ReservedDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseDataGenerator.class);

    private final ReservedRepository reservedRepository;
    private final TicketRepository ticketRepository;
    private final Random random = new Random();

    public ReservedDataGenerator(ReservedRepository reservedRepository, TicketRepository ticketRepository) {
        this.reservedRepository = reservedRepository;
        this.ticketRepository = ticketRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        int userCount = 7;

        if (reservedRepository.count() > 0) {
            return;
        }

        for (long userId = 1; userId <= userCount; userId++) {
            createReservationsForUser(userId);
        }
    }

    private void createReservationsForUser(Long userId) {
        List<Ticket> reservedTickets = ticketRepository.findAll().stream()
            .filter(ticket -> ticket.getStatus().equals("RESERVED"))
            .collect(Collectors.toList());

        if (reservedTickets.size() < 2) {
            LOGGER.warn("Not enough reserved tickets to create reservation.");
            return;
        }

        for (int i = 0; i < 4; i++) { // 2 purchases per user
            // Je 2 gekaufte und reservierte Tickets
            LocalDate cutoffDate = LocalDate.of(2025, 6, 23);
            boolean validSelection = false;

            List<Long> ticketIds = new ArrayList<>();
            int attempts = 0;
            while (!validSelection && attempts < 30) {
                ticketIds.clear();
                ticketIds.addAll(getRandomIds(reservedTickets, 2));

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

            // Berechnung des Gesamtpreises
            Long totalPrice = calculateTotalPrice(ticketIds);

            // Erstellung des Kaufs
            Reservation reserved = new Reservation(
                userId,
                ticketIds,
                getRandomPastDate()
            );

            reservedRepository.save(reserved);
            LOGGER.info("Created reservation for user {}: {}", userId, reserved);
        }
    }


    private List<Long> getRandomIds(List<?> items, int count) {
        return random.ints(0, items.size())
            .distinct()
            .limit(count)
            .mapToObj(i -> {
                if (items.get(0) instanceof Ticket) { // Prüfe das erste Element
                    return ((Ticket) items.get(i)).getTicketId();
                }
                throw new IllegalArgumentException("Unsupported item type in list.");
            })
            .collect(Collectors.toList());
    }

    private Long calculateTotalPrice(List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        long ticketTotal = tickets.stream().mapToLong(ticket -> ticket.getPrice().longValue()).sum();

        return ticketTotal;
    }

    private LocalDateTime getRandomPastDate() {
        return LocalDateTime.now().minusDays(random.nextInt(365 * 3)).minusHours(random.nextInt(24)).minusMinutes(random.nextInt(60));
    }
}
