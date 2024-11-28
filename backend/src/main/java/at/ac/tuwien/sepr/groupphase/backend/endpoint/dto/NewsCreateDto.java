package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.List;

public class NewsCreateDto {

    String title;
    String summary;
    String content;
    List<String> imageUrl;
    LocalDate dateOfNews;

    public NewsCreateDto(String title, String summary, String content, List<String> imageUrl,
        LocalDate dateOfNews) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.imageUrl = List.of(String.valueOf(imageUrl));
        this.dateOfNews = dateOfNews;
    }

    public NewsCreateDto() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDateOfNews() {
        return dateOfNews;
    }

    public void setDateOfNews(LocalDate dateOfNews) {
        this.dateOfNews = dateOfNews;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "NewsCreateDto{" +
            "content='" + content + '\'' +
            ", title='" + title + '\'' +
            ", summary='" + summary + '\'' +
            ", imageUrl=" + List.of(imageUrl) +
            ", dateOfNews=" + dateOfNews +
            '}';
    }

    public static final class NewsCreateDtoBuilder {

        private String title;
        private String summary;
        private String content;
        private List<String> imageUrl;
        private LocalDate dateOfNews;

        private NewsCreateDtoBuilder() {
        }

        public static NewsCreateDtoBuilder aNewsCreateDto() {
            return new NewsCreateDtoBuilder();
        }

        public NewsCreateDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsCreateDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsCreateDtoBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public NewsCreateDtoBuilder withImageUrl(List<String> imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public NewsCreateDtoBuilder withDateOfNews(LocalDate dateOfNews) {
            this.dateOfNews = dateOfNews;
            return this;
        }

        public NewsCreateDto build() {
            NewsCreateDto newsCreateDto = new NewsCreateDto();
            newsCreateDto.setTitle(title);
            newsCreateDto.setSummary(summary);
            newsCreateDto.setContent(content);
            newsCreateDto.setImageUrl(imageUrl);
            newsCreateDto.setDateOfNews(dateOfNews);
            return newsCreateDto;
        }
    }
}
