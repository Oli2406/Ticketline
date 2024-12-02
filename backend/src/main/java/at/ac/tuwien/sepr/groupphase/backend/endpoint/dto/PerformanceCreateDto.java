package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceCreateDto {

    private String name;
    private Long artistId;
    private Long locationId;
    private LocalDate date;
    private BigDecimal price;
    private Long ticketNumber;
    private String hall;

    public PerformanceCreateDto(String name, Long artistId, Long locationId, LocalDate date, BigDecimal price, Long ticketNumber, String hall) {
        this.name = name;
        this.artistId = artistId;
        this.locationId = locationId;
        this.date = date;
        this.price = price;
        this.ticketNumber = ticketNumber;
        this.hall = hall;
    }

    public PerformanceCreateDto() {}

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
