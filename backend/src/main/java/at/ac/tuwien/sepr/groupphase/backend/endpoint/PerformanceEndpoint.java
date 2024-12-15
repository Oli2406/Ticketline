package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping(PerformanceEndpoint.BASE_PATH)
public class PerformanceEndpoint {

    public static final String BASE_PATH = "/api/v1/performance";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceService performanceService;

    public PerformanceEndpoint(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    public ResponseEntity<PerformanceDetailDto> createOrUpdatePerformance(@RequestBody PerformanceCreateDto performanceCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("Received request to create or update performance: {}", performanceCreateDto);
        PerformanceDetailDto createdPerformance = performanceService.createPerformance(performanceCreateDto);
        LOGGER.debug("Performance created/updated successfully: {}", createdPerformance);
        return ResponseEntity.ok(createdPerformance);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<PerformanceDetailDto>> getAllPerformances() {
        LOGGER.info("Fetching all performances");
        List<PerformanceDetailDto> performances = performanceService.getAllPerformances();
        LOGGER.debug("Fetched {} performances: {}", performances.size(), performances);
        return ResponseEntity.ok(performances);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDetailDto> getPerformanceById(@PathVariable Long id) {
        LOGGER.info("Fetching performance with ID: {}", id);
        PerformanceDetailDto performance = performanceService.getPerformanceById(id);
        LOGGER.debug("Fetched performance: {}", performance);
        return ResponseEntity.ok(performance);
    }

    @PermitAll
    @GetMapping("/event/{id}")
    public ResponseEntity<List<PerformanceDetailDto>> getByEventId(@PathVariable Long id) {
        LOGGER.info("Fetching performance by event id: {}", id);
        List<PerformanceDetailDto> result = performanceService.getByEventId(id);
        return ResponseEntity.ok(result);
    }

    @PermitAll
    @GetMapping("/location/{id}")
    public ResponseEntity<List<PerformanceDetailDto>> getByLocationId(@PathVariable Long id) {
        LOGGER.info("Fetching performance by location id: {}", id);
        List<PerformanceDetailDto> result = performanceService.getByLocationId(id);
        return ResponseEntity.ok(result);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        LOGGER.info("Deleting performance with ID: {}", id);
        performanceService.deletePerformance(id);
        LOGGER.debug("Performance with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PermitAll
    @GetMapping("/search")
    public ResponseEntity<Stream<PerformanceDetailDto>> search(PerformanceSearchDto dto) {
        LOGGER.info("GET " + BASE_PATH);
        LOGGER.debug("request parameters: {}", dto.toString());
        Stream<PerformanceDetailDto> result = performanceService.search(dto);
        return ResponseEntity.ok(result);
    }

    @PermitAll
    @GetMapping("/advanced-search")
    public ResponseEntity<?> advancedSearch(@RequestParam String query) {
        List<PerformanceDetailDto> events = performanceService.performAdvancedSearch(query);
        return new ResponseEntity<>(events, HttpStatus.CREATED);
    }
}
