package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class EventSalesDto {
    private Long eventId;
    private String eventTitle;
    private Long soldTickets;

    public EventSalesDto(Long eventId, String eventTitle, Long soldTickets) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.soldTickets = soldTickets;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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
