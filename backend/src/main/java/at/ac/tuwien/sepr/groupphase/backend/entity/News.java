package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long newsId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "summary", nullable = false, length = 1024)
    private String summary;

    @Column(name = "content", nullable = false, length = 4096)
    private String content;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "newsId"))
    @Column(name = "image_url")
    private List<String> imageUrl;

    @Column(name = "date_of_news", nullable = false)
    private LocalDate dateOfNews = LocalDate.now();

    public News(String title, String summary, String content, LocalDate dateOfNews,
                List<String> imageUrl) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.dateOfNews = dateOfNews;
        this.imageUrl = imageUrl;
    }

    public News() {

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

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
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

