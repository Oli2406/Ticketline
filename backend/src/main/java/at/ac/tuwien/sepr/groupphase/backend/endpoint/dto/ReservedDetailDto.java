package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservedDetailDto {
    private Long userId;
    private List<Long> ticketIds;
    private LocalDateTime reservedDate;
    private Long reservedId;

    public ReservedDetailDto(Long userId, LocalDateTime reservedDate, List<Long> ticketIds, Long reservedId) {
        this.userId = userId;
        this.reservedDate = reservedDate;
        this.ticketIds = ticketIds;
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

    public Long getReservedId() {
        return reservedId;
    }

    public void setReservedId(Long reservedId) {
        this.reservedId = reservedId;
    }
}
