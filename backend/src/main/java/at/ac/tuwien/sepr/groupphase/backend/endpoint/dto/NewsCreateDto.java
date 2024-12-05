package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.List;

public class NewsCreateDto {

    String title;
    String summary;
    String content;
    List<String> images;
    LocalDate date;

    public NewsCreateDto(String title, String summary, String content, List<String> images,
                         LocalDate date) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.images = images;
        this.date = date;
    }

    public NewsCreateDto() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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
        return "NewsCreateDto{"
            + "content='" + content + '\''
            + ", title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", images=" + List.of(images)
            + ", date=" + date
            + '}';
    }

    public static final class NewsCreateDtoBuilder {

        private String title;
        private String summary;
        private String content;
        private List<String> images;
        private LocalDate date;

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

        public NewsCreateDtoBuilder withImages(List<String> images) {
            this.images = images;
            return this;
        }

        public NewsCreateDtoBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public NewsCreateDto build() {
            NewsCreateDto newsCreateDto = new NewsCreateDto();
            newsCreateDto.setTitle(title);
            newsCreateDto.setSummary(summary);
            newsCreateDto.setContent(content);
            newsCreateDto.setImages(images);
            newsCreateDto.setDate(date);
            return newsCreateDto;
        }
    }
}
