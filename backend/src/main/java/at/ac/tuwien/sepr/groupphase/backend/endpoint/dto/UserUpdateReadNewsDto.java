package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class UserUpdateReadNewsDto {

    private long newsId;
    private String email;

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
