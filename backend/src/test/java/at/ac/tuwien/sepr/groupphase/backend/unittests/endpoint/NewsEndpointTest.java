package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.NewsEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsEndpointTest {
    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsEndpoint newsEndpoint;

    public NewsEndpointTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNewsSuccessful() {
        List<NewsDetailDto> mockNewsList = Arrays.asList(
            new NewsDetailDto(11L, "Title1", "Summary1", "Content1", null, LocalDate.now()),
            new NewsDetailDto(22L, "Title2", "Summary2", "Content2", null, LocalDate.now())
        );

        when(newsService.getNews()).thenReturn(mockNewsList);

        ResponseEntity<List<NewsDetailDto>> response = newsEndpoint.getNews();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockNewsList.size(), response.getBody().size());

        verify(newsService, times(1)).getNews();
    }
    @Test
    void testGetByIdSuccessful() {
        long id = 321L;
        NewsDetailDto mockNews = new NewsDetailDto(id, "Title", "Summary", "Content", null, LocalDate.now());
        when(newsService.getById(id)).thenReturn(mockNews);

        ResponseEntity<NewsDetailDto> response = newsEndpoint.getById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockNews, response.getBody());
        verify(newsService, times(1)).getById(id);
    }

    @Test
    void testGetByIdNotFound() {
        long id = 2L;
        when(newsService.getById(id)).thenThrow(new NotFoundException("News not found"));

        assertThrows(ResponseStatusException.class, () -> newsEndpoint.getById(id));

        verify(newsService, times(1)).getById(id);
    }

    @Test
    void testGetUnreadNewsSuccessful() {
        String email = "user@example.com";
        List<NewsDetailDto> mockNewsList = Arrays.asList(
            new NewsDetailDto(11L, "Title1", "Summary1", "Content1", null, LocalDate.now()),
            new NewsDetailDto(22L, "Title2", "Summary2", "Content2", null, LocalDate.now())
        );

        when(newsService.getUnreadNews(email)).thenReturn(mockNewsList);

        ResponseEntity<List<NewsDetailDto>> response = newsEndpoint.getUnreadNews(email);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockNewsList, response.getBody());
        verify(newsService, times(1)).getUnreadNews(email);
    }

    @Test
    void testGetUnreadNewsReturnsEmptyList() {
        String email = "user@example.com";
        when(newsService.getUnreadNews(email)).thenReturn(List.of());

        ResponseEntity<List<NewsDetailDto>> response = newsEndpoint.getUnreadNews(email);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(newsService, times(1)).getUnreadNews(email);
    }
}
