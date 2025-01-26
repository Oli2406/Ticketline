package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PurchaseOverviewDto {
    private Long purchaseId;
    private Long userId;
    private List<Ticket> tickets;
    private List<Merchandise> merchandises;
    private Long totalPrice;
    private LocalDateTime purchaseDate;
    private String street;
    private String postalCode;
    private String city;

    private Map<Long, Map<String, String>> performanceDetails;

    public PurchaseOverviewDto(Long purchaseId, Long userId, List<Ticket> tickets, List<Merchandise> merchandises,
                             Long totalPrice, LocalDateTime purchaseDate, String street, String postalCode,
                             String city, Map<Long, Map<String, String>> performanceDetails) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.tickets = tickets;
        this.merchandises = merchandises;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.performanceDetails = performanceDetails;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Merchandise> getMerchandises() {
        return merchandises;
    }

    public void setMerchandises(List<Merchandise> merchandises) {
        this.merchandises = merchandises;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<Long, Map<String, String>> getPerformanceDetails() {
        return performanceDetails;
    }

    public void setPerformanceDetails(Map<Long, Map<String, String>> performanceDetails) {
        this.performanceDetails = performanceDetails;
    }
}
