package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("generateData")
public class NewsDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;

    public NewsDataGenerator(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @PostConstruct
    public void loadInitialData() {
        LOGGER.debug("generating news");

        createNewsIfNotExists("Exciting Music Festival Coming to Town!",
            "Get ready for the biggest music festival of the year!",
            "The Summer Beats Festival is coming to New York City on June 15, 2024. With a lineup of incredible artists, food vendors, and activities, this is an event you won't want to miss. Tickets are on sale now!",
            LocalDate.of(2024, 12, 20),
            new ArrayList<>(List.of("1.png")));

        createNewsIfNotExists("Interactive Art Exhibit Opens at Modern Art Museum",
            "Experience art in a whole new way.",
            "The Modern Art Museum is excited to announce the opening of its new interactive art exhibit, Visionary Realities, on January 5, 2025. This immersive exhibit invites visitors to engage with art through technology and explore their creativity. Don't miss this unique experience!",
            LocalDate.of(2025, 1, 5),
            new ArrayList<>(List.of("2.png")));

        createNewsIfNotExists("Local Band The Soundwaves Live in Concert",
            "The Soundwaves are playing a special show in their hometown!",
            "Catch local favorites The Soundwaves live in concert at The Harmony Hall on December 28, 2024. This intimate show will feature songs from their latest album and some classic hits. Get your tickets before they sell out!",
            LocalDate.of(2024, 12, 28),
            null);

        createNewsIfNotExists("Food and Wine Festival Returns to San Francisco",
            "Indulge in delicious food and drinks at the San Francisco Food and Wine Festival.",
            "The San Francisco Food and Wine Festival is back for another year of culinary delights. From April 10 to April 14, 2024, sample delicious food from local vendors, enjoy wine tastings, and learn from expert chefs. Get your tickets now!",
            LocalDate.of(2024, 4, 10),
            new ArrayList<>(List.of("3.png")));

        createNewsIfNotExists("New Year's Eve Gala at the Grand Palace Hotel",
            "Ring in the new year in style at the Grand Palace Hotel.",
            "Celebrate New Year's Eve at the glamorous Grand Palace Hotel. Enjoy a delicious dinner, live music, and dancing. This black-tie event is the perfect way to welcome the new year. Book your tickets now!",
            LocalDate.of(2024, 12, 31),
            null);

        createNewsIfNotExists("Children's Theatre Presents The Enchanted Forest",
            "Bring the whole family to enjoy The Enchanted Forest at the Starbright Theatre.",
            "The Starbright Theatre is proud to present The Enchanted Forest, a delightful children's play perfect for the whole family. Shows run from January 15 to February 10, 2024. Get your tickets now!",
            LocalDate.of(2024, 1, 15),
            null);

        createNewsIfNotExists("Outdoor Movie Night in the Park",
            "Enjoy a movie under the stars at Central Park.",
            "Join us for a free outdoor movie night at Central Park on June 20, 2024. We'll be showing The Greatest Adventure on a giant screen. Bring your blankets, chairs, and snacks for a fun night under the stars.",
            LocalDate.of(2024, 6, 20),
            new ArrayList<>(List.of("4.jpg")));

        createNewsIfNotExists("Summer Concert Series at the Garden Amphitheater",
            "Enjoy live music all summer long at the Garden Amphitheater.",
            "The Garden Amphitheater is hosting a summer concert series featuring a variety of musical genres. From July 1 to August 31, 2024, enjoy live music every weekend. Check out the lineup and get your tickets today!",
            LocalDate.of(2024, 7, 1),
            null);

        createNewsIfNotExists("Comedy Night at the Laugh Lounge",
            "Laugh the night away with some of the funniest comedians around!",
            "Join us for a night of side-splitting comedy at the Laugh Lounge on January 10, 2024. Featuring John Doe, Jane Smith, and Tim Brown, this show is guaranteed to leave you in stitches. Book your tickets now!",
            LocalDate.of(2024, 1, 10),
            new ArrayList<>(List.of("5.jpg")));

        createNewsIfNotExists("City Farmers Market Opens for the Season",
            "Fresh, local produce is back at the City Farmers Market.",
            "The City Farmers Market is officially open for the season! Every Saturday from 8:00 AM to 2:00 PM, find fresh produce, baked goods, and handcrafted items from local vendors.",
            LocalDate.of(2024, 5, 1),
            null);

        createNewsIfNotExists("Annual Boston Marathon",
            "Run for a cause in the Boston Marathon!",
            "The annual Boston Marathon is back on March 15, 2024. Whether you're a seasoned runner or a first-timer, this is a great opportunity to challenge yourself and support a good cause. Register now and start training!",
            LocalDate.of(2024, 3, 15),
            new ArrayList<>(List.of("6.png")));

        createNewsIfNotExists("Photography Exhibition at the Downtown Gallery",
            "See stunning photography by local artists at the Downtown Gallery.",
            "The Downtown Gallery is pleased to present a new photography exhibition featuring the work of talented local artists. The exhibition opens on April 15, 2024, and runs through May 15, 2024.",
            LocalDate.of(2024, 4, 15),
            null);

        createNewsIfNotExists("Yoga in the Park",
            "Start your day with a free yoga class in the park.",
            "Join us for a free yoga class in Sunset Park every Sunday morning at 9:00 AM. All levels are welcome.",
            LocalDate.of(2024, 5, 10),
            null);

        createNewsIfNotExists("Book Signing with Sarah Johnson",
            "Meet Sarah Johnson and get your book signed.",
            "Sarah Johnson will be signing copies of her new book, The Last Journey, at City Lights Bookstore on January 20, 2025, at 3:00 PM. Don't miss this chance to meet the author and hear about her work.",
            LocalDate.of(2025, 1, 20),
            new ArrayList<>(List.of("7.png")));

        createNewsIfNotExists("Volunteer Day at the Community Center",
            "Give back to the community by volunteering with the Community Center.",
            "The Community Center is hosting a volunteer day on January 15, 2025. Join us to help with park cleanup and make a difference in our community.",
            LocalDate.of(2025, 1, 15),
            null);

        createNewsIfNotExists("Workshop: Learn to Paint",
            "Develop new skills with a free workshop at the Creative Arts Center.",
            "The Creative Arts Center is offering a workshop on painting on January 25, 2025. This beginner-friendly workshop is open to anyone interested in learning a new skill. Register now to reserve your spot.",
            LocalDate.of(2025, 1, 25),
            new ArrayList<>(List.of("8.jpg")));

        createNewsIfNotExists("Community Garage Sale",
            "Find treasures and support your neighbors at the community garage sale.",
            "The annual community garage sale is happening on June 5, 2024, from 8:00 AM to 4:00 PM. Find great deals on clothes, furniture, toys, and more. All proceeds benefit the Local Charity Foundation.",
            LocalDate.of(2024, 6, 5),
            new ArrayList<>(List.of("9.png")));

        createNewsIfNotExists("Open Mic Night at the Cozy Café",
            "Share your talent at the open mic night.",
            "The Cozy Café is hosting an open mic night on December 18, 2024. Singers, musicians, poets, and comedians are all welcome to perform. Sign up starts at 6:00 PM.",
            LocalDate.of(2024, 12, 18),
            null);

        createNewsIfNotExists("Holiday Craft Fair",
            "Find unique gifts and support local artisans at the holiday craft fair.",
            "The annual holiday craft fair is coming to the Town Hall on December 10, 2024. Find handcrafted gifts, decorations, and treats from local artisans. This is the perfect place to find unique gifts for everyone on your list.",
            LocalDate.of(2024, 12, 10),
            new ArrayList<>(List.of("10.jpg")));


        createNewsIfNotExists("Jazz Night at Blue Note Café",
            "An evening of soulful jazz music.",
            "Experience an unforgettable evening of soulful jazz music at the Blue Note Café. Featuring a live performance by renowned saxophonist Michael Carter, this is a night for music lovers.",
            LocalDate.of(2023, 11, 15),
            null);

        createNewsIfNotExists("Annual Charity Gala Raises Funds for Local Shelter",
            "A successful night for a great cause.",
            "The Annual Charity Gala at the Grand Ballroom raised over $100,000 for the City Homeless Shelter. Attendees enjoyed a night of fine dining, music, and auctions.",
            LocalDate.of(2023, 10, 10),
            new ArrayList<>(List.of("11.jpg")));

        createNewsIfNotExists("Fall Harvest Festival Delights Visitors",
            "Celebrate the season at the Fall Harvest Festival.",
            "Families flocked to the Fall Harvest Festival at Greenfield Farms. Activities included hayrides, pumpkin picking, and a corn maze.",
            LocalDate.of(2023, 9, 20),
            null);

        createNewsIfNotExists("City Library Hosts Free Book Exchange",
            "Share your love of reading.",
            "The City Library hosted a free book exchange event last weekend, encouraging residents to bring and take books while enjoying a community atmosphere.",
            LocalDate.of(2023, 8, 15),
            new ArrayList<>(List.of("12.jpg")));

        createNewsIfNotExists("Film Screening: Classic Movies Under the Stars",
            "A nostalgic night in the park.",
            "The Community Park held a classic movie screening under the stars, showing Casablanca to a crowd of over 300 attendees.",
            LocalDate.of(2023, 7, 18),
            null);

        createNewsIfNotExists("Local History Museum Opens New Exhibit",
            "Step back in time with the latest exhibit.",
            "The Local History Museum unveiled its newest exhibit, Life in the 1800s, offering a glimpse into the past with artifacts and immersive displays.",
            LocalDate.of(2023, 6, 25),
            new ArrayList<>(List.of("13.jpg")));

        createNewsIfNotExists("Spring Carnival Brings Joy to the Community",
            "Fun for all ages at the Spring Carnival.",
            "The annual Spring Carnival at Riverside Park featured rides, games, and delicious food. Proceeds supported local youth programs.",
            LocalDate.of(2023, 5, 12),
            null);

        createNewsIfNotExists("Poetry Reading Night at the Writers' Circle",
            "A night of spoken word and creativity.",
            "Local poets gathered at the Writers' Circle for an intimate night of poetry readings, celebrating the art of storytelling.",
            LocalDate.of(2023, 4, 20),
            new ArrayList<>(List.of("14.jpg")));

        createNewsIfNotExists("Art Auction Raises Funds for Education Programs",
            "Art for a cause.",
            "The Downtown Art Center hosted an art auction, raising $50,000 to fund after-school education programs in the community.",
            LocalDate.of(2023, 3, 15),
            null);

        createNewsIfNotExists("Community Cleanup Day a Huge Success",
            "Residents come together to beautify the city.",
            "Hundreds of volunteers gathered for Community Cleanup Day, removing trash and planting trees across the city.",
            LocalDate.of(2023, 2, 18),
            new ArrayList<>(List.of("15.jpg")));

        createNewsIfNotExists("Holiday Lights Parade Draws Record Crowds",
            "A magical holiday tradition.",
            "The annual Holiday Lights Parade lit up Main Street with dazzling floats, live music, and Santa Claus greeting attendees.",
            LocalDate.of(2022, 12, 12),
            null);

        createNewsIfNotExists("Thanksgiving Food Drive Feeds Hundreds",
            "Giving back to those in need.",
            "The City Food Bank held its largest Thanksgiving Food Drive yet, providing meals for over 500 families in need.",
            LocalDate.of(2022, 11, 23),
            new ArrayList<>(List.of("16.jpg")));

        createNewsIfNotExists("Halloween Spooktacular at City Park",
            "A frightfully fun night for all ages.",
            "City Park transformed into a Halloween Spooktacular, featuring haunted houses, costume contests, and trick-or-treating for kids.",
            LocalDate.of(2022, 10, 31),
            null);

        createNewsIfNotExists("Back-to-School Bash Prepares Kids for Success",
            "A day to help students get ready for the new year.",
            "The Back-to-School Bash provided free school supplies and haircuts for local students, ensuring a strong start to the academic year.",
            LocalDate.of(2022, 8, 15),
            null);

        LOGGER.info("All news were created!");
    }

    private void createNewsIfNotExists(String title, String summary, String content,
                                       LocalDate date, List<String> images) {
        if (newsRepository.findByTitle(title).isEmpty()) {
            newsRepository.save(new News(title, summary, content, date, images));
        }
    }
}