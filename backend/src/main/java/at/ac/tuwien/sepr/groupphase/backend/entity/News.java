package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "summary", nullable = false, length = 1024)
    private String summary;

    @Column(name = "content", nullable = false, length = 4096)
    private String content;

    @Column(name = "date_of_news", nullable = false)
    private LocalDate dateOfNews = LocalDate.now();


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

    public long getId() {
        return id;
    }

    public void setId(long newsId) {
        this.id = newsId;
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
}
