package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.List;

public class PurchaseCreateDto {
    private Long userId;
    private List<Long> ticketIds;
    private List<Long> merchandiseIds;
    private Long totalPrice;
    private LocalDate purchaseDate;

    public PurchaseCreateDto(Long userId, List<Long> ticketIds, List<Long> merchandiseIds, Long totalPrice, LocalDate purchaseDate) {
        this.userId = userId;
        this.ticketIds = ticketIds;
        this.merchandiseIds = merchandiseIds;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    public PurchaseCreateDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
