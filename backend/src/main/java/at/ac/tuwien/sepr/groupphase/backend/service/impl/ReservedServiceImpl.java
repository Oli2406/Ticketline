package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservedRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservedServiceImpl implements ReservedService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReservedRepository reservedRepository;
    private final TicketRepository ticketRepository;
    private final RandomStringGenerator generator;

    public ReservedServiceImpl(ReservedRepository reservedRepository, TicketRepository ticketRepository,
                                 RandomStringGenerator generator) {
        this.reservedRepository = reservedRepository;
        this.ticketRepository = ticketRepository;
        this.generator = generator;
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
            reservation.getUserId()
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
                reservation.getUserId()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public ReservedDetailDto createReservation(ReservedCreateDto reservedCreateDto) throws ValidationException {
        logger.info("Creating reservation: {}", reservedCreateDto);

        Optional<Long> optionalUserId = generator.retrieveOriginalId(reservedCreateDto.getUserId());
        Long userId = optionalUserId.orElseThrow(() -> new ValidationException("Invalid user ID", List.of(
            "User ID could not be resolved.",
            "Ensure that the encrypted ID is correct."
        )));

        Reservation reservation = new Reservation(
            userId,
            reservedCreateDto.getTicketIds(),
            reservedCreateDto.getReservedDate().plusHours(1)
        );

        logger.debug("Mapped Reservation entity: {}", reservation);

        reservation = reservedRepository.save(reservation);

        List<Ticket> tickets = ticketRepository.findAllById(reservation.getTicketIds());

        logger.info("Saved reservation to database: {}", reservation);

        return new ReservedDetailDto(
            reservation.getUserId(),
            reservation.getReservationDate(),
            tickets,
            reservation.getUserId()
        );
    }

    @Override
    public void deleteTicketFromReservation(Long reservationId, Long ticketId) {
        logger.info("Deleting ticket {} from reservation {}", ticketId, reservationId);

        // Fetch the reservation by ID
        Reservation reservation = reservedRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

        // Check if the ticket is present in the reservation
        boolean removed = reservation.getTicketIds().removeIf(id -> id.equals(ticketId));

        if (!removed) {
            throw new IllegalArgumentException("Ticket not found in reservation with ID: " + ticketId);
        }

        // If the reservation becomes empty after removing the ticket, delete the reservation
        if (reservation.getTicketIds().isEmpty()) {
            reservedRepository.delete(reservation);
            logger.info("Deleted reservation {} as it has no remaining tickets", reservationId);
        } else {
            // Otherwise, update the reservation
            reservedRepository.save(reservation);
            logger.info("Updated reservation {} after removing ticket {}", reservationId, ticketId);
        }

        logger.info("Updated ticket {} status to AVAILABLE", ticketId);
    }


    /*
    @Override
    public void deleteReservation(Long reservationId) {
        logger.info("Deleting reservation with ID: {}", reservationId);
        if (!reservedRepository.existsById(reservationId)) {
            throw new IllegalArgumentException("Reservation not found");
        }
        reservedRepository.deleteById(reservationId);
        logger.debug("Deleted reservation with ID: {}", reservationId);
    }*/
}
