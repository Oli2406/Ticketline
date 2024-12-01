package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomNewsService implements NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Autowired
    public CustomNewsService(NewsRepository newsRepository, UserService userService, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.newsMapper = newsMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsDetailDto> getUnreadNews(String email) {
        LOG.trace("getUnreadNews({})", email);
        ApplicationUser user = userService.findApplicationUserByEmail(email);

        return newsRepository.findUnreadNews(user.getReadNewsIds())
            .stream()
            .map(newsMapper::entityToDetailDto)
            .collect(Collectors.toList());
    }
}
