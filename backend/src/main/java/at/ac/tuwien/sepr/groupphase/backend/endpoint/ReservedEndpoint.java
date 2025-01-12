package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservedService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(ReservedEndpoint.BASE_PATH)
public class ReservedEndpoint {

    public static final String BASE_PATH = "/api/v1/reserved";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReservedService reservedService;
    private final RandomStringGenerator randomStringGenerator;
    private final TicketService ticketService;

    public ReservedEndpoint(ReservedService reservedService, RandomStringGenerator randomStringGenerator,
                            TicketService ticketService) {
        this.reservedService = reservedService;
        this.randomStringGenerator = randomStringGenerator;
        this.ticketService = ticketService;
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<ReservedDetailDto> getReservedById(@PathVariable Long id) {
        LOG.info("Fetching Purchase by ID: {}", id);
        ReservedDetailDto reserve = reservedService.getReservedById(id);
        LOG.info("Successfully fetched Purchase: {}", reserve);
        return ResponseEntity.ok(reserve);
    }

    @PermitAll
    @GetMapping("/user/{encryptedUserId}")
    public ResponseEntity<List<ReservedDetailDto>> getReservationsByUser(@PathVariable String encryptedUserId) {
        LOG.info("Fetching purchases for user with encrypted ID: {}", encryptedUserId);
        Long userId = randomStringGenerator.retrieveOriginalId(encryptedUserId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));
        List<ReservedDetailDto> reservations = reservedService.getReservationsByUserId(userId);
        LOG.info("Fetched {} purchases for user with ID: {}", reservations.size(), userId);
        return ResponseEntity.ok(reservations);
    }


    @PermitAll
    @PostMapping
    public ResponseEntity<ReservedDetailDto> createReservation(@RequestBody ReservedCreateDto reservedCreateDto) throws ValidationException {
        LOG.info("Received request to create or update Purchase: {}", reservedCreateDto);
        ticketService.updateTicketStatusList(reservedCreateDto.getTicketIds(), "RESERVED");
        ReservedDetailDto createdReservation = reservedService.createReservation(reservedCreateDto);
        LOG.info("Successfully created/updated Purchase: {}", createdReservation);
        return ResponseEntity.ok(createdReservation);
    }

    @PermitAll
    @DeleteMapping("/{reservationId}/ticket/{ticketId}")
    public ResponseEntity<Void> deleteTicketFromReservation(@PathVariable Long reservationId, @PathVariable Long ticketId) {
        LOG.info("Received request to delete ticket {} from reservation {}", ticketId, reservationId);
        reservedService.deleteTicketFromReservation(reservationId, ticketId);
        LOG.info("Successfully deleted ticket {} from reservation {}", ticketId, reservationId);
        return ResponseEntity.noContent().build();
    }

}

