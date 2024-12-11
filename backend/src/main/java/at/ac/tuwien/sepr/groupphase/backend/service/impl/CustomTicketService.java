package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomTicketService implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final TicketValidator ticketValidator;

    public CustomTicketService(TicketRepository ticketRepository, TicketValidator ticketValidator) {
        this.ticketRepository = ticketRepository;
        this.ticketValidator = ticketValidator;
    }

    @Override
    public TicketDetailDto createTicket(TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException {
        logger.info("Creating or updating ticket: {}", ticketCreateDto);

        // Validierung der Ticketdaten
        ticketValidator.validateTicket(ticketCreateDto);

        // Ticket erstellen oder aktualisieren
        Ticket ticket = new Ticket(
            ticketCreateDto.getPerformanceId(),
            ticketCreateDto.getRowNumber(),
            ticketCreateDto.getSeatNumber(),
            ticketCreateDto.getPriceCategory(),
            ticketCreateDto.getTicketType(),
            ticketCreateDto.getSectorType(),
            ticketCreateDto.getPrice(),
            ticketCreateDto.getStatus(),
            ticketCreateDto.getHall(),
            ticketCreateDto.getReservationNumber(),
            ticketCreateDto.getDate()
        );

        ticket = ticketRepository.save(ticket);
        logger.debug("Saved ticket to database: {}", ticket);

        // Rückgabe als TicketDetailDto
        return new TicketDetailDto(
            ticket.getTicketId(),
            ticket.getPerformanceId(),
            ticket.getRowNumber(),
            ticket.getSeatNumber(),
            ticket.getPriceCategory(),
            ticket.getTicketType(),
            ticket.getSectorType(),
            ticket.getPrice(),
            ticket.getStatus(),
            ticket.getHall(),
            ticket.getReservationNumber(),
            ticket.getDate()
        );
    }

    @Override
    public List<TicketDetailDto> getAllTickets() {
        logger.info("Fetching all tickets");

        // Alle Tickets abrufen und als DTOs zurückgeben
        List<TicketDetailDto> tickets = ticketRepository.findAll().stream()
            .map(ticket -> new TicketDetailDto(
                ticket.getTicketId(),
                ticket.getPerformanceId(),
                ticket.getRowNumber(),
                ticket.getSeatNumber(),
                ticket.getPriceCategory(),
                ticket.getTicketType(),
                ticket.getSectorType(),
                ticket.getPrice(),
                ticket.getStatus(),
                ticket.getHall(),
                ticket.getReservationNumber(),
                ticket.getDate()
            ))
            .collect(Collectors.toList());

        logger.debug("Fetched {} tickets: {}", tickets.size(), tickets);
        return tickets;
    }

    @Override
    public TicketDetailDto getTicketById(Long id) {
        logger.info("Fetching ticket with ID: {}", id);

        // Ticket anhand der ID abrufen
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        logger.debug("Fetched ticket: {}", ticket);

        // Rückgabe als TicketDetailDto
        return new TicketDetailDto(
            ticket.getTicketId(),
            ticket.getPerformanceId(),
            ticket.getRowNumber(),
            ticket.getSeatNumber(),
            ticket.getPriceCategory(),
            ticket.getTicketType(),
            ticket.getSectorType(),
            ticket.getPrice(),
            ticket.getStatus(),
            ticket.getHall(),
            ticket.getReservationNumber(),
            ticket.getDate()
        );
    }

    @Override
    public List<TicketDetailDto> getTicketsByPerformanceId(Long performanceId) {
        logger.info("Fetching tickets for performance ID: {}", performanceId);

        // Tickets anhand der Performance-ID abrufen
        List<TicketDetailDto> tickets = ticketRepository.findByPerformanceId(performanceId).stream()
            .map(ticket -> new TicketDetailDto(
                ticket.getTicketId(),
                ticket.getPerformanceId(),
                ticket.getRowNumber(),
                ticket.getSeatNumber(),
                ticket.getPriceCategory(),
                ticket.getTicketType(),
                ticket.getSectorType(),
                ticket.getPrice(),
                ticket.getStatus(),
                ticket.getHall(),
                ticket.getReservationNumber(),
                ticket.getDate()
            ))
            .collect(Collectors.toList());

        logger.debug("Fetched {} tickets for performance ID {}: {}", tickets.size(), performanceId, tickets);
        return tickets;
    }

    @Override
    public void deleteTicket(Long id) {
        logger.info("Deleting ticket with ID: {}", id);

        // Ticket löschen
        ticketRepository.deleteById(id);
        logger.debug("Deleted ticket with ID: {}", id);
    }
}
