package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("generateData")
@DependsOn("performanceDataGenerator")
public class TicketDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketDataGenerator.class);

    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final Random random = new Random();

    public TicketDataGenerator(TicketRepository ticketRepository, PerformanceRepository performanceRepository) {
        this.ticketRepository = ticketRepository;
        this.performanceRepository = performanceRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        List<Performance> performances = performanceRepository.findAll();

        if (performances.isEmpty()) {
            LOGGER.warn("No performances available to create tickets.");
            return;
        }

        for (Performance performance : performances) {
            createTicketsForPerformance(performance);
        }
        LOGGER.info("Generated all tickets");
    }

    private void createTicketsForPerformance(Performance performance) {
        String hall = performance.getHall();
        List<Ticket> tickets = new ArrayList<>();

        if ("A".equals(hall)) {
            for (int row = 1; row <= 12; row++) {
                for (int seat = 1; seat <= 20; seat++) {
                    createTicketIfNotExists(performance, row, seat, PriceCategory.PREMIUM, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(120), Hall.A, tickets);
                }
            }

            for (int row = 1; row <= 12; row++) {
                for (int seat = 1; seat <= 20; seat++) {
                    createTicketIfNotExists(performance, row, seat, PriceCategory.PREMIUM, TicketType.SEATED, SectorType.C, BigDecimal.valueOf(120), Hall.A, tickets);
                }
            }

            for (int i = 1; i <= 80; i++) {
                createTicketIfNotExists(performance, 0, 0, PriceCategory.VIP, TicketType.STANDING, SectorType.A, BigDecimal.valueOf(150), Hall.A, tickets);
            }

            for (int i = 1; i <= 100; i++) {
                createTicketIfNotExists(performance, 0, 0, PriceCategory.STANDARD, TicketType.STANDING, SectorType.A, BigDecimal.valueOf(80), Hall.A, tickets);
            }
        } else if ("B".equals(hall)) {
            for (int row = 1; row <= 3; row++) {
                for (int seat = 1; seat <= 14; seat++) {
                    createTicketIfNotExists(performance, row, seat, PriceCategory.PREMIUM, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(80), Hall.B, tickets);
                }
            }

            for (int row = 1; row <= 9; row++) {
                int seatsInRow = 14 + row;
                for (int seat = 1; seat <= seatsInRow; seat++) {
                    createTicketIfNotExists(performance, row, seat, PriceCategory.STANDARD, TicketType.SEATED, SectorType.C, BigDecimal.valueOf(60), Hall.B, tickets);
                }
            }

            for (int i = 1; i <= 80; i++) {
                createTicketIfNotExists(performance, 0, 0, PriceCategory.PREMIUM, TicketType.STANDING, SectorType.A, BigDecimal.valueOf(70), Hall.B, tickets);
            }

            for (int i = 1; i <= 60; i++) {
                createTicketIfNotExists(performance, 0, 0, PriceCategory.VIP, TicketType.STANDING, SectorType.A, BigDecimal.valueOf(100), Hall.B, tickets);
            }
        }

        ticketRepository.saveAll(tickets);
        LOGGER.debug("Generated {} tickets for performance {} in hall {}", tickets.size(), performance.getName(), hall);
    }

    private void createTicketIfNotExists(Performance performance, int row, int seat, PriceCategory priceCategory,
                                         TicketType ticketType, SectorType sectorType, BigDecimal price, Hall hall, List<Ticket> tickets) {

        // Check if a ticket with the same properties already exists
        if (!ticketRepository.existsByPerformanceIdAndRowNumberAndSeatNumber(performance.getPerformanceId(), row, seat)) {

            tickets.add(new Ticket(
                performance.getPerformanceId(),
                row,
                seat,
                priceCategory,
                ticketType,
                sectorType,
                price,
                getRandomTicketStatus(),
                hall,
                1 + (long) (random.nextDouble() * (99000 - 1 + 1)),
                performance.getDate()
            ));
        }
    }

    private int getTicketCountByHall(String hall) {
        switch (hall) {
            case "A":
                return 660; // Hall A capacity
            case "B":
                return 353; // Hall B capacity
            default:
                return 100; // Default capacity for unknown halls
        }
    }

    private String getRandomTicketStatus() {
        String[] statuses = {"AVAILABLE", "RESERVED", "SOLD"};
        return statuses[random.nextInt(statuses.length)];
    }
}