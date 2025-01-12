package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.springframework.stereotype.Component;

@Component
public class PerformanceMapper {

    public PerformanceDetailDto toPerformanceDetailDto(Performance performance, Artist artist, Location location) {
        return new PerformanceDetailDto(
            performance.getPerformanceId(),
            performance.getName(),
            performance.getArtistId(),
            performance.getLocationId(),
            performance.getDate(),
            performance.getPrice(),
            performance.getTicketNumber(),
            performance.getHall(),
            artist,
            location,
            performance.getDuration()
        );
    }
}

