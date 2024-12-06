package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;

public class TicketDetailDto {

    private Long ticketId;
    private Long performanceId;
    private Integer rowNumber;
    private Integer seatNumber;
    private PriceCategory priceCategory;
    private TicketType ticketType;
    private SectorType sectorType;
    private BigDecimal price;
    private String status; // e.g., "AVAILABLE", "RESERVED", "SOLD"
    private Hall hall;
    private Long reservationNumber;
    private LocalDate date;

    public TicketDetailDto() {}

    public TicketDetailDto(Long ticketId, Long performanceId, Integer rowNumber, Integer seatNumber,
                           PriceCategory priceCategory, TicketType ticketType, SectorType sectorType,
                           BigDecimal price, String status, Hall hall, Long reservationNumber, LocalDate date) {
        this.ticketId = ticketId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.priceCategory = priceCategory;
        this.ticketType = ticketType;
        this.sectorType = sectorType;
        this.price = price;
        this.status = status;
        this.performanceId = performanceId;
        this.reservationNumber = reservationNumber;
        this.hall = hall;
        this.date = date;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public PriceCategory getPriceCategory() {
        return priceCategory;
    }

    public void setPriceCategory(PriceCategory priceCategory) {
        this.priceCategory = priceCategory;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public SectorType getSectorType() {
        return sectorType;
    }

    public void setSectorType(SectorType sectorType) {
        this.sectorType = sectorType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Long getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(Long reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
