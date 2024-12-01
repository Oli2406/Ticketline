package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public class PerformanceDetailDto {

    private Long id;
    private String name;
    private Long artistId;
    private Long locationId;
    private LocalDateTime date;
    private double price;
    private Long ticketNumber;
    private String hall;

    public PerformanceDetailDto(Long id, String name, Long artistId, Long locationId, LocalDateTime date, double price, Long ticketNumber, String hall) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.locationId = locationId;
        this.date = date;
        this.price = price;
        this.ticketNumber = ticketNumber;
        this.hall = hall;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }
}
