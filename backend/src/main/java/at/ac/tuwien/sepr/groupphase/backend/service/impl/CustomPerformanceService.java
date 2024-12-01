package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PerformanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomPerformanceService implements PerformanceService {

    private final PerformanceRepository performanceRepository;

    public CustomPerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    @Override
    public PerformanceDetailDto createOrUpdatePerformance(PerformanceCreateDto performanceCreateDto) {
        Performance performance = new Performance(
            performanceCreateDto.getName(),
            performanceCreateDto.getArtistId(),
            performanceCreateDto.getLocationId(),
            performanceCreateDto.getDate(),
            performanceCreateDto.getPrice(),
            performanceCreateDto.getHall(),
            performanceCreateDto.getTicketNumber()
        );
        performance = performanceRepository.save(performance);
        return new PerformanceDetailDto(performance.getId(), performance.getName(), performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall());
    }

    @Override
    public List<PerformanceDetailDto> getAllPerformances() {
        return performanceRepository.findAll().stream()
            .map(performance -> new PerformanceDetailDto(performance.getId(), performance.getName(), performance.getArtistId(),
                performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall()))
                    .collect(Collectors.toList());
    }

    @Override
    public PerformanceDetailDto getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Performance not found"));
        return new PerformanceDetailDto(performance.getId(), performance.getName(), performance.getArtistId(),
            performance.getLocationId(), performance.getDate(), performance.getPrice(), performance.getTicketNumber(), performance.getHall());
    }

    @Override
    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }
}
