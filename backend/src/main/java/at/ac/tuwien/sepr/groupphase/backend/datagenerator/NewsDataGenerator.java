package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("datagen")
public class NewsDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private static final Path SOURCE_DIR = Paths.get("./src/test/resources/testImages/").toAbsolutePath().normalize();
    private static final Path TARGET_DIR = Paths.get("./newsImages").toAbsolutePath().normalize();

    public NewsDataGenerator(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        LOGGER.debug("generating news");

        createNewsIfNotExists("Exciting Music Festival Coming to Town!",
            "Get ready for the biggest music festival of the year!",
            "The [Festival Name] is coming to [City] on [Date]. With a lineup of incredible artists, food vendors, and activities, this is an event you won't want to miss. Tickets are on sale now!",
            LocalDate.of(2024, 12, 20),
            new ArrayList<>(List.of("festival.png")));

        createNewsIfNotExists("Interactive Art Exhibit Opens at [Museum Name]",
            "Experience art in a whole new way.",
            "The [Museum Name] is excited to announce the opening of its new interactive art exhibit, [Exhibit Name], on [Date]. This immersive exhibit invites visitors to engage with art through technology and explore their creativity. Don't miss this unique experience!",
            LocalDate.of(2025, 2, 5), new ArrayList<>(List.of("art.png")));

        createNewsIfNotExists("Local Band [Band Name] Live in Concert",
            "[Band Name] is playing a special show in their hometown!",
            "Catch local favorites [Band Name] live in concert at [Venue Name] on [Date]. This intimate show will feature songs from their latest album and some classic hits. Get your tickets before they sell out!",
            LocalDate.of(2024, 12, 28),
            null);

        createNewsIfNotExists("Food and Wine Festival Returns to [City]",
            "Indulge in delicious food and drinks at the [City] Food and Wine Festival.",
            "The [City] Food and Wine Festival is back for another year of culinary delights. From [Date] to [Date], sample delicious food from local vendors, enjoy wine tastings, and learn from expert chefs. Get your tickets now!",
            LocalDate.of(2025, 4, 10),
            new ArrayList<>(List.of("food.png")));

        createNewsIfNotExists("New Year's Eve Gala at the [Hotel Name]",
            "Ring in the new year in style at the [Hotel Name].",
            "Celebrate New Year's Eve at the glamorous [Hotel Name]. Enjoy a delicious dinner, live music, and dancing. This black-tie event is the perfect way to welcome the new year. Book your tickets now!",
            LocalDate.of(2024, 12, 31),
            null);

        createNewsIfNotExists("Children's Theatre Presents [Play Name]",
            "Bring the whole family to enjoy [Play Name] at the [Theatre Name].",
            "The [Theatre Name] is proud to present [Play Name], a delightful children's play perfect for the whole family. Shows run from [Date] to [Date]. Get your tickets now!",
            LocalDate.of(2025, 1, 15),
            null);

        createNewsIfNotExists("Outdoor Movie Night in the Park",
            "Enjoy a movie under the stars at [Park Name].",
            "Join us for a free outdoor movie night at [Park Name] on [Date]. We'll be showing [Movie Name] on a giant screen. Bring your blankets, chairs, and snacks for a fun night under the stars.",
            LocalDate.of(2025, 6, 20),
            new ArrayList<>(List.of("outdoor_movie.jpg")));

        createNewsIfNotExists("Summer Concert Series at the [Venue Name]",
            "Enjoy live music all summer long at the [Venue Name].",
            "The [Venue Name] is hosting a summer concert series featuring a variety of musical genres. From [Date] to [Date], enjoy live music every weekend. Check out the lineup and get your tickets today!",
            LocalDate.of(2025, 7, 1),
            null);

        createNewsIfNotExists("Comedy Night at the [Venue Name]",
            "Laugh the night away with some of the funniest comedians around!",
            "Join us for a night of side-splitting comedy at the [Venue Name] on [Date]. Featuring [Comedian 1], [Comedian 2], and [Comedian 3], this show is guaranteed to leave you in stitches. Book your tickets now!",
            LocalDate.of(2025, 1, 10),
            new ArrayList<>(List.of("commedy.jpg")));

        createNewsIfNotExists("[City] Farmers Market Opens for the Season",
            "Fresh, local produce is back at the [City] Farmers Market.",
            "The [City] Farmers Market is officially open for the season! Every [Day of the week] from [Time] to [Time], find fresh produce, baked goods, and handcrafted items from local vendors.",
            LocalDate.of(2025, 5, 1),
            null);

        createNewsIfNotExists("Annual [City] Marathon",
            "Run for a cause in the [City] Marathon!",
            "The annual [City] Marathon is back on [Date]. Whether you're a seasoned runner or a first-timer, this is a great opportunity to challenge yourself and support a good cause. Register now and start training!",
            LocalDate.of(2025, 3, 15),
            new ArrayList<>(List.of("marathon.png")));

        createNewsIfNotExists("Photography Exhibition at the [Gallery Name]",
            "See stunning photography by local artists at the [Gallery Name].",
            "The [Gallery Name] is pleased to present a new photography exhibition featuring the work of talented local artists. The exhibition opens on [Date] and runs through [Date].",
            LocalDate.of(2025, 4, 15),
            null);

        createNewsIfNotExists("Yoga in the Park",
            "Start your day with a free yoga class in the park.",
            "Join us for a free yoga class in [Park Name] every [Day of the week] morning at [Time]. All levels are welcome.",
            LocalDate.of(2025, 5, 10),
            null);

        createNewsIfNotExists("Book Signing with [Author Name]",
            "Meet [Author Name] and get your book signed.",
            "[Author Name] will be signing copies of their new book, [Book Title], at [Bookstore Name] on [Date] at [Time]. Don't miss this chance to meet the author and hear about their work.",
            LocalDate.of(2025, 3, 20),
            new ArrayList<>(List.of("signing.png")));

        createNewsIfNotExists("Volunteer Day at the [Organization Name]",
            "Give back to the community by volunteering with the [Organization Name].",
            "The [Organization Name] is hosting a volunteer day on [Date]. Join us to help with [Volunteer activity] and make a difference in our community.",
            LocalDate.of(2025, 2, 15),
            null);

        createNewsIfNotExists("Workshop: Learn to [Skill]",
            "Develop new skills with a free workshop at the [Venue Name].",
            "The [Venue Name] is offering a workshop on [Skill] on [Date]. This beginner-friendly workshop is open to anyone interested in learning a new skill. Register now to reserve your spot.",
            LocalDate.of(2025, 1, 25),
            new ArrayList<>(List.of("workshop.jpg")));

        createNewsIfNotExists("Community Garage Sale",
            "Find treasures and support your neighbors at the community garage sale.",
            "The annual community garage sale is happening on [Date] from [Time] to [Time]. Find great deals on clothes, furniture, toys, and more. All proceeds benefit the [Charity Name].",
            LocalDate.of(2025, 6, 5),
            new ArrayList<>(List.of("garage.png")));

        createNewsIfNotExists("Open Mic Night at the [Venue Name]",
            "Share your talent at the open mic night.",
            "The [Venue Name] is hosting an open mic night on [Date]. Singers, musicians, poets, and comedians are all welcome to perform. Sign up starts at [Time].",
            LocalDate.of(2024, 12, 18),
            null);

        createNewsIfNotExists("Holiday Craft Fair",
            "Find unique gifts and support local artisans at the holiday craft fair.",
            "The annual holiday craft fair is coming to [Venue Name] on [Date]. Find handcrafted gifts, decorations, and treats from local artisans. This is the perfect place to find unique gifts for everyone on your list.",
            LocalDate.of(2024, 12, 10),
            null);

        copyTestImages();
    }

    private void createNewsIfNotExists(String title, String summary, String content,
                                       LocalDate date, List<String> images) {
        News news = new News(title, summary, content, date, images);
        newsRepository.save(news);
    }

    private void copyTestImages() {
        LOGGER.debug("Copying test images…");

        try {
            if (Files.notExists(TARGET_DIR)) {
                Files.createDirectories(TARGET_DIR);
            }
            Files.walk(SOURCE_DIR)
                .filter(Files::isRegularFile)
                .forEach(this::copyFile);

            LOGGER.debug("Finished copying test images.");
        } catch (IOException e) {
            LOGGER.debug("Error occurred while copying test images: {}", e.getMessage());
        }
    }

    private void copyFile(Path sourcePath) {
        Path targetPath = TARGET_DIR.resolve(SOURCE_DIR.relativize(sourcePath));
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.debug("Copied {} to {}", sourcePath, targetPath);
        } catch (IOException e) {
            LOGGER.debug("Failed to copy file: {} to {}. Error: {}", sourcePath, targetPath, e.getMessage());
        }
    }

}
