package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.enums.Hall;
import at.ac.tuwien.sepr.groupphase.backend.enums.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepr.groupphase.backend.enums.TicketType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(nullable = false)
    private Long performanceId;

    @Column
    private Integer rowNumber;

    @Column
    private Integer seatNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceCategory priceCategory;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SectorType sectorType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String status; // e.g., "AVAILABLE", "RESERVED", "SOLD"

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Hall hall;

    @Column
    private Long reservationNumber;

    @Column(nullable = false)
    private LocalDateTime date;

    public Ticket() {
    }

    public Ticket(Long performanceId, Integer rowNumber, Integer seatNumber,
        PriceCategory priceCategory,
        TicketType ticketType, SectorType sectorType, BigDecimal price, String status, Hall hall,
        Long reservationNumber, LocalDateTime date) {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
