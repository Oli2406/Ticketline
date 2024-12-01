package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping("/api/v1/performance")
public class PerformanceEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceService performanceService;

    public PerformanceEndpoint(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PutMapping
    public ResponseEntity<PerformanceDetailDto> createOrUpdatePerformance(@RequestBody PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException {
        logger.info("Received request to create or update performance: {}", performanceCreateDto);
        PerformanceDetailDto createdPerformance = performanceService.createOrUpdatePerformance(performanceCreateDto);
        logger.debug("Performance created/updated successfully: {}", createdPerformance);
        return ResponseEntity.ok(createdPerformance);
    }

    @GetMapping
    public ResponseEntity<List<PerformanceDetailDto>> getAllPerformances() {
        logger.info("Fetching all performances");
        List<PerformanceDetailDto> performances = performanceService.getAllPerformances();
        logger.debug("Fetched {} performances: {}", performances.size(), performances);
        return ResponseEntity.ok(performances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDetailDto> getPerformanceById(@PathVariable Long id) {
        logger.info("Fetching performance with ID: {}", id);
        PerformanceDetailDto performance = performanceService.getPerformanceById(id);
        logger.debug("Fetched performance: {}", performance);
        return ResponseEntity.ok(performance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        logger.info("Deleting performance with ID: {}", id);
        performanceService.deletePerformance(id);
        logger.debug("Performance with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
