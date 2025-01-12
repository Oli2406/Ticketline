package at.ac.tuwien.sepr.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class NewsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NewsRepository newsRepository;

    private ApplicationUser testUser;
    private News news1;
    private News news2;
    private News news3;

    @BeforeEach
    void setUp() {
        testUser = new ApplicationUser();
        testUser.setFirstName("Admin");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("securePassword123");
        testUser.setAdmin(false);
        entityManager.persist(testUser);

        news1 = new News();
        news1.setTitle("News 1");
        news1.setContent("Content 1");
        news1.setSummary("Summary 1");
        news1.setDateOfNews(LocalDate.now());
        news1.setImageUrl(null);
        entityManager.persist(news1);

        news2 = new News();
        news2.setTitle("News 2");
        news2.setContent("Content 2");
        news2.setSummary("Summary 2");
        news2.setDateOfNews(LocalDate.now().minusDays(1));
        news2.setImageUrl(null);
        entityManager.persist(news2);

        news3 = new News();
        news3.setTitle("News 3");
        news3.setContent("Content 3");
        news3.setSummary("Summary 3");
        news3.setDateOfNews(LocalDate.now().minusDays(2));
        news3.setImageUrl(null);
        entityManager.persist(news3);

        entityManager.flush();
    }

    @Test
    void testFindUnreadNewsWhenSomeRead() {
        List<Long> readNewsIds = List.of(news1.getNewsId(), news2.getNewsId());
        List<News> unreadNews = newsRepository.findUnreadNews(readNewsIds);

        assertNotNull(unreadNews);
        assertEquals(1, unreadNews.size());
        assertEquals(news3.getNewsId(), unreadNews.get(0).getNewsId());
    }

    @Test
    void testFindUnreadNewsWhenAllRead() {
        List<Long> readNewsIds = List.of(news1.getNewsId(), news2.getNewsId(), news3.getNewsId());
        List<News> unreadNews = newsRepository.findUnreadNews(readNewsIds);

        assertNotNull(unreadNews);
        assertTrue(unreadNews.isEmpty());
    }

    @Test
    void testFindUnreadNewsWhenNoneRead() {
        List<Long> readNewsIds = List.of();
        List<News> unreadNews = newsRepository.findUnreadNews(readNewsIds);

        assertNotNull(unreadNews);
        assertEquals(3, unreadNews.size());
        assertTrue(unreadNews.containsAll(List.of(news1, news2, news3)));
    }
}
