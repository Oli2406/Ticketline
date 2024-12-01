package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/performance")
public class PerformanceEndpoint {

    private final PerformanceService performanceService;

    public PerformanceEndpoint(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PutMapping
    public ResponseEntity<PerformanceDetailDto> createOrUpdatePerformance(@RequestBody PerformanceCreateDto performanceCreateDto) {
        PerformanceDetailDto createdPerformance = performanceService.createOrUpdatePerformance(performanceCreateDto);
        return ResponseEntity.ok(createdPerformance);
    }

    @GetMapping
    public ResponseEntity<List<PerformanceDetailDto>> getAllPerformances() {
        List<PerformanceDetailDto> performances = performanceService.getAllPerformances();
        return ResponseEntity.ok(performances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDetailDto> getPerformanceById(@PathVariable Long id) {
        PerformanceDetailDto performance = performanceService.getPerformanceById(id);
        return ResponseEntity.ok(performance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent().build();
    }
}
