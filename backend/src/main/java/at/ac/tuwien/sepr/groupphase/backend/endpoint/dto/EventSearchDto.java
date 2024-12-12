package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

public class EventSearchDto {
    private String title;
    private String category;
    private LocalDate dateEarliest;
    private LocalDate dateLatest;
    private Integer minDuration;
    private Integer maxDuration;

    public EventSearchDto(String title, String category, LocalDate dateEarliest, LocalDate dateLatest, Integer minDuration, Integer maxDuration) {
        this.title = title;
        this.category = category;
        this.dateEarliest = dateEarliest;
        this.dateLatest = dateLatest;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public LocalDate getDateLatest() {
        return dateLatest;
    }

    public void setDateLatest(LocalDate dateLatest) {
        this.dateLatest = dateLatest;
    }

    public LocalDate getDateEarliest() {
        return dateEarliest;
    }

    public void setDateEarliest(LocalDate dateEarliest) {
        this.dateEarliest = dateEarliest;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "EventSearchDto{"
            + "title='" + title + '\''
            + ", category='" + category + '\''
            + ", dateEarliest=" + dateEarliest
            + ", dateLatest=" + dateLatest
            + ", minDuration=" + minDuration
            + ", maxDuration=" + maxDuration
            + '}';
    }

}
