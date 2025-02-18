package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import jakarta.annotation.security.PermitAll;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(NewsEndpoint.BASE_PATH)
public class NewsEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String BASE_PATH = "/api/v1/news";
    private final NewsService newsService;

    public NewsEndpoint(NewsService newsService) {
        this.newsService = newsService;
    }

    @PermitAll
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NewsCreateDto createNews(@ModelAttribute NewsCreateMpfDto news)
        throws ValidationException, IOException, URISyntaxException {
        LOGGER.info("POST /api/v1/create");
        LOGGER.debug("Request parameters: {}", news);
        return this.newsService.createNews(news);
    }

    @PermitAll
    @GetMapping("{id}")
    public ResponseEntity<NewsDetailDto> getById(@PathVariable("id") long id) {
        LOGGER.info("GET " + BASE_PATH + "/{}", id);

        try {
            NewsDetailDto news = newsService.getById(id);
            return ResponseEntity.ok(news);
        } catch (NotFoundException e) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<NewsDetailDto>> getNews() {
        LOGGER.info("GET " + BASE_PATH);

        List<NewsDetailDto> news = newsService.getNews();
        return ResponseEntity.ok(news);
    }

    @PermitAll
    @GetMapping(value = "/unread")
    public ResponseEntity<List<NewsDetailDto>> getUnreadNews(@RequestParam String email) {
        LOGGER.info("GET " + BASE_PATH + "/unread?email=" + email);
        List<NewsDetailDto> news = newsService.getUnreadNews(email);
        return ResponseEntity.ok(news);
    }
}
