package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        performanceService = new PerformanceServiceImpl(performanceRepository, performanceValidator,
            searchPerformanceRepository, artistRepository, locationRepository, performanceMapper);
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
}