package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column
    private LocalDate dateOfEvent;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer duration;

    @ElementCollection
    @CollectionTable(name = "event_performance_ids", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "performance_id")
    private List<Long> performanceIds;

    public Event() {
    }

    public Event(String title, String description, LocalDate dateOfEvent, String category, Integer duration, List<Long> performanceIds) {
        this.title = title;
        this.description = description;
        this.dateOfEvent = dateOfEvent;
        this.category = category;
        this.duration = duration;
        this.performanceIds = performanceIds;
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

    public LocalDate getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(LocalDate dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Long> getPerformanceIds() {
        return performanceIds;
    }

    public void setPerformanceIds(List<Long> performanceIds) {
        this.performanceIds = performanceIds;
    }
}
