package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservedCreateDto {
    private String userId;
    private List<Long> ticketIds;
    private LocalDateTime reservedDate;

    public ReservedCreateDto(String userId, LocalDateTime reservedDate, List<Long> ticketIds) {
        this.userId = userId;
        this.reservedDate = reservedDate;
        this.ticketIds = ticketIds;
    }

    public ReservedCreateDto() {
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

    public LocalDateTime getReservedDate() {
        return reservedDate;
    }

    public void setPurchaseDate(LocalDateTime reservedDate) {
        this.reservedDate = reservedDate;
    }
}
