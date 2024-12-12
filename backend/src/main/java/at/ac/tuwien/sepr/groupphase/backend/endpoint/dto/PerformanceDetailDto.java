package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Location;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceDetailDto {

    private Long performanceId;
    private String name;
    private Long artistId;
    private Long locationId;
    private LocalDateTime date;
    private BigDecimal price;
    private Long ticketNumber;
    private String hall;
    private Artist artist;
    private Location location;
    private Integer duration;

    public PerformanceDetailDto(Long performanceId, String name, Long artistId, Long locationId, LocalDateTime date, BigDecimal price, Long ticketNumber, String hall,
                                Artist artist, Location location, Integer duration) {
        this.performanceId = performanceId;
        this.name = name;
        this.artistId = artistId;
        this.locationId = locationId;
        this.date = date;
        this.price = price;
        this.ticketNumber = ticketNumber;
        this.hall = hall;
        this.artist = artist;
        this.location = location;
        this.duration = duration;
    }

    public PerformanceDetailDto(String performanceName, String artistName, String locationName, LocalDateTime performanceDate, BigDecimal price) {
        this.name = performanceName;
        this.date = performanceDate;
        this.price = price;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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

    public Artist getArtist() {
        return artist;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}