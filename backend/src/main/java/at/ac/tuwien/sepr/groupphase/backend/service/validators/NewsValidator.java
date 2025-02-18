package at.ac.tuwien.sepr.groupphase.backend.service.validators;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());

    /**
     * Method for validating a NewsCreateMpfDto.
     *
     * @param news - dto for validation
     */
    public void validateNews(NewsCreateMpfDto news) throws ValidationException {
        LOGGER.info("validateNews({})", news);
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
            validationErrors.add("News content cannot exceed 4096 characters");
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.warn("News validation failed with errors: {}", validationErrors);
            throw new ValidationException("Validation Exception: ", validationErrors);
        }
    }
}
