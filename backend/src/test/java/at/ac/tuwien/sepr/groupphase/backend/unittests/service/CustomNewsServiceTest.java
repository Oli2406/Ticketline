package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomNewsService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomNewsServiceTest {

    @InjectMocks
    private CustomNewsService customNewsService;

    @Mock
    private UserService userService;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsMapper newsMapper;

    private ApplicationUser user;
    private News news1;
    private News news2;
    private NewsDetailDto newsDto1;
    private NewsDetailDto newsDto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new ApplicationUser();
        user.setEmail("test@example.com");
        news1 = new News();
        news2 = new News();
        newsDto1 = new NewsDetailDto(1L, "News 1", "Summary 1", "Content 1", null, LocalDate.of(2024, 1, 1));
        newsDto2 = new NewsDetailDto(2L, "News 2", "Summary 2", "Content 2", null, LocalDate.of(2024, 1, 1));

        news1.setNewsId(1L);
        news1.setTitle("News 1");
        news1.setSummary("Summary 1");
        news1.setContent("Content 1");
        news1.setDateOfNews(LocalDate.of(2024, 1, 1));

        news2.setNewsId(2L);
        news2.setTitle("News 2");
        news2.setSummary("Summary 2");
        news2.setContent("Content 2");
        news2.setDateOfNews(LocalDate.of(2024, 1, 1));
    }

    @Test
    void testGetNews() {
        List<News> newsList = List.of(news1, news2);
        when(newsRepository.findAll()).thenReturn(newsList);
        when(newsMapper.entityToDetailDto(news1)).thenReturn(newsDto1);
        when(newsMapper.entityToDetailDto(news2)).thenReturn(newsDto2);

        List<NewsDetailDto> result = customNewsService.getNews();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(newsDto1, result.get(0));
        assertEquals(newsDto2, result.get(1));

        verify(newsRepository, times(1)).findAll();
        verify(newsMapper, times(1)).entityToDetailDto(news1);
        verify(newsMapper, times(1)).entityToDetailDto(news2);
    }

    @Test
    void testGetUnreadNewsWhenSomeRead() {
        user.setReadNewsIds(List.of(1L));

        when(userService.findApplicationUserByEmail(user.getEmail())).thenReturn(user);
        when(newsRepository.findUnreadNews(user.getReadNewsIds())).thenReturn(List.of(news2));
        when(newsMapper.entityToDetailDto(news2)).thenReturn(newsDto2);

        List<NewsDetailDto> result = customNewsService.getUnreadNews(user.getEmail());

        assertEquals(1, result.size());
        assertEquals(newsDto2, result.get(0));

        verify(userService, times(1)).findApplicationUserByEmail(user.getEmail());
        verify(newsRepository, times(1)).findUnreadNews(user.getReadNewsIds());
        verify(newsMapper, times(1)).entityToDetailDto(news2);
    }

    @Test
    void testGetUnreadNewsWhenAllRead() {
        user.setReadNewsIds(List.of(1L, 2L));

        when(userService.findApplicationUserByEmail(user.getEmail())).thenReturn(user);
        when(newsRepository.findUnreadNews(user.getReadNewsIds())).thenReturn(List.of());

        List<NewsDetailDto> result = customNewsService.getUnreadNews(user.getEmail());

        assertEquals(0, result.size());

        verify(userService, times(1)).findApplicationUserByEmail(user.getEmail());
        verify(newsRepository, times(1)).findUnreadNews(user.getReadNewsIds());
        verifyNoInteractions(newsMapper);
    }

    @Test
    void testGetUnreadNewsWhenNoneRead() {
        user.setReadNewsIds(List.of());

        when(userService.findApplicationUserByEmail(user.getEmail())).thenReturn(user);
        when(newsRepository.findUnreadNews(user.getReadNewsIds())).thenReturn(List.of(news1, news2));
        when(newsMapper.entityToDetailDto(news1)).thenReturn(newsDto1);
        when(newsMapper.entityToDetailDto(news2)).thenReturn(newsDto2);

        List<NewsDetailDto> result = customNewsService.getUnreadNews(user.getEmail());

        assertEquals(2, result.size());
        assertEquals(newsDto1, result.get(0));
        assertEquals(newsDto2, result.get(1));

        verify(userService, times(1)).findApplicationUserByEmail(user.getEmail());
        verify(newsRepository, times(1)).findUnreadNews(user.getReadNewsIds());
        verify(newsMapper, times(1)).entityToDetailDto(news1);
        verify(newsMapper, times(1)).entityToDetailDto(news2);
    }
}
