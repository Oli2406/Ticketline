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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Profile("generateData")
@DependsOn("performanceDataGenerator")
public class TicketDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketDataGenerator.class);
    private static final String[] STATUSES = {"AVAILABLE", "RESERVED", "SOLD"};
    private static final int BATCH_SIZE = 1000;
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

        // Parallelize ticket generation for performances
        performances.parallelStream().forEach(this::createTicketsForPerformance);
        LOGGER.info("Generated tickets for all performances");
    }

    private void createTicketsForPerformance(Performance performance) {
        // Fetch existing tickets for the performance
        Set<String> existingTicketKeys = fetchExistingTicketKeys(performance.getPerformanceId());
        List<Ticket> tickets = new ArrayList<>();

        if ("A".equals(performance.getHall())) {
            // Generate seated tickets for Hall A
            tickets.addAll(generateSeatedTickets(performance, 12, 20, PriceCategory.PREMIUM, SectorType.B, BigDecimal.valueOf(120), Hall.A, existingTicketKeys));
            tickets.addAll(generateSeatedTickets(performance, 12, 20, PriceCategory.PREMIUM, SectorType.C, BigDecimal.valueOf(120), Hall.A, existingTicketKeys));

            // Generate standing tickets for Hall A
            tickets.addAll(generateStandingTickets(performance, 80, PriceCategory.VIP, SectorType.A, BigDecimal.valueOf(150), Hall.A, existingTicketKeys));
            tickets.addAll(generateStandingTickets(performance, 100, PriceCategory.STANDARD, SectorType.A, BigDecimal.valueOf(80), Hall.A, existingTicketKeys));
        } else if ("B".equals(performance.getHall())) {
            // Generate seated tickets for Hall B
            tickets.addAll(generateSeatedTickets(performance, 3, 14, PriceCategory.PREMIUM, SectorType.B, BigDecimal.valueOf(80), Hall.B, existingTicketKeys));
            tickets.addAll(generateDynamicSeatedTickets(performance, 9, 14, PriceCategory.STANDARD, SectorType.C, BigDecimal.valueOf(60), Hall.B, existingTicketKeys));

            // Generate standing tickets for Hall B
            tickets.addAll(generateStandingTickets(performance, 80, PriceCategory.PREMIUM, SectorType.A, BigDecimal.valueOf(70), Hall.B, existingTicketKeys));
            tickets.addAll(generateStandingTickets(performance, 60, PriceCategory.VIP, SectorType.A, BigDecimal.valueOf(100), Hall.B, existingTicketKeys));
        }

        // Calculate total tickets for this performance
        int totalTickets = tickets.size();

        // Generate a custom status distribution for this performance
        int[] statusDistribution = getStatusDistribution(totalTickets);

        // Apply the status distribution to the tickets
        applyStatusDistribution(tickets, statusDistribution);

        saveTicketsInBatches(tickets);
        LOGGER.debug("Generated {} tickets for performance '{}' in hall {}", tickets.size(), performance.getName(), performance.getHall());
    }

    private Set<String> fetchExistingTicketKeys(Long performanceId) {
        return ticketRepository.findByPerformanceId(performanceId).stream()
            .map(ticket -> ticket.getRowNumber() + "-" + ticket.getSeatNumber())
            .collect(Collectors.toSet());
    }

    private List<Ticket> generateSeatedTickets(Performance performance, int rows, int seats, PriceCategory priceCategory, SectorType sectorType, BigDecimal price, Hall hall,
                                               Set<String> existingTicketKeys) {
        List<Ticket> tickets = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int seat = 1; seat <= seats; seat++) {
                String ticketKey = row + "-" + seat;
                if (!existingTicketKeys.contains(ticketKey)) {
                    tickets.add(createTicket(performance, row, seat, priceCategory, TicketType.STANDING, sectorType, price, hall));
                }
            }
        }
        return tickets;
    }

    private List<Ticket> generateDynamicSeatedTickets(Performance performance, int rows, int baseSeats, PriceCategory priceCategory, SectorType sectorType, BigDecimal price, Hall hall,
                                                      Set<String> existingTicketKeys) {
        List<Ticket> tickets = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            int seatsInRow = baseSeats + row;
            for (int seat = 1; seat <= seatsInRow; seat++) {
                String ticketKey = row + "-" + seat;
                if (!existingTicketKeys.contains(ticketKey)) {
                    tickets.add(createTicket(performance, row, seat, priceCategory, TicketType.STANDING, sectorType, price, hall));
                }
            }
        }
        return tickets;
    }

    private List<Ticket> generateStandingTickets(Performance performance, int count, PriceCategory priceCategory,
                                                 SectorType sectorType, BigDecimal price, Hall hall,
                                                 Set<String> existingTicketKeys) {
        return IntStream.range(0, count)
            .mapToObj(i -> createTicket(performance, 0, 0, priceCategory, TicketType.STANDING, sectorType, price, hall))
            .collect(Collectors.toList());
    }

    private Ticket createTicket(Performance performance, int row, int seat, PriceCategory priceCategory, TicketType ticketType,
                                SectorType sectorType, BigDecimal price, Hall hall) {
        return new Ticket(
            performance.getPerformanceId(),
            row,
            seat,
            priceCategory,
            ticketType,
            sectorType,
            price,
            "AVAILABLE", // Status will be set later in applyStatusDistribution
            hall,
            1 + (long) (random.nextDouble() * (99000 - 1 + 1)),
            performance.getDate()
        );
    }

    private void saveTicketsInBatches(List<Ticket> tickets) {
        for (int i = 0; i < tickets.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, tickets.size());
            ticketRepository.saveAll(tickets.subList(i, end));
        }
    }

    private int[] getStatusDistribution(int totalTickets) {
        Random statusRandom = new Random();
        int[] distribution = new int[STATUSES.length];

        int soldPercentage = statusRandom.nextInt(101);
        distribution[2] = (int) (totalTickets * (soldPercentage / 100.0)); // SOLD
        int remaining = totalTickets - distribution[2];
        distribution[1] = statusRandom.nextInt(remaining + 1); // RESERVED
        distribution[0] = remaining - distribution[1]; // AVAILABLE

        return distribution;
    }

    private void applyStatusDistribution(List<Ticket> tickets, int[] distribution) {
        Collections.shuffle(tickets, random);

        int index = 0;
        for (int i = 0; i < STATUSES.length; i++) {
            for (int j = 0; j < distribution[i]; j++) {
                if (index < tickets.size()) {
                    tickets.get(index).setStatus(STATUSES[i]);
                    index++;
                }
            }
        }
    }
}