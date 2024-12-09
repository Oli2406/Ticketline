package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchPerformanceRepository {

    List<PerformanceDetailDto> findByAdvancedSearch(String query);
}
