package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsValidator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;

    public NewsValidator(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * Method for validating a NewsCreateMpfDto.
     *
     * @param news - dto for validation
     */
    public void validateNews(NewsCreateMpfDto news) throws ValidationException {
        LOG.info("validateNews({})", news);
        List<String> validationErrors = new ArrayList<>();

        if (news.getTitle() == null || news.getTitle().isEmpty()) {
            validationErrors.add("News title is required");
        } else if (news.getTitle().length() > 64) {
            validationErrors.add("News title cannot exceed 64 characters");
        }

        if (news.getSummary() == null || news.getSummary().isEmpty()) {
            validationErrors.add("News summary is required");
        } else if (news.getSummary().length() > 1024) {
            validationErrors.add("News summary cannot exceed 1024 characters");
        }

        if (news.getContent() == null || news.getContent().isEmpty()) {
            validationErrors.add("News content is required");
        } else if (news.getContent().length() > 4096) {
            validationErrors.add("News content length cannot exceed 255 characters");
        }

        if (!validationErrors.isEmpty()) {
            LOG.warn("News validation failed with errors: {}", validationErrors);
            throw new ValidationException("Validation Exception: ", validationErrors);
        }
    }
}
