package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservedRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservedService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservedServiceImpl implements ReservedService {

    private static final Logger logger = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final ReservedRepository reservedRepository;
    private final TicketRepository ticketRepository;
    private final RandomStringGenerator generator;
    private final TicketService ticketService;
    private final PerformanceRepository performanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;

    public ReservedServiceImpl(ReservedRepository reservedRepository,
                               TicketRepository ticketRepository,
                               RandomStringGenerator generator, TicketService ticketService, PerformanceRepository performanceRepository, ArtistRepository artistRepository, LocationRepository locationRepository) {
        this.reservedRepository = reservedRepository;
        this.ticketRepository = ticketRepository;
        this.generator = generator;
        this.ticketService = ticketService;
        this.performanceRepository = performanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public ReservedDetailDto getReservedById(Long reservationId) {
        logger.info("Fetching reservation with ID: {}", reservationId);

        Reservation reservation = reservedRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        logger.debug("Fetched reservation: {}", reservation);

        List<Ticket> tickets = ticketRepository.findAllById(reservation.getTicketIds());

        return new ReservedDetailDto(
            reservation.getUserId(),
            reservation.getReservationDate(),
            tickets,
            reservationId
        );
    }

    @Override
    public List<ReservedDetailDto> getReservationsByUserId(Long userId) {
        logger.info("Fetching reservations for user with ID: {}", userId);

        List<Reservation> reservations = reservedRepository.findByUserId(userId);

        return reservations.stream().map(reservation -> {
            List<Ticket> tickets = ticketRepository.findAllById(reservation.getTicketIds());

            return new ReservedDetailDto(
                reservation.getUserId(),
                reservation.getReservationDate(),
                tickets,
                reservation.getReservationId()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReservedDetailDto createReservation(ReservedCreateDto reservedCreateDto) throws ValidationException {
        logger.info("Creating reservation: {}", reservedCreateDto);


        // Lock and validate tickets
        List<Ticket> tickets = ticketRepository.findByIdsWithLock(reservedCreateDto.getTicketIds());
        List<Long> unavailableTickets = tickets.stream()
            .filter(ticket -> !ticket.getStatus().equals("AVAILABLE"))
            .map(Ticket::getTicketId)
            .toList();

        if (!unavailableTickets.isEmpty()) {
            logger.error("Some tickets are not available: {}", unavailableTickets);
            throw new ValidationException("Some tickets are not available for reservation: ", List.of(unavailableTickets.toString()));
        }

        // Update ticket statuses
        tickets.forEach(ticket -> ticket.setStatus("RESERVED"));
        ticketRepository.saveAll(tickets);

        Long userId = generator.retrieveOriginalId(reservedCreateDto.getUserId())
            .orElseThrow(() -> new ValidationException("Invalid user ID",
                List.of("User ID could not be resolved.",
                    "Ensure that the encrypted ID is correct."
                )));

        // Create reservation
        Reservation reservation = new Reservation(
            userId,
            reservedCreateDto.getTicketIds(),
            reservedCreateDto.getReservedDate().plusHours(1)
        );

        reservation = reservedRepository.save(reservation);

        logger.info("Reservation created: {}", reservation);

        return new ReservedDetailDto(
            reservation.getUserId(),
            reservation.getReservationDate(),
            tickets,
            reservation.getReservationId()
        );
    }


    @Override
    public void updateReservation(ReservedDetailDto reservedDetailDto) {
        Reservation existingReservation = reservedRepository.findById(reservedDetailDto.getReservedId())
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        List<Long> oldTickets = existingReservation.getTicketIds();

        List<Long> ticketIds = reservedDetailDto.getTickets()
            .stream()
            .map(Ticket::getTicketId)
            .collect(Collectors.toList());

        List<Long> cancelledTickets = oldTickets.stream()
            .filter(oldTicket -> !ticketIds.contains(oldTicket))
            .collect(Collectors.toList());

        if (!cancelledTickets.isEmpty()) {
            this.ticketService.updateTicketStatusList(cancelledTickets, "AVAILABLE");
        }

        if (ticketIds.isEmpty()) {
            reservedRepository.deleteById(existingReservation.getReservationId());
            logger.info("Deleted reservation with ID: {}", existingReservation.getReservationId());
            return;
        }

        existingReservation.setTicketIds(ticketIds);
        reservedRepository.save(existingReservation);

        logger.info("Updated reservation: {}", existingReservation);
    }

    @Override
    public void deleteTicketFromReservation(Long reservationId, Long ticketId) {
        logger.info("Deleting ticket {} from reservation {}", ticketId, reservationId);

        // Fetch the reservation by ID
        Reservation reservation = reservedRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

        // Get the list of current tickets in the reservation
        List<Long> ticketIds = reservation.getTicketIds();

        // Check if the ticket is present in the reservation
        if (!ticketIds.contains(ticketId)) {
            throw new IllegalArgumentException("Ticket not found in reservation with ID: " + ticketId);
        }

        // Remove the ticket from the list
        ticketIds.remove(ticketId);

        // If the reservation becomes empty after removing the ticket, delete the reservation
        if (ticketIds.isEmpty()) {
            reservedRepository.delete(reservation);
            logger.info("Deleted reservation {} as it has no remaining tickets", reservationId);
        } else {
            // Otherwise, update the reservation
            reservation.setTicketIds(ticketIds);
            reservedRepository.save(reservation);
            logger.info("Updated reservation {} after removing ticket {}", reservationId, ticketId);
        }
    }

    @Override
    public List<ReservationOverviewDto> getReservationDetailsByUser(Long userId) {
        logger.info("Fetching detailed reservations for user with ID: {}", userId);

        List<Reservation> reservations = reservedRepository.findByUserId(userId);

        return reservations.stream().map(reservation -> {
            List<Ticket> tickets = ticketRepository.findAllById(reservation.getTicketIds());

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

            return new ReservationOverviewDto(
                reservation.getReservationId(),
                reservation.getUserId(),
                tickets,
                reservation.getReservationDate(),
                performanceDetails
            );
        }).collect(Collectors.toList());
    }
}

