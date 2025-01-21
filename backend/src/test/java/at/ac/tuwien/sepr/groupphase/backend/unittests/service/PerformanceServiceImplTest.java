package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.*;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.PerformanceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PerformanceServiceImplTest {

    private PerformanceServiceImpl performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceValidator performanceValidator;

    @Mock
    private SearchPerformanceRepository searchPerformanceRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PerformanceMapper performanceMapper;

    @Mock
    private TicketRepository ticketRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        performanceService = new PerformanceServiceImpl(performanceRepository, performanceValidator,
            searchPerformanceRepository, artistRepository, locationRepository, performanceMapper, ticketRepository);
    }

    @Test
    void createOrUpdatePerformance_ShouldSavePerformance_WhenValidInput()
        throws ValidationException, ConflictException {

        Artist artist = new Artist("John", "Doe", "ArtistName");
        artist.setArtistId(1L);
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345",
            "Countryland");
        location.setLocationId(1L);

        when(artistRepository.findArtistByArtistId(1L)).thenReturn(artist);
        when(locationRepository.findByLocationId(1L)).thenReturn(location);

        PerformanceCreateDto dto = new PerformanceCreateDto("PerformanceName", 1L, 1L,
            LocalDateTime.now(), new BigDecimal("50.00"), 100L, "Main Hall", artist, location, 300);

        when(performanceRepository.save(any(Performance.class))).thenAnswer(invocation -> {
            Performance p = invocation.getArgument(0);
            p.setPerformanceId(1L);
            return p;
        });

        PerformanceDetailDto created = performanceService.createPerformance(dto);

        assertNotNull(created, "Created performance DTO should not be null");
        assertAll(
            () -> assertNotNull(created.getPerformanceId(), "Performance ID should not be null"),
            () -> assertEquals(dto.getName(), created.getName(), "Performance name should match"),
            () -> assertEquals(dto.getPrice(), created.getPrice(), "Price should match")
        );

        verify(performanceValidator, times(1)).validatePerformance(dto);
        verify(performanceRepository, times(1)).save(any(Performance.class));
    }

    @Test
    void getPerformanceById_ShouldThrowException_WhenPerformanceNotFound() {
        when(performanceRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> performanceService.getPerformanceById(1L),
            "Should throw exception for non-existent ID");

        assertEquals("Performance not found", exception.getMessage(),
            "Exception message should match");
        verify(performanceRepository, times(1)).findById(1L);
    }

    @Test
    void searchPerformanceByNameReturnsMatchingEvent() {
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("Location Name", "Street1", "City1", "12345", "Country1");
        PerformanceDetailDto performance1DetailDto = new PerformanceDetailDto(1L, "Matching Name",
            1L, 1L, LocalDateTime.now(), BigDecimal.ONE, 300L, "A", artist, location, 300);
        Performance performance1 = new Performance("Matching Name", 1L, 1L, LocalDateTime.now(),
            BigDecimal.ONE, 300L, "A", artist, location, 300);
        Performance performance2 = new Performance("Other Name", 1L, 1L, LocalDateTime.now(),
            BigDecimal.valueOf(-100), 300L, "B", artist, location, 300);

        when(performanceRepository.findAll()).thenReturn(List.of(performance1, performance2));
        when(performanceMapper.toPerformanceDetailDto(performance1, artist, location))
            .thenReturn(performance1DetailDto);
        when(artistRepository.findArtistByArtistId(performance1.getArtistId())).thenReturn(artist);
        when(locationRepository.findByLocationId(performance1.getLocationId())).thenReturn(
            location);

        PerformanceSearchDto searchDto = new PerformanceSearchDto(null, BigDecimal.ONE,
            null); // Search by price

        List<PerformanceDetailDto> result = performanceService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return only one event");
        verify(performanceRepository, times(1)).findAll();
        verify(performanceMapper, times(1)).toPerformanceDetailDto(performance1, artist, location);
        verify(performanceMapper, never()).toPerformanceDetailDto(performance2, artist, location);
    }

    @Test
    void searchPerformanceByNameReturnsNoEventsWhenNoMatch() {
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("Location Name", "Street1", "City1", "12345", "Country1");
        Performance performance1 = new Performance("Some Name", 1L, 1L, LocalDateTime.now(),
            BigDecimal.ONE, 300L, "A", artist, location, 300);
        Performance performance2 = new Performance("Other Name", 1L, 1L, LocalDateTime.now(),
            BigDecimal.ONE, 300L, "B", artist, location, 300);
        when(performanceRepository.findAll()).thenReturn(List.of(performance1, performance2));

        PerformanceSearchDto searchDto = new PerformanceSearchDto(null, null,
            "X"); // Search by hall

        List<PerformanceDetailDto> result = performanceService.search(searchDto).toList();

        assertEquals(0, result.size(), "Should return no events");
        verify(performanceRepository, times(1)).findAll();
        verify(performanceMapper, never()).toPerformanceDetailDto(any(Performance.class),
            any(Artist.class), any(Location.class));
    }

    @Test
    void getByEventIdReturnsPerformancesForGivenEventId() {
        Long eventId = 1L;
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345", "Countryland");
        Performance performance = new Performance("Performance1", eventId, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);
        PerformanceDetailDto performanceDetailDto = new PerformanceDetailDto(1L, "Performance1", eventId, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);

        when(performanceRepository.findByEventId(eventId)).thenReturn(List.of(performance));
        when(artistRepository.findArtistByArtistId(performance.getArtistId())).thenReturn(artist);
        when(locationRepository.findByLocationId(performance.getLocationId())).thenReturn(location);
        when(performanceMapper.toPerformanceDetailDto(performance, artist, location)).thenReturn(performanceDetailDto);

        List<PerformanceDetailDto> result = performanceService.getByEventId(eventId);

        assertEquals(1, result.size(), "Should return one performance");
        assertEquals(performanceDetailDto, result.getFirst(), "Returned performance should match expected");
        verify(performanceRepository, times(1)).findByEventId(eventId);
    }

    @Test
    void getByLocationIdReturnsPerformancesForGivenLocationId() {
        Long locationId = 1L;

        Artist artist = new Artist();
        artist.setArtistId(1L);
        artist.setFirstName("John");
        artist.setLastName("Doe");
        artist.setArtistName("ArtistName");

        Location location = new Location();
        location.setLocationId(locationId);
        location.setName("VenueName");
        location.setStreet("Main Street 123");
        location.setCity("Cityville");
        location.setPostalCode("12345");
        location.setCountry("Countryland");

        Performance performance = new Performance();
        performance.setPerformanceId(1L);
        performance.setName("Performance1");
        performance.setArtistId(1L);
        performance.setLocationId(locationId);
        performance.setDate(LocalDateTime.now());
        performance.setPrice(BigDecimal.TEN);
        performance.setTicketNumber(100L);
        performance.setHall("Main Hall");
        performance.setDuration(100);

        PerformanceDetailDto performanceDetailDto = new PerformanceDetailDto(
            1L, "Performance1", 1L, locationId, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100
        );

        when(performanceRepository.findByLocationId(locationId)).thenReturn(List.of(performance));
        when(artistRepository.findArtistByArtistId(performance.getArtistId())).thenReturn(artist);
        when(locationRepository.findByLocationId(locationId)).thenReturn(location);
        when(performanceMapper.toPerformanceDetailDto(performance, artist, location)).thenReturn(performanceDetailDto);

        List<PerformanceDetailDto> result = performanceService.getByLocationId(locationId);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one performance");
        assertEquals(performanceDetailDto, result.getFirst(), "Returned performance should match expected");
    }

    @Test
    void updateTicketNumberByIdUpdatesSuccessfully() throws NotFoundException {
        Long performanceId = 1L;
        Long ticketNumber = 150L;
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345", "Countryland");
        Performance performance = new Performance("Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, ticketNumber, "Main Hall", artist, location, 100);
        PerformanceDetailDto performanceDetailDto = new PerformanceDetailDto(performanceId, "Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, ticketNumber, "Main Hall", artist, location, 100);

        when(performanceRepository.findById(performanceId)).thenReturn(Optional.of(performance));
        when(performanceRepository.save(any(Performance.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(artistRepository.findArtistByArtistId(performance.getArtistId())).thenReturn(artist);
        when(locationRepository.findByLocationId(performance.getLocationId())).thenReturn(location);
        when(performanceMapper.toPerformanceDetailDto(performance, artist, location)).thenReturn(performanceDetailDto);

        PerformanceDetailDto updatedPerformance = performanceService.updateTicketNumberById(performanceId, ticketNumber);

        assertNotNull(updatedPerformance, "Updated performance should not be null");
        assertEquals(ticketNumber, updatedPerformance.getTicketNumber(), "Updated ticket number should match");
        verify(performanceRepository, times(1)).save(performance);
    }

    @Test
    void updateTicketNumberByIdWithNegativeTicketNumberThrowsException() {
        Long performanceId = 1L;
        Long ticketNumber = -150L;
        Artist artist = new Artist("John", "Doe", "ArtistName");
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345", "Countryland");
        Performance performance = new Performance("Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);

        when(performanceRepository.findById(performanceId)).thenReturn(Optional.of(performance));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            performanceService.updateTicketNumberById(performanceId, ticketNumber);
        });

        assertEquals("Ticket number cannot be negative.", exception.getMessage());

        verify(performanceRepository, never()).save(any(Performance.class));
    }

    @Test
    void updateTicketNumberByIdThrowsNotFoundExceptionWhenIdNotFound() {
        Long performanceId = 1L;
        when(performanceRepository.findById(performanceId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            performanceService.updateTicketNumberById(performanceId, 100L);
        });

        assertEquals("Performance with ID 1 not found.", exception.getMessage());
        verify(performanceRepository, never()).save(any(Performance.class));
    }

    @Test
    void getAllPerformancesReturnsAllPerformances() {
        Artist artist = new Artist("John", "Doe", "ArtistName");
        artist.setArtistId(1L);
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345", "Countryland");
        location.setLocationId(1L);
        Performance performance = new Performance("Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);
        PerformanceDetailDto performanceDetailDto = new PerformanceDetailDto(1L, "Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);

        when(performanceRepository.findAll()).thenReturn(List.of(performance));
        when(artistRepository.findArtistByArtistId(performance.getArtistId())).thenReturn(artist);
        when(locationRepository.findByLocationId(performance.getLocationId())).thenReturn(location);

        List<PerformanceDetailDto> result = performanceService.getAllPerformances();

        assertEquals(1, result.size(), "Should return one performance");
        assertEquals(performanceDetailDto.getName(), result.getFirst().getName(), "Performance name should match");
        verify(performanceRepository, times(1)).findAll();
        verify(artistRepository, times(1)).findArtistByArtistId(performance.getArtistId());
        verify(locationRepository, times(1)).findByLocationId(performance.getLocationId());
    }

    @Test
    void getPerformanceByIdReturnsPerformanceWithGivenId() {
        Long performanceId = 1L;
        Artist artist = new Artist("John", "Doe", "ArtistName");
        artist.setArtistId(1L);
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345", "Countryland");
        location.setLocationId(1L);
        Performance performance = new Performance("Performance1", performanceId, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);
        PerformanceDetailDto performanceDetailDto = new PerformanceDetailDto(performanceId, "Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", artist, location, 100);

        when(performanceRepository.findById(performanceId)).thenReturn(Optional.of(performance));
        when(artistRepository.findArtistByArtistId(performance.getArtistId())).thenReturn(artist);
        when(locationRepository.findByLocationId(performance.getLocationId())).thenReturn(location);

        PerformanceDetailDto result = performanceService.getPerformanceById(performanceId);

        assertNotNull(result, "Result should not be null");
        assertEquals(performanceDetailDto.getName(), result.getName(), "Performance name should match");
        verify(performanceRepository, times(1)).findById(performanceId);
        verify(artistRepository, times(1)).findArtistByArtistId(performance.getArtistId());
        verify(locationRepository, times(1)).findByLocationId(performance.getLocationId());
    }

    @Test
    void deletePerformanceDeletesPerformanceAndTickets() {
        Long performanceId = 1L;
        when(ticketRepository.deleteByPerformanceId(performanceId)).thenReturn(10);

        performanceService.deletePerformance(performanceId);

        verify(ticketRepository, times(1)).deleteByPerformanceId(performanceId);
        verify(performanceRepository, times(1)).deleteById(performanceId);
    }

    @Test
    void deletePerformanceWithoutTicketsLogsZeroDeleted() {
        Long performanceId = 1L;
        when(ticketRepository.deleteByPerformanceId(performanceId)).thenReturn(0);

        performanceService.deletePerformance(performanceId);

        verify(ticketRepository, times(1)).deleteByPerformanceId(performanceId);
        verify(performanceRepository, times(1)).deleteById(performanceId);
    }

    @Test
    void performAdvancedSearchReturnsSearchResults() {
        String term = "concert";
        PerformanceDetailDto performanceDetailDto = new PerformanceDetailDto(1L, "Concert Performance", 1L, 1L, LocalDateTime.now(), BigDecimal.TEN, 100L, "Main Hall", null, null, 100);
        when(searchPerformanceRepository.findByAdvancedSearch(term)).thenReturn(List.of(performanceDetailDto));

        List<PerformanceDetailDto> result = performanceService.performAdvancedSearch(term);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one matching performance");
        assertEquals(performanceDetailDto.getName(), result.getFirst().getName(), "Performance name should match");
        verify(searchPerformanceRepository, times(1)).findByAdvancedSearch(term);
    }

    @Test
    void deletePerformanceWithoutDeletedTickets() {
        Long performanceId = 1L;

        when(ticketRepository.deleteByPerformanceId(performanceId)).thenReturn(0);

        performanceService.deletePerformance(performanceId);

        verify(ticketRepository, times(1)).deleteByPerformanceId(performanceId);
        verify(performanceRepository, times(1)).deleteById(performanceId);
    }

    @Test
    void searchPerformanceFiltersByDate() {
        PerformanceSearchDto searchDto = new PerformanceSearchDto();
        searchDto.setDate(LocalDateTime.of(2023, 1, 15, 12, 0));

        Performance matchingPerformance = new Performance();
        matchingPerformance.setPerformanceId(1L);
        matchingPerformance.setName("Performance1");
        matchingPerformance.setDate(LocalDateTime.of(2023, 1, 15, 10, 0));
        matchingPerformance.setDuration(120);

        Performance nonMatchingPerformance = new Performance();
        nonMatchingPerformance.setPerformanceId(2L);
        nonMatchingPerformance.setName("Performance2");
        nonMatchingPerformance.setDate(LocalDateTime.of(2023, 1, 15, 15, 0));
        nonMatchingPerformance.setDuration(60);

        Artist artist = new Artist("John", "Doe", "ArtistName");
        artist.setArtistId(1L);
        Location location = new Location("VenueName", "Main Street 123", "Cityville", "12345", "Countryland");
        location.setLocationId(1L);

        when(performanceRepository.findAll()).thenReturn(List.of(matchingPerformance, nonMatchingPerformance));
        when(artistRepository.findArtistByArtistId(anyLong())).thenReturn(artist);
        when(locationRepository.findByLocationId(anyLong())).thenReturn(location);
        when(performanceMapper.toPerformanceDetailDto(any(), any(), any()))
            .thenAnswer(invocation -> {
                Performance p = invocation.getArgument(0);
                return new PerformanceDetailDto(p.getPerformanceId(), p.getName(), p.getArtistId(),
                    p.getLocationId(), p.getDate(), p.getPrice(), p.getTicketNumber(),
                    p.getHall(), artist, location, p.getDuration());
            });

        List<PerformanceDetailDto> result = performanceService.search(searchDto).toList();

        assertEquals(1, result.size(), "Should return one matching performance");
        assertEquals("Performance1", result.getFirst().getName(), "The performance name should match");
        verify(performanceRepository, times(1)).findAll();
    }
}