package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(ArtistEndpoint.BASE_PATH)
public class ArtistEndpoint {

    public static final String BASE_PATH = "/api/v1/artist";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;

    public ArtistEndpoint(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    public ResponseEntity<ArtistDetailDto> createOrUpdateArtist(@RequestBody ArtistCreateDto artistCreateDto) throws ValidationException, ConflictException {
        LOG.info("Received request to create or update Artist: {}", artistCreateDto);
        ArtistDetailDto createdArtist = artistService.createOrUpdateArtist(artistCreateDto);
        LOG.info("Successfully created/updated Artist: {}", createdArtist);
        return ResponseEntity.ok(createdArtist);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<ArtistDetailDto>> getAllArtists() {
        LOG.info("Fetching all artists");
        List<ArtistDetailDto> artists = artistService.getAllArtists();
        LOG.info("Successfully fetched {} artists: {}", artists.size(), artists);
        return ResponseEntity.ok(artists);
    }


    @PermitAll
    @GetMapping("/search")
    public ResponseEntity<Stream<ArtistDetailDto>> search(ArtistSearchDto dto) {
        LOG.info("GET " + BASE_PATH);
        LOG.debug("request parameters: {}", dto);
        Stream<ArtistDetailDto> result = artistService.search(dto);
        return ResponseEntity.ok(result);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDetailDto> getArtistById(@PathVariable Long id) {
        LOG.info("Fetching Artist by ID: {}", id);
        ArtistDetailDto artist = artistService.getArtistById(id);
        LOG.info("Successfully fetched Artist: {}", artist);
        return ResponseEntity.ok(artist);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        LOG.info("Deleting Artist with ID: {}", id);
        artistService.deleteArtist(id);
        LOG.info("Successfully deleted Artist with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}


