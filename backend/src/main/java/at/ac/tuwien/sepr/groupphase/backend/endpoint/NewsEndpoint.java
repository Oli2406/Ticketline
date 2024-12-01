package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(NewsEndpoint.BASE_PATH)
public class NewsEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String BASE_PATH = "/api/v1/news";
    private final NewsService newsService;

    public NewsEndpoint(NewsService newsService) {
        this.newsService = newsService;
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<NewsDetailDto>> getUnreadNews(@RequestParam String email) {
        LOG.info("GET " + BASE_PATH);
        List<NewsDetailDto> news = newsService.getUnreadNews(email);
        return ResponseEntity.ok(news);
    }
}
