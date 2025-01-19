package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReservationOverviewDto {
    private Long reservedId;
    private Long userId;
    private List<Ticket> tickets;
    private LocalDateTime reservedDate;
    private Map<Long, Map<String, String>> performanceDetails;

    public ReservationOverviewDto(Long reservedId, Long userId, List<Ticket> tickets,
                                LocalDateTime reservedDate,
                                Map<Long, Map<String, String>> performanceDetails) {
        this.reservedId = reservedId;
        this.userId = userId;
        this.tickets = tickets;
        this.reservedDate = reservedDate;
        this.performanceDetails = performanceDetails;
    }

    public Long getReservedId() {
        return reservedId;
    }

    public void setReservedId(Long reservedId) {
        this.reservedId = reservedId;
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

    public LocalDateTime getReservedDate() {
        return reservedDate;
    }

    public void setReservedDate(LocalDateTime reservedDate) {
        this.reservedDate = reservedDate;
    }

    public Map<Long, Map<String, String>> getPerformanceDetails() {
        return performanceDetails;
    }

    public void setPerformanceDetails(Map<Long, Map<String, String>> performanceDetails) {
        this.performanceDetails = performanceDetails;
    }
}
