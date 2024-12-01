package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class NewsCreateMpfDto {

    String title;
    String summary;
    String content;
    MultipartFile[] images;
    LocalDate dateOfNews;

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

    public MultipartFile[] getImages() {
        return images;
    }

    public void setImages(MultipartFile[] images) {
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
        return "NewsCreateMpfDto{"
            + "content='"
            + content
            + '\''
            + ", dateOfNews="
            + dateOfNews
            + ", summary='"
            + summary
            + '\''
            + ", title='"
            + title
            + '\''
            + '}';
    }

    public static final class NewsCreateMPFDtoBuilder {

        private String title;
        private String summary;
        private String content;
        private MultipartFile[] images;
        private LocalDate dateOfNews;

        public NewsCreateMPFDtoBuilder() {
        }

        public NewsCreateMPFDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NewsCreateMPFDtoBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsCreateMPFDtoBuilder content(String content) {
            this.content = content;
            return this;
        }

        public NewsCreateMPFDtoBuilder images(MultipartFile[] images) {
            this.images = images;
            return this;
        }

        public NewsCreateMPFDtoBuilder dateOfNews(LocalDate dateOfNews) {
            this.dateOfNews = dateOfNews;
            return this;
        }

        public NewsCreateMpfDto build() {
            NewsCreateMpfDto newsCreateMPFDto = new NewsCreateMpfDto();
            newsCreateMPFDto.setTitle(title);
            newsCreateMPFDto.setSummary(summary);
            newsCreateMPFDto.setContent(content);
            newsCreateMPFDto.setImages(images);
            newsCreateMPFDto.setDateOfNews(dateOfNews);
            return newsCreateMPFDto;
        }
    }
}
