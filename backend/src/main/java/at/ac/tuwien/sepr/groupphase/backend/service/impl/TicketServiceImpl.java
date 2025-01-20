package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;

import at.ac.tuwien.sepr.groupphase.backend.service.validators.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final TicketValidator ticketValidator;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketValidator ticketValidator) {
        this.ticketRepository = ticketRepository;
        this.ticketValidator = ticketValidator;
    }

    @Override
    public TicketDetailDto createTicket(TicketCreateDto ticketCreateDto)
        throws ValidationException, ConflictException {
        LOGGER.info("Creating or updating ticket: {}", ticketCreateDto);

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
        LOGGER.debug("Saved ticket to database: {}", ticket);

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
        LOGGER.info("Fetching all tickets");

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

        LOGGER.debug("Fetched {} tickets: {}", tickets.size(), tickets);
        return tickets;
    }

    @Override
    public TicketDetailDto getTicketById(Long id) {
        LOGGER.info("Fetching ticket with ID: {}", id);

        // Ticket anhand der ID abrufen
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        LOGGER.debug("Fetched ticket: {}", ticket);

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
        LOGGER.info("Fetching tickets for performance ID: {}", performanceId);

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

        LOGGER.debug("Fetched {} tickets for performance ID {}: {}", tickets.size(), performanceId,
            tickets);
        return tickets;
    }

    @Override
    @Transactional
    public TicketDetailDto updateTicket(Long ticketId, TicketCreateDto ticketCreateDto)
        throws ValidationException, ConflictException {
        LOGGER.info("Updating ticket with ID: {}", ticketId);

        // Validate the incoming ticket data
        ticketValidator.validateTicket(ticketCreateDto);

        // Lock the ticket row to prevent concurrent updates
        Ticket existingTicket = ticketRepository.findByIdWithLock(ticketId)
            .orElseThrow(() -> {
                LOGGER.error("Ticket not found with ID: {}", ticketId);
                return new IllegalArgumentException("Ticket not found with ID: " + ticketId);
            });

        // Check if the ticket is available if the status is being updated to RESERVED
        if (Objects.equals(ticketCreateDto.getStatus(), "RESERVED") && !existingTicket.getStatus().equals("AVAILABLE")) {
            LOGGER.error("Ticket with ID {} is not available for reservation.", ticketId);
            throw new ConflictException("Ticket is not available for reservation.", new ArrayList<>());
        }

        // Update the ticket details
        existingTicket.setRowNumber(ticketCreateDto.getRowNumber());
        existingTicket.setSeatNumber(ticketCreateDto.getSeatNumber());
        existingTicket.setPriceCategory(ticketCreateDto.getPriceCategory());
        existingTicket.setTicketType(ticketCreateDto.getTicketType());
        existingTicket.setSectorType(ticketCreateDto.getSectorType());
        existingTicket.setPrice(ticketCreateDto.getPrice());
        existingTicket.setStatus(ticketCreateDto.getStatus());
        existingTicket.setPerformanceId(ticketCreateDto.getPerformanceId());
        existingTicket.setReservationNumber(ticketCreateDto.getReservationNumber());
        existingTicket.setHall(ticketCreateDto.getHall());
        existingTicket.setDate(ticketCreateDto.getDate());

        // Save the updated ticket
        Ticket savedTicket = ticketRepository.save(existingTicket);
        LOGGER.debug("Updated ticket saved to database: {}", savedTicket);

        // Map the saved ticket to a DTO
        return new TicketDetailDto(
            savedTicket.getTicketId(),
            savedTicket.getPerformanceId(),
            savedTicket.getRowNumber(),
            savedTicket.getSeatNumber(),
            savedTicket.getPriceCategory(),
            savedTicket.getTicketType(),
            savedTicket.getSectorType(),
            savedTicket.getPrice(),
            savedTicket.getStatus(),
            savedTicket.getHall(),
            savedTicket.getReservationNumber(),
            savedTicket.getDate()
        );
    }


    @Override
    public void deleteTicket(Long id) {
        LOGGER.info("Deleting ticket with ID: {}", id);

        // Ticket löschen
        ticketRepository.deleteById(id);
        LOGGER.debug("Deleted ticket with ID: {}", id);
    }

    @Override
    public void updateTicketStatusList(List<Long> ticketIds, String status) {
        if (!List.of("SOLD", "RESERVED", "AVAILABLE").contains(status)) {
            throw new IllegalArgumentException("Status is invalid");
        }
        ticketIds.stream()
            .map(ticketRepository::findByTicketId)
            .forEach(ticket -> {
                if (ticket != null) {
                    ticket.setStatus(status);
                    ticketRepository.save(ticket);
                } else {
                    LOGGER.error("Ticket with ID {} not found", ticket);
                }
            });
    }
}
