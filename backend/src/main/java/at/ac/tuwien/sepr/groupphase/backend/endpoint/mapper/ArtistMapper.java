package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.mapstruct.Mapper;

@Mapper
public interface ArtistMapper {
    ArtistDetailDto artistToArtistDetailDto(Artist artist);
}