package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/artist")
public class ArtistEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ArtistService artistService;

    public ArtistEndpoint(ArtistService artistService) {
        this.artistService = artistService;
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    public ResponseEntity<ArtistDetailDto> createOrUpdateArtist(@RequestBody ArtistCreateDto artistCreateDto) {
        logger.info("Received request to create or update Artist: {}", artistCreateDto);
        ArtistDetailDto createdArtist = artistService.createOrUpdateArtist(artistCreateDto);
        logger.info("Successfully created/updated Artist: {}", createdArtist);
        return ResponseEntity.ok(createdArtist);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<ArtistDetailDto>> getAllArtists() {
        logger.info("Fetching all artists");
        List<ArtistDetailDto> artists = artistService.getAllArtists();
        logger.info("Successfully fetched {} artists: {}", artists.size(), artists);
        return ResponseEntity.ok(artists);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDetailDto> getArtistById(@PathVariable Long id) {
        logger.info("Fetching Artist by ID: {}", id);
        ArtistDetailDto artist = artistService.getArtistById(id);
        logger.info("Successfully fetched Artist: {}", artist);
        return ResponseEntity.ok(artist);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        logger.info("Deleting Artist with ID: {}", id);
        artistService.deleteArtist(id);
        logger.info("Successfully deleted Artist with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}


