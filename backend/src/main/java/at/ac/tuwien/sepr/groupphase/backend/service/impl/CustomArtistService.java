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
public class CustomArtistService implements ArtistService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ArtistRepository artistRepository;
    private final ArtistValidator artistValidator;
    private final ArtistMapper artistMapper;

    public CustomArtistService(ArtistRepository artistRepository, ArtistValidator artistValidator, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistValidator = artistValidator;
        this.artistMapper = artistMapper;
    }

    @Override
    public ArtistDetailDto createOrUpdateArtist(ArtistCreateDto artistCreateDto) throws ValidationException, ConflictException {
        LOG.info("Creating/Updating Artist with data: {}", artistCreateDto);

        artistValidator.validateArtist(artistCreateDto);

        Artist artist = new Artist(
            artistCreateDto.getFirstName(),
            artistCreateDto.getSurname(),
            artistCreateDto.getArtistName()
        );

        LOG.debug("Mapped Artist entity: {}", artist);

        artist = artistRepository.save(artist);

        LOG.info("Saved Artist to database: {}", artist);

        ArtistDetailDto artistDetailDto = new ArtistDetailDto(
            artist.getArtistId(),
            artist.getFirstName(),
            artist.getSurname(),
            artist.getArtistName()
        );

        LOG.info("Returning mapped ArtistDetailDto: {}", artistDetailDto);
        return artistDetailDto;
    }

    @Override
    public List<ArtistDetailDto> getAllArtists() {
        LOG.info("Fetching all Artists from database");

        List<ArtistDetailDto> artistList = artistRepository.findAll().stream()
            .map(artist -> new ArtistDetailDto(
                artist.getArtistId(),
                artist.getFirstName(),
                artist.getSurname(),
                artist.getArtistName()
            ))
            .collect(Collectors.toList());

        LOG.info("Fetched {} Artists from database", artistList.size());
        LOG.debug("Artists fetched: {}", artistList);

        return artistList;
    }

    @Override
    public ArtistDetailDto getArtistById(Long artistId) {
        LOG.info("Fetching Artist by ID: {}", artistId);

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> {
                LOG.error("Artist not found with ID: {}", artistId);
                return new IllegalArgumentException("Artist not found with ID: " + artistId);
            });

        LOG.debug("Fetched Artist entity: {}", artist);

        ArtistDetailDto artistDetailDto = new ArtistDetailDto(
            artist.getArtistId(),
            artist.getFirstName(),
            artist.getSurname(),
            artist.getArtistName()
        );

        LOG.info("Returning ArtistDetailDto: {}", artistDetailDto);
        return artistDetailDto;
    }

    @Override
    public void deleteArtist(Long artistId) {
        LOG.info("Deleting Artist by ID: {}", artistId);

        if (!artistRepository.existsById(artistId)) {
            LOG.error("Attempted to delete non-existent Artist with ID: {}", artistId);
            throw new IllegalArgumentException("Artist not found with ID: " + artistId);
        }

        artistRepository.deleteById(artistId);

        LOG.info("Successfully deleted Artist with ID: {}", artistId);
    }

    @Override
    public Stream<ArtistDetailDto> search(ArtistSearchDto dto) {
        LOG.info("Searching artists with data: {}", dto);
        var query = artistRepository.findAll().stream();
        if (dto.getFirstName() != null) {
            query = query.filter(artist -> artist.getFirstName().toLowerCase().contains(dto.getFirstName().toLowerCase()));
        }
        if (dto.getSurname() != null) {
            query = query.filter(artist -> artist.getSurname().toLowerCase().contains(dto.getSurname().toLowerCase()));
        }
        if (dto.getArtistName() != null) {
            query = query.filter(artist -> artist.getArtistName().toLowerCase().contains(dto.getArtistName().toLowerCase()));
        }

        return query.map(this.artistMapper::artistToArtistDetailDto);
    }
}

