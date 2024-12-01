package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDetailDto;

import java.util.List;

/**
 * Service interface for managing artists.
 */
public interface ArtistService {

    /**
     * Creates or updates an artist.
     *
     * @param artistCreateDto the data for creating or updating the artist
     * @return the detailed representation of the created or updated artist
     */
    ArtistDetailDto createOrUpdateArtist(ArtistCreateDto artistCreateDto);

    /**
     * Retrieves all artists.
     *
     * @return a list of detailed representations of all artists
     */
    List<ArtistDetailDto> getAllArtists();

    /**
     * Retrieves a specific artist by its ID.
     *
     * @param id the ID of the artist to retrieve
     * @return the detailed representation of the requested artist
     */
    ArtistDetailDto getArtistById(Long id);

    /**
     * Deletes a specific artist by its ID.
     *
     * @param id the ID of the artist to delete
     */
    void deleteArtist(Long id);
}
