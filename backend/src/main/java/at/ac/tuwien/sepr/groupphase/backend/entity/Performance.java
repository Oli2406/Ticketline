package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column
    private LocalDateTime date;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String hall;

    @Column(nullable = false)
    private Long ticketNumber;

    public Performance() {
    }

    public Performance(String name, Long artistId, Long locationId, LocalDateTime date, Double price, String hall, Long ticketNumber) {
        this.name = name;
        this.artistId = artistId;
        this.locationId = locationId;
        this.date = date;
        this.price = price;
        this.hall = hall;
        this.ticketNumber = ticketNumber;
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
