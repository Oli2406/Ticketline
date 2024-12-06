package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long artistId;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long ticketNumber;

    @Column(nullable = false)
    private String hall;

    /*
     * @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
     * private List<Ticket> tickets; // Wird nicht als Spalte in der Tabelle gespeichert
     */

    public Performance() {
    }

    public Performance(String name, Long artistId, Long locationId, LocalDate date, Long ticketNumber, String hall) {
        this.name = name;
        this.artistId = artistId;
        this.locationId = locationId;
        this.date = date;
        this.ticketNumber = ticketNumber;
        this.hall = hall;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
}
