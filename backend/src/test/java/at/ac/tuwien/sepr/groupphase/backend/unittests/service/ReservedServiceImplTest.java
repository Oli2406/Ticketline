package at.ac.tuwien.sepr.groupphase.backend.unittests.service;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservedDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationOverviewDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ReservedServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ReservedRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;

import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;

public class ReservedServiceImplTest {

    private ReservedServiceImpl reservedService;

    @Mock
    private ReservedRepository reservedRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private RandomStringGenerator generator;

    @Mock
    private TicketService ticketService;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        reservedService = new ReservedServiceImpl(reservedRepository, ticketRepository, generator, ticketService, performanceRepository, artistRepository, locationRepository);
    }

    @Test
    void getReservedById_ShouldReturnReservation_WhenReservationExists() {
        Long reservationId = 1L;
        Reservation reservation = new Reservation(1L, List.of(1L, 2L), LocalDateTime.now().plusHours(1));
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.STANDARD, TicketType.STANDING, SectorType.A, null, "RESERVED", Hall.A, 123L, LocalDateTime.now());
        Ticket ticket2 = new Ticket(2L, 2, 2, PriceCategory.VIP, TicketType.SEATED, SectorType.B, null, "RESERVED", Hall.A, 124L, LocalDateTime.now());

        when(reservedRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(ticketRepository.findAllById(reservation.getTicketIds())).thenReturn(List.of(ticket1, ticket2));

        ReservedDetailDto result = reservedService.getReservedById(reservationId);

        assertNotNull(result, "Result should not be null");
        assertEquals(reservationId, result.getReservedId(), "Reservation ID should match");
        assertEquals(2, result.getTickets().size(), "Should return two tickets");

        verify(reservedRepository, times(1)).findById(reservationId);
        verify(ticketRepository, times(1)).findAllById(reservation.getTicketIds());
    }

    @Test
    void getReservationsByUserId_ShouldReturnReservations_WhenUserHasReservations() {
        Long userId = 1L;
        Reservation reservation = new Reservation(userId, List.of(1L, 2L), LocalDateTime.now().plusHours(1));
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, null, "RESERVED", Hall.A, 123L, LocalDateTime.now());

        when(reservedRepository.findByUserId(userId)).thenReturn(List.of(reservation));
        when(ticketRepository.findAllById(reservation.getTicketIds())).thenReturn(List.of(ticket1));

        List<ReservedDetailDto> result = reservedService.getReservationsByUserId(userId);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one reservation");
        assertEquals(userId, result.get(0).getUserId(), "User ID should match");

        verify(reservedRepository, times(1)).findByUserId(userId);
        verify(ticketRepository, times(1)).findAllById(reservation.getTicketIds());
    }

    @Test
    void createReservation_ShouldCreateReservation_WhenTicketsAreAvailable() throws ValidationException {
        ReservedCreateDto reservedCreateDto = new ReservedCreateDto("ENC123", LocalDateTime.now(), List.of(1L, 2L));
        Ticket ticket1 = new Ticket(1L, 1, 1, PriceCategory.STANDARD, TicketType.SEATED, SectorType.B, null, "AVAILABLE", Hall.A, 123L, LocalDateTime.now());
        Ticket ticket2 = new Ticket(2L, 2, 2, PriceCategory.VIP, TicketType.STANDING, SectorType.A, null, "AVAILABLE", Hall.A, 124L, LocalDateTime.now());

        when(ticketRepository.findByIdsWithLock(reservedCreateDto.getTicketIds())).thenReturn(List.of(ticket1, ticket2));
        when(generator.retrieveOriginalId(reservedCreateDto.getUserId())).thenReturn(Optional.of(1L));
        when(reservedRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setReservationId(1L);
            return reservation;
        });

        ReservedDetailDto result = reservedService.createReservation(reservedCreateDto);

        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getReservedId(), "Reservation ID should match");
        assertEquals(2, result.getTickets().size(), "Should reserve two tickets");

        verify(ticketRepository, times(1)).findByIdsWithLock(reservedCreateDto.getTicketIds());
        verify(ticketRepository, times(1)).saveAll(anyList());
        verify(reservedRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void deleteTicketFromReservation_ShouldUpdateReservation_WhenTicketRemoved() {
        Long reservationId = 1L;
        Long ticketId = 1L;
        Reservation reservation = new Reservation(1L, List.of(1L, 2L), LocalDateTime.now().plusHours(1));

        when(reservedRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservedService.deleteTicketFromReservation(reservationId, ticketId);

        verify(reservedRepository, times(1)).save(any(Reservation.class));
        verify(reservedRepository, never()).delete(any(Reservation.class));
    }

    @Test
    void getReservationDetailsByUser_ShouldReturnDetails_WhenUserHasReservations() {
        Long userId = 1L;
        Reservation reservation = new Reservation(userId, List.of(1L, 2L), LocalDateTime.now().plusHours(1));
        Ticket ticket = new Ticket(1L, 1, 1, PriceCategory.VIP, TicketType.SEATED, SectorType.B, null, "RESERVED", Hall.A, 123L, LocalDateTime.now());
        Performance performance = new Performance("Concert", 1L, 1L, LocalDateTime.now(), null, 100L, "Main Hall", null, null, 120);
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("Venue", "Street", "City", "12345", "Country");

        when(reservedRepository.findByUserId(userId)).thenReturn(List.of(reservation));
        when(ticketRepository.findAllById(reservation.getTicketIds())).thenReturn(List.of(ticket));
        when(performanceRepository.findById(anyLong())).thenReturn(Optional.of(performance));
        when(artistRepository.findById(anyLong())).thenReturn(Optional.of(artist));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(location));

        List<ReservationOverviewDto> result = reservedService.getReservationDetailsByUser(userId);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one reservation overview");
        assertEquals(userId, result.get(0).getUserId(), "User ID should match");

        verify(reservedRepository, times(1)).findByUserId(userId);
    }
}
