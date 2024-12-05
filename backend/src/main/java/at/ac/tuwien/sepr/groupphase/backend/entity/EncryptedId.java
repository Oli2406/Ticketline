package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EncryptedId {

    @Id
    private Long id;

    @Column(nullable = false)
    private String encryptedId;

    public EncryptedId(Long id, String encryptedId) {
        this.id = id;
        this.encryptedId = encryptedId;
    }

    public EncryptedId() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedId() {
        return encryptedId;
    }

    public void setEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }
}
