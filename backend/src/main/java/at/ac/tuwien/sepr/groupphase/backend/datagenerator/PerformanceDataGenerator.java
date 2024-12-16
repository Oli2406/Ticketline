package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@Profile("generateData")
@DependsOn({"artistDataGenerator", "locationDataGenerator"})
public class PerformanceDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PerformanceRepository performanceRepository;
    private final ArtistRepository artistRepository;
    private final LocationRepository locationRepository;
    private final Random random = new Random();

    public PerformanceDataGenerator(PerformanceRepository performanceRepository, ArtistRepository artistRepository, LocationRepository locationRepository) {
        this.performanceRepository = performanceRepository;
        this.artistRepository = artistRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        if (performanceRepository.count() > 0) {
            LOGGER.info("Performances already exist in the database. Skipping initial data generation.");
            return;
        }

        List<Artist> artists = artistRepository.findAll();
        List<Location> locations = locationRepository.findAll();

        if (artists.isEmpty() || locations.isEmpty()) {
            LOGGER.warn("Artists or Locations not available. Please ensure data generation is complete.");
            return;
        }

        String[] performanceThemes = {
            "Electric Beats Night",
            "Symphony Under the Stars",
            "House Music Explosion",
            "Festival of Lights",
            "Global EDM Bash",
            "Summer Vibes Live",
            "Retro Rewind Party",
            "Dance till Dawn",
            "Bass Drop Fiesta",
            "Chillout Sessions",
            "Neon Dreams",
            "Cosmic Groove",
            "Pulse of the Night",
            "Rhythmic Journey",
            "Echoes of Euphoria",
            "Mystical Melodies",
            "Vortex of Sound",
            "Starlight Serenade",
            "Aurora Beats",
            "Prismatic Party",
            "Indie Rock Riot",
            "Acoustic Afternoon",
            "Jazz Jam Session",
            "Classical Crossover",
            "World Music Fusion",
            "Latin Rhythms",
            "Hip Hop Hype",
            "Funk Fever",
            "Soulful Sounds",
            "Blues Night",
            "Folklore Fiesta",
            "Country Jamboree",
            "Reggae Roots",
            "Ska Party",
            "Punk Rock Rebellion",
            "Metal Mayhem",
            "Hardcore Havoc",
            "Drum & Bass Blitz",
            "Techno Takeover",
            "Trance Transmission",
            "Ambient Atmospheres",
            "Experimental Soundscapes",
            "Avant-Garde Adventures",
            "Orchestral Odyssey",
            "Chamber Music Charms",
            "Opera Extravaganza",
            "Ballet Beauty",
            "Modern Dance Moves",
            "Theatrical Thrills",
            "Comedy Capers",
            "Spoken Word Showcase",
            "Poetry Slam",
            "Film Festival",
            "Art Exhibition",
            "Literary Lounge",
            "Gaming Gathering",
            "Cosplay Carnival",
            "Magic Show Mysteries",
            "Circus Spectacular",
            "Street Performance Showcase",
            "Fire Dancing Flames",
            "Acrobatic Amazement",
            "Burlesque Bonanza",
            "Drag Delight",
            "Karaoke Chaos",
            "Open Mic Night",
            "Silent Disco",
            "Tribute Band Night",
            "Emerging Artists Showcase",
            "Battle of the Bands",
            "DJ Competition",
            "Producer Showdown",
            "Live Looping Extravaganza",
            "Instrumental Innovations",
            "Vocal Virtuosos",
            "Songwriters Circle",
        };
        for (int i = 0; i < performanceThemes.length; i = i + 3) {
            if (i + 2 >= performanceThemes.length) {
                // Handle the case where there aren't enough elements left for a full iteration (i, i+1, i+2)
                break;
            }

            Artist artist1 = artists.get(random.nextInt(artists.size()));
            Artist artist2 = artists.get(random.nextInt(artists.size()));
            Artist artist3 = artists.get(random.nextInt(artists.size()));
            Location location1 = locations.get(random.nextInt(locations.size()));
            Location location2 = locations.get(random.nextInt(locations.size()));
            Location location3 = locations.get(random.nextInt(locations.size()));

            String name1 = performanceThemes[i];
            LocalDateTime date1 = generateRandomFutureDate();
            BigDecimal price1 = generateRandomPrice();
            String hall1 = generateRandomHall();
            Long ticketNumber1 = getTicketNumberByHall(hall1);
            int duration1 = generateRandomDuration();
            createPerformanceIfNotExists(name1, artist1, location1, date1, price1, ticketNumber1, hall1, duration1);

            String name2 = performanceThemes[i + 1];
            LocalDateTime date2 = date1.plusDays(2);
            BigDecimal price2 = generateRandomPrice();
            String hall2 = generateRandomHall();
            Long ticketNumber2 = getTicketNumberByHall(hall2);
            int duration2 = generateRandomDuration();
            createPerformanceIfNotExists(name2, artist2, location2, date2, price2, ticketNumber2, hall2, duration2);

            String name3 = performanceThemes[i + 2];
            LocalDateTime date3 = date2.plusDays(2);
            BigDecimal price3 = generateRandomPrice();
            String hall3 = generateRandomHall();
            Long ticketNumber3 = getTicketNumberByHall(hall3);
            int duration3 = generateRandomDuration();
            createPerformanceIfNotExists(name3, artist3, location3, date3, price3, ticketNumber3, hall3, duration3);
        }

    }

    private void createPerformanceIfNotExists(String name, Artist artist, Location location, LocalDateTime date, BigDecimal price, Long ticketNumber, String hall, int duration) {
        if (!performanceRepository.existsByNameAndDate(name, date)) {
            Performance performance = new Performance(
                name,
                artist.getArtistId(),
                location.getLocationId(),
                date,
                price,
                ticketNumber,
                hall,
                artist,
                location,
                duration
            );
            performanceRepository.save(performance);
            LOGGER.debug("Performance created: {} at location {} by artist {}", name, location.getName(), artist.getArtistName());
        }
    }

    private LocalDateTime generateRandomFutureDate() {
        int year = 2023 + random.nextInt(3);
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        LocalDateTime date = LocalDateTime.of(year, month, day, hour, minute);

        LocalDateTime minDate = LocalDateTime.of(2022, 3, 1, 0, 0);
        return date.isBefore(minDate) ? minDate.plusDays(random.nextInt(365)) : date;
    }

    private BigDecimal generateRandomPrice() {
        double price = 20 + (500 - 20) * random.nextDouble();
        return BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Long getTicketNumberByHall(String hall) {
        if ("A".equals(hall)) {
            return 660L;
        } else if ("B".equals(hall)) {
            return 353L;
        }
        return 0L;
    }

    private String generateRandomHall() {
        String[] halls = {"A", "B"};
        return halls[random.nextInt(halls.length)];
    }

    private int generateRandomDuration() {
        return 60 + random.nextInt(120);
    }
}