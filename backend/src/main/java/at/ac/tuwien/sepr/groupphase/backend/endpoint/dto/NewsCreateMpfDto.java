package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class NewsCreateMpfDto {
    String title;
    String summary;
    String content;
    MultipartFile[] images;
    LocalDate date;

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
            + ", dates="
            + date
            + ", summary='"
            + summary
            + '\''
            + ", title='"
            + title
            + '\''
            + '}';
    }

    public static final class NewsCreateMpfDtoBuilder {

        private String title;
        private String summary;
        private String content;
        private MultipartFile[] images;
        private LocalDate date;

        public NewsCreateMpfDtoBuilder() {
        }

        public NewsCreateMpfDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NewsCreateMpfDtoBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsCreateMpfDtoBuilder content(String content) {
            this.content = content;
            return this;
        }

        public NewsCreateMpfDtoBuilder images(MultipartFile[] images) {
            this.images = images;
            return this;
        }

        public NewsCreateMpfDtoBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public NewsCreateMpfDto build() {
            NewsCreateMpfDto mpfDto = new NewsCreateMpfDto();
            mpfDto.setTitle(title);
            mpfDto.setSummary(summary);
            mpfDto.setContent(content);
            mpfDto.setImages(images);
            mpfDto.setDate(date);
            return mpfDto;
        }
    }
}
