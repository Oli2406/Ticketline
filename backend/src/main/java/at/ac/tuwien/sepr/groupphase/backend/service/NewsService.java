package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMPFDto;
import jakarta.xml.bind.ValidationException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface NewsService {

    /**
     * Create new News as an Admin.
     *
     * @param newsCreateMPFDto the newsCreateMPFDTo input for creation
     * @return the created news
     * @throws ValidationException when the validation of the news fails
     * @throws IOException         when there is an error with the image input
     * @throws URISyntaxException  when there is an error with the image urls
     */
    NewsCreateDto createNews(NewsCreateMPFDto newsCreateMPFDto)
        throws ValidationException, IOException, URISyntaxException;

}
