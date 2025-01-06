package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class EventSalesDto {
    private Long eventId;
    private Long soldTickets;
    private Long totalTickets;
    private Double soldPercentage;

    public EventSalesDto(Long eventId, Long soldTickets, Long totalTickets, Double soldPercentage) {
        this.eventId = eventId;
        this.soldTickets = soldTickets;
        this.totalTickets = totalTickets;
        this.soldPercentage = soldPercentage;
    }

    public Double getSoldPercentage() {
        return soldPercentage;
    }

    public void setSoldPercentage(Double soldPercentage) {
        this.soldPercentage = soldPercentage;
    }

    public Long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Long getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Long soldTickets) {
        this.soldTickets = soldTickets;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
