package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@Profile("generateData")
public class ArtistDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ArtistRepository artistRepository;

    public ArtistDataGenerator(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        createArtistIfNotExists("David", "Guetta", "David Guetta");
        createArtistIfNotExists("Calvin", "Harris", "Calvin Harris");
        createArtistIfNotExists("Martijn", "Garritsen", "Martin Garrix");
        createArtistIfNotExists("Tijs", "Verwest", "Tiësto");
        createArtistIfNotExists("Armin", "van Buuren", "Armin van Buuren");
        createArtistIfNotExists("Tim", "Bergling", "Avicii");
        createArtistIfNotExists("Anton", "Zaslavski", "Zedd");
        createArtistIfNotExists("Kyrre", "Gørvell-Dahll", "Kygo");
        createArtistIfNotExists("Joel", "Zimmerman", "Deadmau5");
        createArtistIfNotExists("Sonny", "Moore", "Skrillex");
        createArtistIfNotExists("Christopher", "Comstock", "Marshmello");
        createArtistIfNotExists("Dillon", "Francis", "Dillon Francis");
        createArtistIfNotExists("Steven", "Hiroyuki Aoki", "Steve Aoki");
        createArtistIfNotExists("Alessandro", "Lindblad", "Alesso");
        createArtistIfNotExists("Thomas", "Pentz", "Diplo");
        createArtistIfNotExists("Andrew", "Taggart", "The Chainsmokers");
        createArtistIfNotExists("William", "Grigahcine", "DJ Snake");
        createArtistIfNotExists("Don", "Pepijn Schipper", "Don Diablo");
        createArtistIfNotExists("Robbert", "van de Corput", "Hardwell");
        createArtistIfNotExists("Nick", "Rotteveel", "Nicky Romero");
    }

    private void createArtistIfNotExists(String firstName, String lastName, String artistName) {
        if (!artistRepository.existsByArtistName(artistName)) {
            Artist artist = new Artist(firstName, lastName, artistName);
            artistRepository.save(artist);
            LOGGER.debug("Artist created: {}", artistName);
        }
    }
}
