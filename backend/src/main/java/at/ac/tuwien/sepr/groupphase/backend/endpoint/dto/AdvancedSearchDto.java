package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdvancedSearchDto {
    private Long eventId;
    private String eventTitle;
    private String eventDescription;
    private LocalDate eventDateOfEvent;
    private String eventCategory;
    private Integer eventDuration;

    private Long performanceId;
    private String performanceName;
    private LocalDateTime performanceDate;
    private BigDecimal performancePrice;
    private Long performanceTicketNumber;
    private String performanceHall;

    private Long artistId;
    private String artistFirstName;
    private String artistSurname;
    private String artistArtistName;

    private Long locationId;
    private String locationName;
    private String locationStreet;
    private String locationCity;
    private String locationCountry;
    private String locationPostalCode;

    public AdvancedSearchDto(Long eventId, String eventTitle, String eventDescription,
                             LocalDate eventDateOfEvent, String eventCategory, Integer eventDuration,
                             Long performanceId, String performanceName, LocalDateTime performanceDate,
                             BigDecimal performancePrice, Long performanceTicketNumber, String performanceHall,
                             Long artistId, String artistFirstName, String artistSurname, String artistArtistName,
                             Long locationId, String locationName, String locationStreet,
                             String locationCity, String locationCountry, String locationPostalCode) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDateOfEvent = eventDateOfEvent;
        this.eventCategory = eventCategory;
        this.eventDuration = eventDuration;
        this.performanceId = performanceId;
        this.performanceName = performanceName;
        this.performanceDate = performanceDate;
        this.performancePrice = performancePrice;
        this.performanceTicketNumber = performanceTicketNumber;
        this.performanceHall = performanceHall;
        this.artistId = artistId;
        this.artistFirstName = artistFirstName;
        this.artistSurname = artistSurname;
        this.artistArtistName = artistArtistName;
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationStreet = locationStreet;
        this.locationCity = locationCity;
        this.locationCountry = locationCountry;
        this.locationPostalCode = locationPostalCode;
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

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LocalDate getEventDateOfEvent() {
        return eventDateOfEvent;
    }

    public void setEventDateOfEvent(LocalDate eventDateOfEvent) {
        this.eventDateOfEvent = eventDateOfEvent;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Integer getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(Integer eventDuration) {
        this.eventDuration = eventDuration;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public String getPerformanceName() {
        return performanceName;
    }

    public void setPerformanceName(String performanceName) {
        this.performanceName = performanceName;
    }

    public LocalDateTime getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(LocalDateTime performanceDate) {
        this.performanceDate = performanceDate;
    }

    public BigDecimal getPerformancePrice() {
        return performancePrice;
    }

    public void setPerformancePrice(BigDecimal performancePrice) {
        this.performancePrice = performancePrice;
    }

    public Long getPerformanceTicketNumber() {
        return performanceTicketNumber;
    }

    public void setPerformanceTicketNumber(Long performanceTicketNumber) {
        this.performanceTicketNumber = performanceTicketNumber;
    }

    public String getPerformanceHall() {
        return performanceHall;
    }

    public void setPerformanceHall(String performanceHall) {
        this.performanceHall = performanceHall;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistFirstName() {
        return artistFirstName;
    }

    public void setArtistFirstName(String artistFirstName) {
        this.artistFirstName = artistFirstName;
    }

    public String getArtistSurname() {
        return artistSurname;
    }

    public void setArtistSurname(String artistSurname) {
        this.artistSurname = artistSurname;
    }

    public String getArtistArtistName() {
        return artistArtistName;
    }

    public void setArtistArtistName(String artistArtistName) {
        this.artistArtistName = artistArtistName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationStreet() {
        return locationStreet;
    }

    public void setLocationStreet(String locationStreet) {
        this.locationStreet = locationStreet;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public String getLocationPostalCode() {
        return locationPostalCode;
    }

    public void setLocationPostalCode(String locationPostalCode) {
        this.locationPostalCode = locationPostalCode;
    }
}

