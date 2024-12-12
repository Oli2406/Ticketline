package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Location;
import at.ac.tuwien.sepr.groupphase.backend.repository.LocationRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
@Profile("generateData")
public class LocationDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LocationRepository locationRepository;

    public LocationDataGenerator(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        createLocationIfNotExists("Madison Square Garden", "4 Pennsylvania Plaza", "New York", "10001", "USA");
        createLocationIfNotExists("The O2 Arena", "Peninsula Square", "London", "SE10 0DX", "Vereinigtes Königreich");
        createLocationIfNotExists("Crypto.com Arena", "1111 S Figueroa St", "Los Angeles", "90015", "USA");
        createLocationIfNotExists("Red Rocks Amphitheatre", "18300 W Alameda Pkwy", "Morrison", "80465", "USA");
        createLocationIfNotExists("Royal Albert Hall", "Kensington Gore", "London", "SW7 2AP", "Vereinigtes Königreich");
        createLocationIfNotExists("Sydney Opera House", "Bennelong Point", "Sydney", "2000", "Australien");
        createLocationIfNotExists("Tomorrowland Mainstage", "De Schorre, Schommelei", "Boom", "2850", "Belgien");
        createLocationIfNotExists("Ushuaïa Ibiza Beach Hotel", "Playa d'en Bossa 10", "Ibiza", "07817", "Spanien");
        createLocationIfNotExists("Ziggo Dome", "De Passage 100", "Amsterdam", "1101 AX", "Niederlande");
        createLocationIfNotExists("Wembley Stadium", "Wembley", "London", "HA9 0WS", "Vereinigtes Königreich");
        createLocationIfNotExists("Hollywood Bowl", "2301 N Highland Ave", "Los Angeles", "90068", "USA");
        createLocationIfNotExists("Mercedes-Benz Arena", "Mercedesstraße 1", "Berlin", "10243", "Deutschland");
        createLocationIfNotExists("Empire Polo Club", "81800 Avenue 51", "Indio", "92201", "USA");
        createLocationIfNotExists("Worthy Farm", "Worthy Lane", "Pilton", "BA4 4BY", "Vereinigtes Königreich");
        createLocationIfNotExists("Barclays Center", "620 Atlantic Ave", "Brooklyn", "11217", "USA");
        createLocationIfNotExists("Accor Arena", "8 Boulevard de Bercy", "Paris", "75012", "Frankreich");
        createLocationIfNotExists("AFAS Live", "ArenA Boulevard 590", "Amsterdam", "1101 DS", "Niederlande");
        createLocationIfNotExists("Palau Sant Jordi", "Passeig Olímpic 5-7", "Barcelona", "08038", "Spanien");
        createLocationIfNotExists("Gorky Park Open-Air Stage", "Krymsky Val 9", "Moskau", "119049", "Russland");
        createLocationIfNotExists("Wiener Musikverein", "Musikvereinsplatz 1", "Wien", "1010", "Österreich");
    }

    private void createLocationIfNotExists(String name, String street, String city, String postalCode, String country) {
        if (!locationRepository.existsByName(name)) {
            Location location = new Location(name, street, city, postalCode, country);
            locationRepository.save(location);
            LOGGER.debug("Location created: {}", name);
        }
    }
}
