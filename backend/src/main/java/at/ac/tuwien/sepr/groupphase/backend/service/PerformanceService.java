package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;

import java.util.List;

/**
 * Service interface for managing performances.
 */
public interface PerformanceService {

    /**
     * Creates or updates a performance.
     *
     * @param performanceCreateDto the data for creating or updating the performance
     * @return the detailed representation of the created or updated performance
     */
    PerformanceDetailDto createOrUpdatePerformance(PerformanceCreateDto performanceCreateDto);

    /**
     * Retrieves all performances.
     *
     * @return a list of detailed representations of all performances
     */
    List<PerformanceDetailDto> getAllPerformances();

    /**
     * Retrieves a specific performance by its ID.
     *
     * @param id the ID of the performance to retrieve
     * @return the detailed representation of the requested performance
     */
    PerformanceDetailDto getPerformanceById(Long id);

    /**
     * Deletes a specific performance by its ID.
     *
     * @param id the ID of the performance to delete
     */
    void deletePerformance(Long id);
}
