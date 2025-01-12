package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.LocationDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import org.mapstruct.Mapper;

@Mapper
public interface LocationMapper {
    LocationDetailDto locationToLocationDetailDto(Location location);
}
