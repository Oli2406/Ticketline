package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMPFDto;
import at.ac.tuwien.sepr.groupphase.backend.service.NewsService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {

    private final NewsService newsService;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public NewsEndpoint(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Method for creating news
     *
     * @param news - the data for news creation from the frontend
     * @throws ValidationException - when the dto isn't valid
     * @throws IOException         - when there is an error with the picture input
     * @throws URISyntaxException  - when there occurs an error with the image urls
     */
    @PostMapping
    public NewsCreateDto createNews(@RequestBody NewsCreateMPFDto news)
        throws ValidationException, IOException, URISyntaxException {
        LOG.info("POST" + "/api/v1/news");
        LOG.debug("get request parameters: {}", news);

        try {
            return this.newsService.createNews(news);

        } catch (ValidationException e) {
            throw new ValidationException("Validation for News failed");
        } catch (IOException e) {
            throw new IOException("There was an Error with the image input");
        } catch (URISyntaxException e) {
            throw new URISyntaxException("An error occurred with the image URLs", "URL Error");
        }
    }
}
