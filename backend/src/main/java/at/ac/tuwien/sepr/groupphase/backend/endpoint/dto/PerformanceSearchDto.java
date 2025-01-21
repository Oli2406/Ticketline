package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PerformanceSearchDto {
    private LocalDateTime date;
    private BigDecimal price;
    private String hall;

    public PerformanceSearchDto(LocalDateTime date, BigDecimal price, String hall) {
        this.date = date;
        this.price = price;
        this.hall = hall;
    }

    public PerformanceSearchDto() {

    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    @Override
    public String toString() {
        return "PerformanceSearchDto{"
            + "date=" + date
            + ", price=" + price
            + ", hall='" + hall + '\''
            + '}';
    }
}
