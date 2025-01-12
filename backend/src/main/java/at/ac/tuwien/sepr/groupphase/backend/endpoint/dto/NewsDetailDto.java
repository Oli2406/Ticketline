package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.List;

public class NewsDetailDto {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private List<String> images;
    private LocalDate date;

    public NewsDetailDto(long newsId, String title, String summary, String content, List<String> images, LocalDate date) {
        this.id = newsId;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.images = images;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
}
