package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());

    private final ArtistRepository artistRepository;
    private final ArtistValidator artistValidator;
    private final ArtistMapper artistMapper;

    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistValidator artistValidator,
        ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistValidator = artistValidator;
        this.artistMapper = artistMapper;
    }

    @Override
    public ArtistDetailDto createArtist(ArtistCreateDto artistCreateDto)
        throws ValidationException, ConflictException {
        LOGGER.info("Creating/Updating Artist with data: {}", artistCreateDto);

        artistValidator.validateArtist(artistCreateDto);

        Artist artist = new Artist(
            artistCreateDto.getFirstName(),
            artistCreateDto.getLastName(),
            artistCreateDto.getArtistName()
        );

        LOGGER.debug("Mapped Artist entity: {}", artist);

        artist = artistRepository.save(artist);

        LOGGER.info("Saved Artist to database: {}", artist);

        ArtistDetailDto artistDetailDto = new ArtistDetailDto(
            artist.getArtistId(),
            artist.getFirstName(),
            artist.getLastName(),
            artist.getArtistName()
        );

        LOGGER.info("Returning mapped ArtistDetailDto: {}", artistDetailDto);
        return artistDetailDto;
    }

    @Override
    public List<ArtistDetailDto> getAllArtists() {
        LOGGER.info("Fetching all Artists from database");

        List<ArtistDetailDto> artistList = artistRepository.findAll().stream()
            .map(artist -> new ArtistDetailDto(
                artist.getArtistId(),
                artist.getFirstName(),
                artist.getLastName(),
                artist.getArtistName()
            ))
            .collect(Collectors.toList());

        LOGGER.info("Fetched {} Artists from database", artistList.size());
        LOGGER.debug("Artists fetched: {}", artistList);

        return artistList;
    }

    @Override
    public ArtistDetailDto getArtistById(Long artistId) {
        LOGGER.info("Fetching Artist by ID: {}", artistId);

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> {
                LOGGER.error("Artist not found with ID: {}", artistId);
                return new IllegalArgumentException("Artist not found with ID: " + artistId);
            });

        LOGGER.debug("Fetched Artist entity: {}", artist);

        ArtistDetailDto artistDetailDto = new ArtistDetailDto(
            artist.getArtistId(),
            artist.getFirstName(),
            artist.getLastName(),
            artist.getArtistName()
        );

        LOGGER.info("Returning ArtistDetailDto: {}", artistDetailDto);
        return artistDetailDto;
    }

    @Override
    public void deleteArtist(Long artistId) {
        LOGGER.info("Deleting Artist by ID: {}", artistId);

        if (!artistRepository.existsById(artistId)) {
            LOGGER.error("Attempted to delete non-existent Artist with ID: {}", artistId);
            throw new IllegalArgumentException("Artist not found with ID: " + artistId);
        }

        artistRepository.deleteById(artistId);

        LOGGER.info("Successfully deleted Artist with ID: {}", artistId);
    }

    @Override
    public Stream<ArtistDetailDto> search(ArtistSearchDto dto) {
        LOGGER.info("Searching artists with data: {}", dto);
        var query = artistRepository.findAll().stream();
        if (dto.getFirstName() != null) {
            query = query.filter(artist -> artist.getFirstName().toLowerCase()
                .contains(dto.getFirstName().toLowerCase()));
        }
        if (dto.getLastName() != null) {
            query = query.filter(artist -> artist.getLastName().toLowerCase()
                .contains(dto.getLastName().toLowerCase()));
        }
        if (dto.getArtistName() != null) {
            query = query.filter(artist -> artist.getArtistName().toLowerCase()
                .contains(dto.getArtistName().toLowerCase()));
        }

        return query.map(this.artistMapper::artistToArtistDetailDto);
    }
}

