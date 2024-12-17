package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.PerformanceEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
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
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PerformanceEndpointTest {

    private final Artist artist = new Artist();
    private final Artist artist2 = new Artist();
    private final Location location = new Location();
    private final Location location2 = new Location();
    private final PerformanceDetailDto performance1 = new PerformanceDetailDto(1L, "Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.ONE, 1L, "A", artist, location, 1);
    private final PerformanceDetailDto performance2 = new PerformanceDetailDto(2L, "Performance2", 2L, 2L, LocalDateTime.now(), BigDecimal.TWO, 2L, "B", artist2, location2, 2);
    private final List<PerformanceDetailDto> mockPerformances = List.of(performance1, performance2);
    @Mock
    private PerformanceService performanceService;
    @InjectMocks
    private PerformanceEndpoint performanceEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPerformanceWhenValidInputReturnsSuccess() throws ValidationException, ConflictException {
        PerformanceCreateDto performanceCreateDto = new PerformanceCreateDto("Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.ONE, 1L, "A", new Artist(), new Location(), 1);

        when(performanceService.createPerformance(any(PerformanceCreateDto.class))).thenReturn(performance1);

        ResponseEntity<PerformanceDetailDto> response = performanceEndpoint.createOrUpdatePerformance(performanceCreateDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(performance1, response.getBody());
    }

    @Test
    void createPerformanceWhenInvalidInputThrowsValidationException() throws ValidationException, ConflictException {
        PerformanceCreateDto performanceCreateDto = new PerformanceCreateDto("Performance1", 1L, 1L, LocalDateTime.now(), BigDecimal.ONE, 1L, "A", artist, location, 1);

        List<String> validationErrors = List.of("Name is required", "Name cannot exceed 64 Characters");
        doThrow(new ValidationException("Invalid input", validationErrors)).when(performanceService).createPerformance(any());

        ValidationException exception = assertThrows(ValidationException.class, () -> performanceEndpoint.createOrUpdatePerformance(performanceCreateDto));

        assertTrue(exception.getMessage().contains("Invalid input"));
        assertEquals(validationErrors.toString(), exception.getErrors());
    }

    @Test
    void getAllPerformancesReturnsAllPerformances() {
        when(performanceService.getAllPerformances()).thenReturn(mockPerformances);

        ResponseEntity<List<PerformanceDetailDto>> response = performanceEndpoint.getAllPerformances();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPerformances, response.getBody());
    }

    @Test
    void getPerformanceByIdReturnsPerformanceWithGivenId() {
        Long performanceId = 1L;
        when(performanceService.getPerformanceById(performanceId)).thenReturn(performance1);

        ResponseEntity<PerformanceDetailDto> response = performanceEndpoint.getPerformanceById(performanceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(performance1, response.getBody());
    }

    @Test
    void deletePerformanceDeletesPerformanceWithGivenId() {
        Long performanceId = 1L;

        ResponseEntity<Void> response = performanceEndpoint.deletePerformance(performanceId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(performanceService, times(1)).deletePerformance(performanceId);
    }

    @Test
    void searchPerformanceReturnsMatchingPerformance() {
        PerformanceSearchDto performanceSearchDto = new PerformanceSearchDto(null, null, "A");

        when(performanceService.search(performanceSearchDto)).thenReturn(Stream.of(performance1));

        ResponseEntity<Stream<PerformanceDetailDto>> response = performanceEndpoint.search(performanceSearchDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<PerformanceDetailDto> result = Objects.requireNonNull(response.getBody()).toList();
        assertEquals(1, result.size());
        assertEquals(performance1, result.getFirst());
    }
}
