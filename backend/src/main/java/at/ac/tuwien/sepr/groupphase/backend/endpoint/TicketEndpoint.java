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

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket")
public class TicketEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<TicketDetailDto> createTicket(@RequestBody TicketCreateDto ticketCreateDto) throws ValidationException, ConflictException {
        logger.info("Received request to create or update ticket: {}", ticketCreateDto);
        TicketDetailDto createdTicket = ticketService.createTicket(ticketCreateDto);
        logger.debug("Ticket created/updated successfully: {}", createdTicket);
        return ResponseEntity.ok(createdTicket);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<TicketDetailDto>> getAllTickets() {
        logger.info("Fetching all tickets");
        List<TicketDetailDto> tickets = ticketService.getAllTickets();
        logger.debug("Fetched {} tickets: {}", tickets.size(), tickets);
        return ResponseEntity.ok(tickets);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDto> getTicketById(@PathVariable Long id) {
        logger.info("Fetching ticket with ID: {}", id);
        TicketDetailDto ticket = ticketService.getTicketById(id);
        logger.debug("Fetched ticket: {}", ticket);
        return ResponseEntity.ok(ticket);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/performance/{performanceId}")
    public ResponseEntity<List<TicketDetailDto>> getTicketsByPerformanceId(@PathVariable Long performanceId) {
        logger.info("Fetching tickets for performance ID: {}", performanceId);
        List<TicketDetailDto> tickets = ticketService.getTicketsByPerformanceId(performanceId);
        logger.debug("Fetched {} tickets for performance ID {}: {}", tickets.size(), performanceId, tickets);
        return ResponseEntity.ok(tickets);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        logger.info("Deleting ticket with ID: {}", id);
        ticketService.deleteTicket(id);
        logger.debug("Ticket with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
