package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import jakarta.annotation.security.PermitAll;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
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
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NewsCreateDto createNews(@ModelAttribute NewsCreateMpfDto news)
        throws ValidationException, IOException, URISyntaxException {
        LOG.info("POST /api/v1/createNews");
        LOG.debug("Request parameters: {}", news);

        try {
            System.out.println("We have reached the endpoint");
            return this.newsService.createNews(news);
        } catch (ValidationException e) {
            throw new ValidationException("Validation for News failed");
        } catch (IOException e) {
            throw new IOException("There was an error with the image input");
        } catch (URISyntaxException e) {
            throw new URISyntaxException("An error occurred with the image URLs", "URL Error");
        }
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<NewsDetailDto>> getUnreadNews(@RequestParam String email) {
        LOG.info("GET " + BASE_PATH);
        List<NewsDetailDto> news = newsService.getUnreadNews(email);
        return ResponseEntity.ok(news);
    }

    @PermitAll
    @GetMapping("create-news")
    public String getRoot() {
        return "Get stub";
    }
}
