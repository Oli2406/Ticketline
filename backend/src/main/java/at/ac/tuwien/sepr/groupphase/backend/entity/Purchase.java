package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Purchase {

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
    private LocalDate purchaseDate;

    public Purchase() {
    }

    public Purchase(Long userId, List<Long> ticketIds, List<Long> merchandiseIds, Long totalPrice, LocalDate purchaseDate,
                    List<Long> merchandiseQuantities) {
        this.userId = userId;
        this.setTicketIds(ticketIds);
        this.setMerchandiseIds(merchandiseIds);
        this.setMerchandiseQuantities(merchandiseQuantities);
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    private void setMerchandiseQuantities(List<Long> merchandiseQuantities) {
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
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