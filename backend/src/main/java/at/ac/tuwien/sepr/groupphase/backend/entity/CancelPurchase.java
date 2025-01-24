package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class CancelPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ticketIds;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String merchandiseIds;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String merchandiseQuantities;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    public CancelPurchase() {
    }

    public CancelPurchase(Long purchaseId, Long userId, List<Long> ticketIds,
        List<Long> merchandiseIds, List<Long> merchandiseQuantities, Long totalPrice,
        LocalDateTime purchaseDate, String street, String postalCode, String city) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.setTicketIds(ticketIds);
        this.setMerchandiseIds(merchandiseIds);
        this.setMerchandiseQuantities(merchandiseQuantities);
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    public void setMerchandiseQuantities(List<Long> merchandiseQuantities) {
        this.merchandiseQuantities = convertListToCsv(merchandiseQuantities);
    }

    public List<Long> getMerchandiseQuantities() {
        return convertCsvToList(this.merchandiseQuantities);
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

    public List<Long> getTicketIds() {
        return convertCsvToList(this.ticketIds);
    }

    public void setTicketIds(List<Long> ticketIds) {
        this.ticketIds = convertListToCsv(ticketIds);
    }

    public List<Long> getMerchandiseIds() {
        return convertCsvToList(this.merchandiseIds);
    }

    public void setMerchandiseIds(List<Long> merchandiseIds) {
        this.merchandiseIds = convertListToCsv(merchandiseIds);
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    private static String convertListToCsv(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        return ids.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    }

    private static List<Long> convertCsvToList(String csv) {
        if (csv == null || csv.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }
}
