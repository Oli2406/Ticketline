package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class TopEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "sold_tickets")
    private Long soldTickets;

    @Column(name = "category")
    private String category;

    @Column(name = "year_filter")
    private Integer year;

    @Column(name = "month_filter")
    private Integer month;

    @Column(name = "update_date")
    private LocalDate updateDate;

    public TopEvent() {
    }

    public TopEvent(Long id, Long eventId, String eventTitle, Long soldTickets, String category, Integer year, Integer month, LocalDate updateDate) {
        this.id = id;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.soldTickets = soldTickets;
        this.category = category;
        this.year = year;
        this.month = month;
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Long getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Long soldTickets) {
        this.soldTickets = soldTickets;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }
}
