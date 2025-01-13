package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.ReservedEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.ReservedService;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
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
    void testGetReservationsByUserSuccessful() {
        String encryptedUserId = "encrypted123";
        Long userId = 123L;
        LocalDateTime reservedDate = LocalDateTime.now();
        List<Ticket> tickets = Arrays.asList(
            createMockTicket(1L, 1, 1),
            createMockTicket(1L, 1, 2)
        );
        List<ReservedDetailDto> mockReservations = Arrays.asList(
            new ReservedDetailDto(userId, reservedDate, tickets, 1L),
            new ReservedDetailDto(userId, reservedDate, tickets, 2L)
        );

        when(randomStringGenerator.retrieveOriginalId(encryptedUserId)).thenReturn(Optional.of(userId));
        when(reservedService.getReservationsByUserId(userId)).thenReturn(mockReservations);

        ResponseEntity<List<ReservedDetailDto>> response = reservedEndpoint.getReservationsByUser(encryptedUserId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockReservations.size(), response.getBody().size());
        verify(randomStringGenerator, times(1)).retrieveOriginalId(encryptedUserId);
        verify(reservedService, times(1)).getReservationsByUserId(userId);
    }

    @Test
    void testCreateReservationSuccessful() throws ValidationException {
        String userId = "user123";
        LocalDateTime reservedDate = LocalDateTime.now();
        List<Long> ticketIds = List.of(1L, 2L, 3L);

        ReservedCreateDto createDto = new ReservedCreateDto(userId, reservedDate, ticketIds);

        List<Ticket> tickets = Arrays.asList(
            createMockTicket(1L, 1, 1),
            createMockTicket(1L, 1, 2),
            createMockTicket(1L, 1, 3)
        );

        ReservedDetailDto mockCreatedReservation = new ReservedDetailDto(123L, reservedDate, tickets, 1L);

        when(reservedService.createReservation(createDto)).thenReturn(mockCreatedReservation);

        ResponseEntity<ReservedDetailDto> response = reservedEndpoint.createReservation(createDto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockCreatedReservation, response.getBody());
        verify(ticketService, times(1)).updateTicketStatusList(createDto.getTicketIds(), "RESERVED");
        verify(reservedService, times(1)).createReservation(createDto);
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
