package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.NewsEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

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
    void shouldReturnNewsList_whenGetNewsIsCalled() {
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
}
