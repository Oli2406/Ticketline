package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsDetailDto;

import java.util.List;

public interface NewsService {

    /**
     * Retrieves a list of unread news articles for a given user.
     *
     * @param email The email address of the user.
     * @return A list of {@link NewsDetailDto} objects representing the unread news articles.
     */
    List<NewsDetailDto> getUnreadNews(String email);
}
