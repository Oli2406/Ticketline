package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ticketIds;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    public Reservation() {
    }

    public Reservation(Long userId, List<Long> ticketIds, LocalDateTime reservationDate) {
        this.userId = userId;
        this.setTicketIds(ticketIds);
        this.reservationDate = reservationDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
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

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
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