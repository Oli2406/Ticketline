package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceDetailDto {

    private Long performanceId;
    private String name;
    private Long artistId;
    private Long locationId;
    private LocalDate date;
    private Long ticketNumber;
    private String hall;

    public PerformanceDetailDto(Long performanceId, String name, Long artistId, Long locationId, LocalDate date, Long ticketNumber, String hall) {
        this.performanceId = performanceId;
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

    public void setPerformaanceId(Long id) {
        this.performanceId = id;
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
