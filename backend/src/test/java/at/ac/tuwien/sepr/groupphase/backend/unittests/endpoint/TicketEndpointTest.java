package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.TicketEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;


import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;

class TicketEndpointTest {

    private final TicketDetailDto ticket1 = new TicketDetailDto(1L, 123L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123L, LocalDateTime.now());
    private final TicketDetailDto ticket2 = new TicketDetailDto(2L, 123L, 1, 2, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(50), "SOLD", Hall.A, 124L, LocalDateTime.now());
    private final List<TicketDetailDto> mockTickets = List.of(ticket1, ticket2);

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketEndpoint ticketEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTicketWhenValidInputReturnsSuccess() throws ValidationException, ConflictException {
        TicketCreateDto ticketCreateDto = new TicketCreateDto(123L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123L, LocalDateTime.now());

        when(ticketService.createTicket(any(TicketCreateDto.class))).thenReturn(ticket1);

        ResponseEntity<TicketDetailDto> response = ticketEndpoint.createTicket(ticketCreateDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ticket1, response.getBody());
    }

    @Test
    void createTicketWhenInvalidInputThrowsValidationException() throws ValidationException, ConflictException {
        TicketCreateDto ticketCreateDto = new TicketCreateDto(123L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123L, LocalDateTime.now());

        doThrow(new ValidationException("Invalid input", List.of("Row number is required"))).when(ticketService).createTicket(any());

        ValidationException exception = assertThrows(ValidationException.class, () -> ticketEndpoint.createTicket(ticketCreateDto));

        assertTrue(exception.getMessage().contains("Invalid input"));
    }

    @Test
    void getAllTicketsReturnsAllTickets() {
        when(ticketService.getAllTickets()).thenReturn(mockTickets);

        ResponseEntity<List<TicketDetailDto>> response = ticketEndpoint.getAllTickets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTickets, response.getBody());
    }

    @Test
    void getTicketByIdReturnsTicketWithGivenId() {
        Long ticketId = 1L;
        when(ticketService.getTicketById(ticketId)).thenReturn(ticket1);

        ResponseEntity<TicketDetailDto> response = ticketEndpoint.getTicketById(ticketId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ticket1, response.getBody());
    }

    @Test
    void getTicketsByPerformanceIdReturnsTicketsForPerformance() {
        Long performanceId = 123L;
        when(ticketService.getTicketsByPerformanceId(performanceId)).thenReturn(mockTickets);

        ResponseEntity<List<TicketDetailDto>> response = ticketEndpoint.getTicketsByPerformanceId(performanceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTickets, response.getBody());
    }

    @Test
    void deleteTicketDeletesTicketWithGivenId() {
        Long ticketId = 1L;

        ResponseEntity<Void> response = ticketEndpoint.deleteTicket(ticketId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ticketService, times(1)).deleteTicket(ticketId);
    }

    @Test
    void updateTicketWhenValidInputReturnsUpdatedTicket() throws ValidationException, ConflictException {
        Long ticketId = 1L;
        TicketCreateDto ticketCreateDto = new TicketCreateDto(123L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123L, LocalDateTime.now());;
        TicketDetailDto updatedTicket = new TicketDetailDto(1L, 123L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, BigDecimal.valueOf(150), "SOLD", Hall.A, 123L, LocalDateTime.now());

        when(ticketService.updateTicket(ticketId, ticketCreateDto)).thenReturn(updatedTicket);

        ResponseEntity<TicketDetailDto> response = ticketEndpoint.updateTicket(ticketId, ticketCreateDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTicket, response.getBody());
    }
}
