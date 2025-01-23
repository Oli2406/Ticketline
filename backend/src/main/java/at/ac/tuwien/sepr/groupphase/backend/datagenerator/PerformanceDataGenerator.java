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
            "Galactic Groove Gala",
            "Sunset Serenades",
            "Urban Beats Block Party",
            "Melodic Midnight",
            "Soundwave Spectacle",
            "Desert Dunes Dance",
            "Fusion Fireworks",
            "Carnival of Rhythms",
            "Vintage Vinyl Vibes",
            "Ethereal Echoes",
            "Cosmic Harmony",
            "Seaside Symphonies",
            "Mountain Melodies",
            "Oceanic Overtures",
            "Cultural Soundscapes",
            "Electro Groove Gala",
            "Sonic Sanctuary",
            "Skyline Sessions",
            "Underwater Adventures",
            "Lunar Beats Bash",
            "Twisted Treble Night",
            "Infinite Frequencies",
            "Spirit of Sound",
            "Jungle Jams",
            "Safari Sounds",
            "Lush Beats Paradise",
            "Electro Swing Party",
            "Lo-Fi Lounge",
            "Midnight Mystique",
            "Grit and Groove",
            "Rhythm of the Rails",
            "Waterfront Waltz",
            "Dreamwave Festival",
            "High Altitude Harmonies",
            "Velvet Vibes",
            "Silver Strings Soiree",
            "Underground Unplugged",
            "Techno Odyssey",
            "Flamenco Fever",
            "Carnival of Sound",
            "Galactic Bounce",
            "Aurora Acoustics",
            "Rhythm of the Forest",
            "Desert Oasis Tunes",
            "Golden Hour Grooves",
            "Northern Lights Symphony",
            "Sonic Boom Bash",
            "Echo Chamber Experience",
            "Stardust Showcase",
            "Midnight Mirage",
            "Beachside Beats",
            "Acoustic Echo Festival",
            "Dusk Dance Party",
            "Thunder Groove Gala",
            "Sonic Rainbow",
            "Enchanted Evening Serenades",
            "Beats Beyond Borders",
            "Cloud Nine Concert",
            "Sound and Fury Fest",
            "Lunar Lounge Live",
            "Retro Rhythm Revival",
            "Cityscape Soundtrack",
            "Sonic Tranquility",
            "Echo of the Ages",
            "Magnetic Melodies",
            "Planet Party Pulse",
            "Underground Rhythms",
            "Hidden Harmonies",
            "Ethno-Electro Escape",
            "Cascade of Beats",
            "Twilight Tales of Tunes",
            "Global Jam Vibes",
            "Radiant Rhythms",
            "Eclectic Groove Gathering",
            "Celestial Sound Circus",
            "Heartbeat Harmony",
            "Mindful Melodies",
            "Edge of the Horizon",
            "Tribal Treasures",
            "Neon Symphony Night",
            "Rhythmic Radiance",
            "Euphoria on the Edge",
            "Midnight Glow Gala",
            "Moonlit Melodies",
            "Retro Futuristic Vibes",
            "Timeless Tracks Showcase",
            "Sunset Glow Symphony",
            "Sonic Dreamscapes",
            "Canyon Echoes",
            "Groove Horizons",
            "Euphoric Crescendo",
            "Drifting Dream Beats",
            "Eclectic Echo Festival",
            "Beats of the Bayou",
            "Serenade of the Skies",
            "Industrial Pulse Party",
            "Carnival Pulse Parade",
            "Cosmic Carnival Beats",
            "Beyond the Groove",
            "Starry Night Melodies",
            "Bohemian Beats Bash",
            "Galaxy Groove Getaway",
            "Wind and String Symphony",
            "Phoenix Festival of Sound",
            "Tidal Wave Tunes",
            "Sound Garden Live",
            "Cliffside Concert",
            "Skyfall Serenades",
            "Under the Canopy",
            "Sonic Labyrinth",
            "Rainbow Groove Gala",
            "Sunset Groove Gathering",
            "Vibrant Pulse Festival",
            "Acoustic Galaxy",
            "Mountain Top Jams",
            "Rhapsody in Waves",
            "Timeless Symphony",
            "Floating Beats Fiesta",
            "Skyline Grooves",
            "Sunrise Serenade",
            "Stardust Beats",
            "Serenade in the Rain",
            "Groove of the Ancients",
            "Interstellar Beats",
            "Sonic Waterfall",
            "Twilight Groove Gathering",
            "Whispers of the Wild",
            "Sky High Symphony",
            "Ember Beats Festival",
            "Cosmic Serenade",
            "Neon Waves Bash",
            "Shadow Rhythms",
            "Dreamcatcher Vibes",
            "Spirit of Soundscape",
            "Reverie of Rhythms",
            "Vibrations of the Void",
            "Kaleidoscope Sound Showcase",
            "Pulse in the Shadows",
            "Glimmer Groove Gala",
            "Galaxy Pulse Festival",
            "Mystic Rhythm Circle",
            "Night Bloom Beats",
            "Fireworks of Sound",
            "City Rhythm Revelations",
            "Symphony of Dimensions",
            "Sunbeam Serenade",
            "Electric Pulse Horizon",
            "Future Groove Gala",
            "Chords of Enchantment",
            "Glowstick Groove Bash",
            "Primal Beats Showcase",
            "Festival in the Fog",
            "Harmonies on the Hill",
            "Sound Spectrum Bash",
            "Funk in the Moonlight",
            "Orbit Beats Bash",
            "Ripple Rhythms",
            "Infinite Harmony Night",
            "Ethereal Groove Festival",
            "Sparkle Sound Spectacle",
            "Beats and Bubbles",
            "Electro Euphoria",
            "High Tide Beats",
            "Rhythm of the Sky",
            "Groove Illuminated",
            "Forest Symphony",
            "Interstellar Illumination",
            "Midnight Mirage Groove",
            "Deep Sea Melodies",
            "Dream Voyage Serenade",
            "Euphoric Dusk Tunes",
            "Northern Star Serenades",
            "Electric Forest Fest",
            "Sonic Sojourn",
            "Magnetic Pulse Gathering",
            "Crystal Symphony Nights",
            "Ember Echoes Festival",
            "Vortex of Vibes",
            "Neon Pulse Party",
            "Serene Groove Circle",
            "Harmony Beyond Borders",
            "Velvet Pulse Night",
            "Twilight Glow Rhythms",
            "Beyond the Horizon Beats",
            "Skyline Groove Symphony",
            "Cosmic Twilight Tunes",
            "Ember Symphony Festival",
            "Lush Neon Beats",
            "Sonic Daydream Bash",
            "Rhythm in the Rain",
            "Euphoria in the Stars",
            "Magnetic Symphony Night",
            "Galactic Harmonies",
            "Skyline Glow Sessions",
            "Golden Groove Gathering",
            "Desert Glow Serenade",
            "Aurora of Sound",
            "Horizon Beats Bash",
            "Sonic Euphoria Circle",
            "Celestial Groove Odyssey",
            "Solar Flare Symphony",
            "Under the Neon Sky",
            "Rising Rhythms Festival",
            "Echoes in the Canyon",
            "Lights and Beats Spectacle",
            "Shadows and Strings",
            "Galactic Rhythm Voyage",
            "Moonlight Harmony",
            "Voices of the Wild",
            "Desert Mirage Beats",
            "Prism of Sound",
            "Riverside Rhapsody",
            "Crystal Clear Concert",
            "Golden Strings Serenade",
            "Skyline Pulse Festival",
            "Sonic Voyage Symphony",
            "Lunar Legends Live",
            "Vibrations in the Valley",
            "Shimmering Soundscapes",
            "Melodic Horizons",
            "Whispering Strings Showcase",
            "Vortex of Rhythms",
            "Galactic Groove Fusion",
            "Silent Stars Serenade",
            "Festival of Vibrance",
            "Rhythm of the Tides",
            "Sapphire Strings Soiree",
            "Serenade Under the Sea",
            "Infinity Groove Gathering",
            "Cosmic Treble Celebration",
            "Beats Across Time",
            "Twilight Soundscape",
            "Dreamers' Symphony",
            "Urban Lights Harmony",
            "Chasing the Melody",
            "Stars and Strings Festival",
            "Sonic Twilight Symphony",
            "Harmonic Pulse Nights",
            "Midnight Groove Odyssey",
            "City Glow Concert",
            "Fields of Melody",
            "Reverberation Festival",
            "Strings of Eternity",
            "Neon Horizon Beats",
            "Voices of the Galaxy",
            "Edge of the Soundwave",
            "Dreamlike Harmonies",
            "Electric Echo Nights",
            "Ambient Treasures",
            "Pulse of the Waves",
            "Sky Beats Gathering",
            "Mountain Glow Symphony",
            "Future Frequencies",
            "Nightfall Rhythms",
            "Underworld Soundscapes",
            "Chasing the Groove",
            "Electric Tides Festival",
            "Shadow Pulse Beats",
            "Enchanted Grove Serenade",
            "Hidden Echoes Live",
            "Pulse in the Pines",
            "Retro Rhythm Renaissance",
            "Timeless Grooves Gathering",
            "Sound Spiral Showcase",
            "Luminous Beats Bash",
            "Harmony in Motion",
            "Stargazers' Serenade",
            "Primal Pulse Festival",
            "Celestial Beat Celebration",
            "Midnight Wanderers' Symphony",
            "Futuristic Rhythms",
            "Chords Beneath the Stars",
            "Sonic Blaze Festival",
            "Sunset Groove Odyssey",
            "Serenade of the Aurora",
            "Beats of the Spectrum",
            "Acoustic Adventures",
            "Pulse of Paradise",
            "Horizon Serenade",
            "Golden Glow Festival",
            "Wildwood Beats",
            "Crystal Coast Symphony",
            "Rhythms in Reflection",
            "Beats on the Breeze",
            "Stellar Strings Showcase",
            "Sunrise Harmony",
            "Neon Jungle Groove",
            "Whispers of the Tide",
            "Ambient Pulse Festival",
            "Skyline Serenade",
            "Fields of Sound",
            "Electro Luminescence",
            "Dawn's Echo Beats",
            "Sonic Solar Soiree",
            "Chords of the Cosmos",
            "Pulse in the Shadows",
            "Shimmering Groove Odyssey",
            "Prismatic Symphony Nights",
            "Beats Beyond Infinity",
            "Dreamcatcher Beats",
            "Twilight Rhythms Festival",
            "Timeless Harmony Nights",
            "Echoes of the Skyline",
            "Neon Dream Odyssey",
            "Rhythms Under the Stars",
            "Voices in the Canyon",
            "Vibrations of the Horizon",
            "Sonic Serenade Sessions",
            "Rhapsody in the Hills",
            "Lights of the Serenade",
            "Electric Skylines Bash",
            "Hidden Beats Festival",
            "Celestial Harmony Gala",
            "Pulse of Eternity",
            "Chords of the Wild",
            "Golden Echoes Symphony",
            "Galactic Strings Festival",
            "Infinity Harmony Nights",
            "Fields of Soundwave",
            "Neon Stars Serenade",
            "Velvet Skies Groove",
            "Whispers of Infinity",
            "Rhythms of the Caverns",
            "Serenade Under the Horizon",
            "Dreamer's Groove Gathering",
            "Silver Sky Soundscapes",
            "Electric Harmony Odyssey",
            "Urban Serenade Bash",
            "Beats Among the Clouds",
            "Timeless Rhythm Voyage",
            "Festival of the Cosmos",
            "Chords of the Dawn",
            "Shimmering Pulse Night",
            "Vortex of Melody",
            "Crystal Beats Celebration",
            "Celestial Strings Soiree",
            "Chasing the Horizon",
            "Sonic Vibrations Gala",
            "Dreamweaver Beats",
            "Pulse in the Stars",
            "Skyline Harmony Gathering",
            "Electric Groove Odyssey",
            "Rhapsody in Twilight",
            "Golden Serenade Gathering",
            "Dreamscapes of Sound",
            "Stellar Pulse Festival",
            "Serenade of the Sea",
            "Timeless Vibrations",
            "Urban Euphoria Nights",
            "Galactic Melody Gala",
            "Prismatic Beats Night",
            "Beats of the Past",
            "Fields of Harmony",
            "Luminous Sky Symphony",
            "Stargazer Groove Bash",
            "Golden Strings Night",
            "Pulse of the Wild",
            "Dreamcatcher Symphony",
            "Whispers in the Valley",
            "Infinity Rhythm Gala",
            "Twilight Groove Circle",
            "Urban Strings Festival",
            "Neon Beats Spectacle",
            "Timeless Rhythm Gala",
            "Pulse in the Rain",
            "Rhythms Under the Aurora",
            "Shadows of Melody",
            "Golden Serenade Soiree",
            "Beats in the Valley",
            "Twilight Serenade Odyssey",
            "Cosmic Groove Celebration",
            "Stargazer Harmony Gathering",
            "Pulse Beyond Borders",
            "Dreamweaver Serenade",
            "Whispers of Eternity",
            "Skyline Beats Festival",
            "Celestial Sound Odyssey",
            "Echoes in the Breeze",
            "Urban Pulse Bash",
            "Neon Harmony Nights",
            "Rhythms of the Fields",
            "Silver Glow Symphony",
            "Shimmering Sounds Festival",
            "Crystal Skies Serenade",
            "Chords of the Valley",
            "Dreamcatcher Sound Odyssey",
            "Electric Horizon Bash",
            "Fields of Beats",
            "Galactic Echoes Festival",
            "Timeless Sound Odyssey",
            "Vortex of Vibrations",
            "Twilight Beats Gathering",
            "Pulse of the Canyon",
            "Urban Serenade Symphony",
            "Prismatic Strings Soiree",
            "Golden Glow Serenade",
            "Velvet Harmony Nights",
            "Dreamweaver Groove Gathering",
            "Infinity Soundscape",
            "Neon Pulse Gala",
            "Echoes of the Skies",
            "Golden Beats Celebration",
            "Celestial Twilight Serenade",
            "Fields of Soundscape",
            "Vibrations in the Breeze",
            "Crystal Harmony Gala",
            "Prismatic Sound Odyssey",
            "Pulse Beyond Infinity",
            "Timeless Echoes Gathering",
            "Shimmering Skyline Symphony",
            "Infinity Beats Festival",
            "Glorious champions night",
            "Exquisite gaming concert"
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
        LOGGER.info("All performances were created!");
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
        LocalDateTime now = LocalDateTime.now().minusDays(random.nextInt(365 * 2));

        int daysToAdd = random.nextInt(365 * 3);
        int hoursToAdd = random.nextInt(24);
        int minutesToAdd = random.nextInt(60);

        return now.plusDays(daysToAdd).plusHours(hoursToAdd).plusMinutes(minutesToAdd);
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