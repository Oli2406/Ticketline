package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;

import java.time.LocalDate;
import java.util.List;

public class PurchaseDetailDto {
    private Long userId;
    private List<Ticket> tickets;
    private List<Merchandise> merchandises;
    private List<Long> merchandiseQuantities;
    private Long totalPrice;
    private LocalDate purchaseDate;
    private Long purchaseId;

    public PurchaseDetailDto(Long purchaseId, Long userId, List<Ticket> tickets,
                             List<Merchandise> merchandises, Long totalPrice, LocalDate purchaseDate,
                             List<Long> merchandiseQuantities) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.tickets = tickets;
        this.merchandises = merchandises;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.merchandiseQuantities = merchandiseQuantities;
    }

    public PurchaseDetailDto() {
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<Long> getMerchandiseQuantities() {
        return merchandiseQuantities;
    }

    public void setMerchandiseQuantities(List<Long> merchandiseQuantities) {
        this.merchandiseQuantities = merchandiseQuantities;
    }
}
