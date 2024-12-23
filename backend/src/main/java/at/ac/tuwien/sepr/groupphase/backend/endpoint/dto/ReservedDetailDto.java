package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;

public class ReservedDetailDto {
    private Long userId;
    private List<Ticket> tickets;
    private LocalDateTime reservedDate;
    private Long reservedId;

    public ReservedDetailDto(Long userId, LocalDateTime reservedDate, List<Ticket> tickets, Long reservedId) {
        this.userId = userId;
        this.reservedDate = reservedDate;
        this.tickets = tickets;
        this.reservedId = reservedId;
    }

    public ReservedDetailDto() {
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

    public void setPurchaseDate(LocalDateTime reservedDate) {
        this.reservedDate = reservedDate;
    }

    public Long getReservedId() {
        return reservedId;
    }

    public void setReservedId(Long reservedId) {
        this.reservedId = reservedId;
    }
}
