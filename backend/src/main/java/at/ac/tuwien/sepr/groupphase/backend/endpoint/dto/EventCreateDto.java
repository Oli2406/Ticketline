package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EventCreateDto {

    private String title;
    private String description;
    private String category;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private List<Long> performanceIds; // List of associated Performance IDs

    public EventCreateDto(String title, String description, String category, LocalDate dateFrom, LocalDate dateTo, List<Long> performanceIds) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.performanceIds = performanceIds;
    }

    public EventCreateDto() {
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

    public List<Long> getPerformanceIds() {
        return performanceIds;
    }

    public void setPerformanceIds(List<Long> performanceIds) {
        this.performanceIds = performanceIds;
    }
}
