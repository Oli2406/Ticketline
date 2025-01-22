package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.ReservedEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservedRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservedService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ReservedServiceImpl;
import org.hibernate.Remove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservedEndpointTest {

    @Mock
    private ReservedService reservedService;

    @Mock
    private RandomStringGenerator randomStringGenerator;

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReservedRepository reservedRepository;

    @Mock
    private RandomStringGenerator generator;

    @InjectMocks
    private ReservedEndpoint reservedEndpoint;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Ticket createMockTicket(Long performanceId, Integer rowNumber, Integer seatNumber) {
        return new Ticket(
            performanceId,
            rowNumber,
            seatNumber,
            PriceCategory.VIP,
            TicketType.SEATED,
            SectorType.B,
            BigDecimal.valueOf(50.00)
        );
    }

    private Ticket createMockTicketWithStatus(Long performanceId, Integer rowNumber, Integer seatNumber, String status) {
        return new Ticket(
            performanceId,
            rowNumber,
            seatNumber,
            PriceCategory.VIP,
            status,
            TicketType.SEATED,
            SectorType.B,
            BigDecimal.valueOf(50.00)
        );
    }

    @Test
    void testGetReservedByIdSuccessful() {
        Long id = 1L;
        LocalDateTime reservedDate = LocalDateTime.now();
        List<Ticket> tickets = Arrays.asList(
            createMockTicket(1L, 1, 1),
            createMockTicket(1L, 1, 2)
        );
        ReservedDetailDto mockReserved = new ReservedDetailDto(123L, reservedDate, tickets, id);

        when(reservedService.getReservedById(id)).thenReturn(mockReserved);

        ResponseEntity<ReservedDetailDto> response = reservedEndpoint.getReservedById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockReserved, response.getBody());
        verify(reservedService, times(1)).getReservedById(id);
    }

    @Test
    void testGetReservedByIdNotFound() {
        Long id = 1L;
        when(reservedService.getReservedById(id)).thenThrow(new RuntimeException("Reservation not found"));

        assertThrows(RuntimeException.class, () -> reservedEndpoint.getReservedById(id));

        verify(reservedService, times(1)).getReservedById(id);
    }

    @Test
    void testCreateReservationSuccessful() throws ValidationException {
        String userId = "user123";
        LocalDateTime reservedDate = LocalDateTime.now();
        List<Long> ticketIds = List.of(1L, 2L, 3L);

        ReservedCreateDto createDto = new ReservedCreateDto(userId, reservedDate, ticketIds);

        List<Ticket> availableTickets = List.of(
            createMockTicketWithStatus(1L, 1, 1, "AVAILABLE"),
            createMockTicketWithStatus(2L, 1, 2, "AVAILABLE"),
            createMockTicketWithStatus(3L, 1, 3, "AVAILABLE")
        );

        List<Ticket> reservedTickets = List.of(
            createMockTicketWithStatus(1L, 1, 1, "RESERVED"),
            createMockTicketWithStatus(2L, 1, 2, "RESERVED"),
            createMockTicketWithStatus(3L, 1, 3, "RESERVED")
        );

        Reservation mockReservation = new Reservation();
        mockReservation.setReservationId(123L);
        mockReservation.setUserId(1L);
        mockReservation.setTicketIds(ticketIds);
        mockReservation.setReservationDate(reservedDate);

        when(ticketRepository.findByIdsWithLock(ticketIds)).thenReturn(availableTickets);
        when(ticketRepository.saveAll(anyList())).thenReturn(reservedTickets);
        when(reservedRepository.save(any(Reservation.class))).thenReturn(mockReservation);

        ResponseEntity<ReservedDetailDto> response = reservedEndpoint.createReservation(createDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    @Test
    void testUpdateReservationSuccessful() throws ValidationException {
        Long reservationId = 1L;
        LocalDateTime reservedDate = LocalDateTime.now();
        List<Ticket> tickets = Arrays.asList(
            createMockTicket(1L, 1, 1),
            createMockTicket(1L, 1, 2)
        );

        ReservedDetailDto updateDto = new ReservedDetailDto(123L, reservedDate, tickets, reservationId);

        ResponseEntity<Void> response = reservedEndpoint.updateReservation(reservationId, updateDto);

        assertEquals(204, response.getStatusCodeValue());
        verify(reservedService, times(1)).updateReservation(updateDto);
    }

    @Test
    void testUpdateReservationIdMismatch() {
        Long reservationId = 1L;
        LocalDateTime reservedDate = LocalDateTime.now();
        List<Ticket> tickets = Arrays.asList(
            createMockTicket(1L, 1, 1),
            createMockTicket(1L, 1, 2)
        );

        ReservedDetailDto updateDto = new ReservedDetailDto(123L, reservedDate, tickets, 2L);

        assertThrows(ValidationException.class, () -> reservedEndpoint.updateReservation(reservationId, updateDto));

        verifyNoInteractions(reservedService);
    }

    @Test
    void testDeleteTicketFromReservationSuccessful() {
        Long reservationId = 1L;
        Long ticketId = 2L;

        ResponseEntity<Void> response = reservedEndpoint.deleteTicketFromReservation(reservationId, ticketId);

        assertEquals(204, response.getStatusCodeValue());
        verify(reservedService, times(1)).deleteTicketFromReservation(reservationId, ticketId);
    }
}
