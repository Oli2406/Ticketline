package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.*;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomPerformanceServiceTest {

    private CustomPerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceValidator performanceValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        performanceService = new CustomPerformanceService(performanceRepository, performanceValidator);
    }

    @Test
    void createOrUpdatePerformance_ShouldSavePerformance_WhenValidInput() throws ValidationException, ConflictException {
        PerformanceCreateDto dto = new PerformanceCreateDto("PerformanceName", 1L, 1L, LocalDate.now(), new BigDecimal("50.00"), 100L, "Main Hall");

        when(performanceRepository.save(any(Performance.class))).thenAnswer(invocation -> {
            Performance p = invocation.getArgument(0);
            p.setPerformanceId(1L);
            return p;
        });

        PerformanceDetailDto created = performanceService.createOrUpdatePerformance(dto);

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

        assertEquals("Performance not found", exception.getMessage(), "Exception message should match");
        verify(performanceRepository, times(1)).findById(1L);
    }
}
