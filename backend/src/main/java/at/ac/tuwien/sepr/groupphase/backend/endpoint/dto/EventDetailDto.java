package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EventDetailDto {

    private Long eventId;
    private String title;
    private String description;
    private String category;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public EventDetailDto(Long eventId, String title, String description, String category, LocalDate dateFrom, LocalDate dateTo) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
}
