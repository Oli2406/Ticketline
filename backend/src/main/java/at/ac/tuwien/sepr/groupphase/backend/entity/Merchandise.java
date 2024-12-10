package at.ac.tuwien.sepr.groupphase.backend.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
public class Merchandise {

    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private int stock;
    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    private int points;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchandiseId;




    public Merchandise(String name, String category, BigDecimal price, int stock, String imagePath, int points) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imagePath;
        this.points = points;
    }

    public Merchandise() {
    }

    public Long getMerchandiseId() {
        return merchandiseId;
    }

    public void setMerchandiseId(Long merchandiseId) {
        this.merchandiseId = merchandiseId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Long getId() {
        return merchandiseId;
    }
}
