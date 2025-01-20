package at.ac.tuwien.sepr.groupphase.backend.unittests.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsCreateMpfDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.NewsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class NewsValidatorTest {

    private NewsValidator newsValidator;

    @BeforeEach
    void setUp() {
        newsValidator = new NewsValidator();
    }

    private NewsCreateMpfDto createValidNewsDto() {
        NewsCreateMpfDto news = new NewsCreateMpfDto();
        news.setTitle("Valid Title");
        news.setSummary("This is a valid news summary.");
        news.setContent("This is valid content for the news.");
        return news;
    }

    @Test
    void validateNews_validInput_noExceptionsThrown() {
        NewsCreateMpfDto news = createValidNewsDto();

        assertDoesNotThrow(() -> newsValidator.validateNews(news));
    }

    @Test
    void validateNews_emptyTitle_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setTitle("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        assertTrue(exception.getErrors().contains("News title is required"));
    }

    @Test
    void validateNews_titleTooLong_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setTitle("A".repeat(65));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        assertTrue(exception.getErrors().contains("News title cannot exceed 64 characters"));
    }

    @Test
    void validateNews_emptySummary_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setSummary("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        assertTrue(exception.getErrors().contains("News summary is required"));
    }

    @Test
    void validateNews_summaryTooLong_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setSummary("A".repeat(1025));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        assertTrue(exception.getErrors().contains("News summary cannot exceed 1024 characters"));
    }

    @Test
    void validateNews_emptyContent_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setContent("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        assertTrue(exception.getErrors().contains("News content is required"));
    }

    @Test
    void validateNews_contentTooLong_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setContent("A".repeat(4097));

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        assertTrue(exception.getErrors().contains("News content cannot exceed 4096 characters"));
    }

    @Test
    void validateNews_multipleErrors_throwsValidationException() {
        NewsCreateMpfDto news = createValidNewsDto();
        news.setTitle("");
        news.setSummary("");
        news.setContent("");

        ValidationException exception = assertThrows(ValidationException.class, () ->
            newsValidator.validateNews(news)
        );

        List<String> errors = exception.errors();

        assertTrue(errors.contains("News title is required"));
        assertTrue(errors.contains("News summary is required"));
        assertTrue(errors.contains("News content is required"));
    }

}
