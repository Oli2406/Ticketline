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

        for (int i = 1; i <= 30; i++) {
            Artist artist = artists.get(random.nextInt(artists.size()));
            Location location = locations.get(random.nextInt(locations.size()));

            String name = "Performance " + i;
            LocalDateTime date = generateRandomFutureDate();
            BigDecimal price = generateRandomPrice();
            String hall = generateRandomHall();
            Long ticketNumber = getTicketNumberByHall(hall);
            int duration = generateRandomDuration();

            createPerformanceIfNotExists(name, artist, location, date, price, ticketNumber, hall, duration);
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
