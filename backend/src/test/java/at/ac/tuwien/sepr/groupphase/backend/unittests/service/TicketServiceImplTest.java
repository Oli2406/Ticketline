package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TicketDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TicketServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.TicketValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;

import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;

public class TicketServiceImplTest {

    private TicketServiceImpl ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketValidator ticketValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ticketService = new TicketServiceImpl(ticketRepository, ticketValidator);
    }

    @Test
    void createTicket_ShouldSaveTicket_WhenValidInput() throws ValidationException, ConflictException {
        TicketCreateDto ticketCreateDto = new TicketCreateDto(1L, 1, 1, PriceCategory.PREMIUM, TicketType.SEATED, SectorType.A,
            BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123456L, LocalDateTime.now());

        Ticket savedTicket = new Ticket(1L, 1, 1, PriceCategory.PREMIUM, TicketType.SEATED, SectorType.A,
            BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123456L, LocalDateTime.now());

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketDetailDto result = ticketService.createTicket(ticketCreateDto);

        assertNotNull(result, "Result should not be null");
        assertEquals(savedTicket.getTicketId(), result.getTicketId(), "Ticket ID should match");
        assertEquals(savedTicket.getPrice(), result.getPrice(), "Price should match");

        verify(ticketValidator, times(1)).validateTicket(ticketCreateDto);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void getTicketById_ShouldReturnTicket_WhenTicketExists() {
        Ticket ticket = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.STANDING, SectorType.B,
            BigDecimal.valueOf(100), "AVAILABLE", Hall.B, 123467L, LocalDateTime.now());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        TicketDetailDto result = ticketService.getTicketById(1L);

        assertNotNull(result, "Result should not be null");
        assertEquals(ticket.getTicketId(), result.getTicketId(), "Ticket ID should match");

        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void getTicketById_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ticketService.getTicketById(1L));

        assertEquals("Ticket not found", exception.getMessage(), "Exception message should match");
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void getAllTickets_ShouldReturnAllTickets() {
        Ticket ticket = new Ticket(1L, 1, 1, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B,
            BigDecimal.valueOf(100), "AVAILABLE", Hall.C, 145678L, LocalDateTime.now());

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        List<TicketDetailDto> result = ticketService.getAllTickets();

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one ticket");
        assertEquals(ticket.getTicketId(), result.get(0).getTicketId(), "Ticket ID should match");

        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void updateTicket_ShouldUpdateTicket_WhenValidInput() throws ValidationException, ConflictException {
        TicketCreateDto ticketCreateDto = new TicketCreateDto(1L, 2, 3, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B,
            BigDecimal.valueOf(150), "RESERVED", Hall.A, 124L, LocalDateTime.now());

        Ticket existingTicket = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.STANDING, SectorType.A,
            BigDecimal.valueOf(100), "AVAILABLE", Hall.A, 123L, LocalDateTime.now());

        when(ticketRepository.findByIdWithLock(1L)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketDetailDto result = ticketService.updateTicket(1L, ticketCreateDto);

        assertNotNull(result, "Result should not be null");
        assertEquals(ticketCreateDto.getPrice(), result.getPrice(), "Price should match");
        assertEquals(ticketCreateDto.getStatus(), result.getStatus(), "Status should match");

        verify(ticketValidator, times(1)).validateTicket(ticketCreateDto);
        verify(ticketRepository, times(1)).save(existingTicket);
    }

    @Test
    void updateTicket_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findByIdWithLock(anyLong())).thenReturn(Optional.empty());

        TicketCreateDto ticketCreateDto = new TicketCreateDto(1L, 2, 3, PriceCategory.STANDARD, TicketType.STANDING, SectorType.B,
            BigDecimal.valueOf(150), "RESERVED", Hall.A, 12345L, LocalDateTime.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ticketService.updateTicket(1L, ticketCreateDto));

        assertEquals("Ticket not found with ID: 1", exception.getMessage(), "Exception message should match");
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void deleteTicket_ShouldDeleteTicket() {
        doNothing().when(ticketRepository).deleteById(1L);

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).deleteById(1L);
    }
}
