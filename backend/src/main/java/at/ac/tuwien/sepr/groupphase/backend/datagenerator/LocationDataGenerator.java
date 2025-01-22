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
        createLocationIfNotExists("Madison Square Garden", "4 Pennsylvania Plaza", "New York",
            "10001", "USA");
        createLocationIfNotExists("The O2 Arena", "Peninsula Square", "London", "SE10 0DX",
            "Vereinigtes Königreich");
        createLocationIfNotExists("Crypto.com Arena", "1111 S Figueroa St", "Los Angeles", "90015",
            "USA");
        createLocationIfNotExists("Red Rocks Amphitheatre", "18300 W Alameda Pkwy", "Morrison",
            "80465", "USA");
        createLocationIfNotExists("Royal Albert Hall", "Kensington Gore", "London", "SW7 2AP",
            "Vereinigtes Königreich");
        createLocationIfNotExists("Sydney Opera House", "Bennelong Point", "Sydney", "2000",
            "Australien");
        createLocationIfNotExists("Tomorrowland Mainstage", "De Schorre, Schommelei", "Boom",
            "2850", "Belgien");
        createLocationIfNotExists("Ushuaïa Ibiza Beach Hotel", "Playa d'en Bossa 10", "Ibiza",
            "07817", "Spanien");
        createLocationIfNotExists("Ziggo Dome", "De Passage 100", "Amsterdam", "1101 AX",
            "Niederlande");
        createLocationIfNotExists("Wembley Stadium", "Wembley", "London", "HA9 0WS",
            "Vereinigtes Königreich");
        createLocationIfNotExists("Hollywood Bowl", "2301 N Highland Ave", "Los Angeles", "90068",
            "USA");
        createLocationIfNotExists("Mercedes-Benz Arena", "Mercedesstraße 1", "Berlin", "10243",
            "Deutschland");
        createLocationIfNotExists("Empire Polo Club", "81800 Avenue 51", "Indio", "92201", "USA");
        createLocationIfNotExists("Worthy Farm", "Worthy Lane", "Pilton", "BA4 4BY",
            "Vereinigtes Königreich");
        createLocationIfNotExists("Barclays Center", "620 Atlantic Ave", "Brooklyn", "11217",
            "USA");
        createLocationIfNotExists("Accor Arena", "8 Boulevard de Bercy", "Paris", "75012",
            "Frankreich");
        createLocationIfNotExists("AFAS Live", "ArenA Boulevard 590", "Amsterdam", "1101 DS",
            "Niederlande");
        createLocationIfNotExists("Palau Sant Jordi", "Passeig Olímpic 5-7", "Barcelona", "08038",
            "Spanien");
        createLocationIfNotExists("Gorky Park Open-Air Stage", "Krymsky Val 9", "Moskau", "119049",
            "Russland");
        createLocationIfNotExists("Wiener Musikverein", "Musikvereinsplatz 1", "Wien", "1010",
            "Österreich");
        createLocationIfNotExists("Allianz Arena", "Werner-Heisenberg-Allee 25", "München", "80939",
            "Deutschland");
        createLocationIfNotExists("San Siro", "Piazzale Angelo Moratti", "Mailand", "20151",
            "Italien");
        createLocationIfNotExists("Estádio do Maracanã", "Rua Professor Eurico Rabelo",
            "Rio de Janeiro", "20271-150", "Brasilien");
        createLocationIfNotExists("Aviva Stadium", "Lansdowne Rd", "Dublin", "D04 K5F9", "Irland");
        createLocationIfNotExists("Tokyo Dome", "1 Chome-3-61 Koraku", "Tokyo", "112-0004",
            "Japan");
        createLocationIfNotExists("Camp Nou", "Carrer d'Arístides Maillol", "Barcelona", "08028",
            "Spanien");
        createLocationIfNotExists("Stade de France", "ZAC du Cornillon Nord", "Saint-Denis",
            "93216", "Frankreich");
        createLocationIfNotExists("Olympic Stadium", "Stratford", "London", "E20 2ST",
            "Vereinigtes Königreich");
        createLocationIfNotExists("Eden Park", "Reimers Avenue", "Auckland", "1024", "Neuseeland");
        createLocationIfNotExists("Signal Iduna Park", "Strobelallee 50", "Dortmund", "44139",
            "Deutschland");
        createLocationIfNotExists("Stamford Bridge", "Fulham Rd", "London", "SW6 1HS",
            "Vereinigtes Königreich");
        createLocationIfNotExists("Croke Park", "Jones' Rd", "Dublin", "D03 P6K7", "Irland");
        createLocationIfNotExists("MetLife Stadium", "1 MetLife Stadium Dr", "East Rutherford",
            "07073", "USA");
        createLocationIfNotExists("FNB Stadium", "Soccer City Ave", "Johannesburg", "2147",
            "Südafrika");
        createLocationIfNotExists("Krestovsky Stadium", "Futbolnaya Alleya 1", "Sankt Petersburg",
            "197110", "Russland");
        LOGGER.info("All Locations are created!");
    }

    private void createLocationIfNotExists(String name, String street, String city,
        String postalCode, String country) {
        if (!locationRepository.existsByName(name)) {
            Location location = new Location(name, street, city, postalCode, country);
            locationRepository.save(location);
            LOGGER.debug("Location created: {}", name);
        }
    }
}
