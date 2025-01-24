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
import java.util.Collections;
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
        int userCount = 100;

        if (reservedRepository.count() > 0) {
            return;
        }

        // Pre-fetch reserved tickets from the database
        List<Ticket> reservedTickets = ticketRepository.findReservedTickets();

        if (reservedTickets.size() < 2) {
            LOGGER.warn("Not enough reserved tickets to create reservations.");
            return;
        }

        // Split tickets into two groups: before and after cutoff date
        LocalDate cutoffDate = LocalDate.of(2025, 6, 23);
        List<Ticket> ticketsBeforeCutoff = reservedTickets.stream()
            .filter(ticket -> ticket.getDate().isBefore(cutoffDate.atStartOfDay()))
            .collect(Collectors.toList());
        List<Ticket> ticketsAfterCutoff = reservedTickets.stream()
            .filter(ticket -> ticket.getDate().isAfter(cutoffDate.atStartOfDay()))
            .collect(Collectors.toList());

        // Create reservations for each user
        for (long userId = 1; userId <= userCount; userId++) {
            createReservationsForUser(userId, ticketsBeforeCutoff, ticketsAfterCutoff);
        }
    }

    private void createReservationsForUser(Long userId, List<Ticket> ticketsBeforeCutoff, List<Ticket> ticketsAfterCutoff) {
        if (ticketsBeforeCutoff.size() < 2 && ticketsAfterCutoff.size() < 2) {
            LOGGER.warn("Not enough valid tickets for user {}.", userId);
            return;
        }

        for (int i = 0; i < 4; i++) { // 4 reservations per user
            // Randomly select a group: before or after cutoff
            List<Ticket> selectedGroup = random.nextBoolean() ? ticketsBeforeCutoff : ticketsAfterCutoff;

            if (selectedGroup.size() < 2) {
                continue;
            }

            // Select 2 random tickets
            List<Ticket> selectedTickets = getRandomSubset(selectedGroup, 2);


            // Create reservation
            Reservation reservation = new Reservation(
                userId,
                selectedTickets.stream().map(Ticket::getTicketId).collect(Collectors.toList()),
                getRandomPastDate()
            );

            reservedRepository.save(reservation);
        }
        LOGGER.info("Reservations for user {} created", userId);
    }

    private List<Ticket> getRandomSubset(List<Ticket> tickets, int count) {
        List<Ticket> copy = new ArrayList<>(tickets);
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
