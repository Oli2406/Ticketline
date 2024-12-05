package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface NewsService {

    /**
     * Create new News as an Admin.
     *
     * @param mpfDto the newsCreateMPFDTo input for creation
     * @return the created news
     * @throws ValidationException when the validation of the news fails
     * @throws IOException         when there is an error with the image input
     * @throws URISyntaxException  when there is an error with the image urls
     */
    NewsCreateDto createNews(NewsCreateMpfDto mpfDto)
        throws ValidationException, IOException, URISyntaxException;

    /**
     * Retrieves a list of all news articles.
     *
     * @return A list of {@link NewsDetailDto} objects representing the news articles.
     */
    List<NewsDetailDto> getNews();

    /**
     * Retrieves a list of unread news articles for a given user.
     *
     * @param email The email address of the user.
     * @return A list of {@link NewsDetailDto} objects representing the unread news articles.
     */
    List<NewsDetailDto> getUnreadNews(String email);


}
