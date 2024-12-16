package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket")
public class TicketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<TicketDetailDto> createTicket(@RequestBody TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("Received request to create or update ticket: {}", ticketCreateDto);
        TicketDetailDto createdTicket = ticketService.createTicket(ticketCreateDto);
        LOGGER.debug("Ticket created/updated successfully: {}", createdTicket);
        return ResponseEntity.ok(createdTicket);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<TicketDetailDto>> getAllTickets() {
        LOGGER.info("Fetching all tickets");
        List<TicketDetailDto> tickets = ticketService.getAllTickets();
        LOGGER.debug("Fetched {} tickets: {}", tickets.size(), tickets);
        return ResponseEntity.ok(tickets);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDto> getTicketById(@PathVariable Long id) {
        LOGGER.info("Fetching ticket with ID: {}", id);
        TicketDetailDto ticket = ticketService.getTicketById(id);
        LOGGER.debug("Fetched ticket: {}", ticket);
        return ResponseEntity.ok(ticket);
    }

    @PermitAll
    @GetMapping("/performance/{performanceId}")
    public ResponseEntity<List<TicketDetailDto>> getTicketsByPerformanceId(@PathVariable Long performanceId) {
        LOGGER.info("Fetching tickets for performance ID: {}", performanceId);
        List<TicketDetailDto> tickets = ticketService.getTicketsByPerformanceId(performanceId);
        LOGGER.debug("Fetched {} tickets for performance ID {}: {}", tickets.size(), performanceId, tickets);
        return ResponseEntity.ok(tickets);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        LOGGER.info("Deleting ticket with ID: {}", id);
        ticketService.deleteTicket(id);
        LOGGER.debug("Ticket with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PermitAll
    @PutMapping("/{id}")
    public ResponseEntity<TicketDetailDto> updateTicket(
        @PathVariable Long id,
        @RequestBody TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("Updating ticket with ID: {}", id);
        TicketDetailDto updatedTicket = ticketService.updateTicket(id, ticketCreateDto);
        LOGGER.debug("Updated ticket successfully: {}", updatedTicket);
        return ResponseEntity.ok(updatedTicket);
    }
}
