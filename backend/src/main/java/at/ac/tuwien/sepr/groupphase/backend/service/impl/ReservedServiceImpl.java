package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
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

    private static final Logger logger = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final ReservedRepository reservedRepository;
    private final TicketRepository ticketRepository;
    private final RandomStringGenerator generator;

    public ReservedServiceImpl(ReservedRepository reservedRepository,
        TicketRepository ticketRepository,
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

    @Override
    public ReservedDetailDto createReservation(ReservedCreateDto reservedCreateDto)
        throws ValidationException {
        logger.info("Creating reservation: {}", reservedCreateDto);

        Optional<Long> optionalUserId = generator.retrieveOriginalId(reservedCreateDto.getUserId());
        Long userId = optionalUserId.orElseThrow(
            () -> new ValidationException("Invalid user ID", List.of(
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
    public void updateReservation(ReservedDetailDto reservedDetailDto) {
        // Fetch the existing purchase
        Reservation existingReservation = reservedRepository.findById(
                reservedDetailDto.getReservedId())
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        List<Long> ticketIds = new java.util.ArrayList<>(List.of());
        List<Ticket> tickets = reservedDetailDto.getTickets();

        for (Ticket ticket : tickets) {
            ticketIds.add(ticket.getTicketId());
        }

        existingReservation.setTicketIds(ticketIds);
        reservedRepository.save(existingReservation);
        logger.info("Updated reservation: {}", existingReservation);
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
