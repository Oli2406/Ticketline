package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseCreateDto {

    private String userId;
    private List<Long> ticketIds;
    private List<Long> merchandiseIds;
    private List<Long> merchandiseQuantities;
    private Long totalPrice;
    private LocalDateTime purchaseDate;
    private String street;
    private String postalCode;
    private String city;

    public PurchaseCreateDto(String userId, List<Long> ticketIds, List<Long> merchandiseIds,
        Long totalPrice, LocalDateTime purchaseDate, List<Long> merchandiseQuantities,
        String street, String postalCode, String city) {
        this.userId = userId;
        this.ticketIds = ticketIds;
        this.merchandiseIds = merchandiseIds;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.merchandiseQuantities = merchandiseQuantities;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    public PurchaseCreateDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Long> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(List<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public List<Long> getMerchandiseIds() {
        return merchandiseIds;
    }

    public void setMerchandiseIds(List<Long> merchandiseIds) {
        this.merchandiseIds = merchandiseIds;
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

    public List<Long> getMerchandiseQuantities() {
        return merchandiseQuantities;
    }

    public void setMerchandiseQuantities(List<Long> merchandiseQuantities) {
        this.merchandiseQuantities = merchandiseQuantities;
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
}
