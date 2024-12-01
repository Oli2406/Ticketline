package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EventDetailDto {

    private Long id;
    private String title;
    private String description;
    private String category;
    private LocalDateTime dateOfEvent;
    private int duration;
    private List<Long> performanceIds; // List of associated Performance IDs

    public EventDetailDto(Long id, String title, String description, String category, LocalDateTime dateOfEvent, int duration, List<Long> performanceIds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.dateOfEvent = dateOfEvent;
        this.duration = duration;
        this.performanceIds = performanceIds;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(LocalDateTime dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Long> getPerformanceIds() {
        return performanceIds;
    }

    public void setPerformanceIds(List<Long> performanceIds) {
        this.performanceIds = performanceIds;
    }
}
