package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String password;
    @Column
    private Boolean admin;

    @Column(nullable = false)
    private int loginAttempts = 0;

    @Column
    private LocalDateTime lastFailedLogin;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private boolean isLoggedIn = false;

    @ElementCollection
    @CollectionTable(name = "user_read_news", joinColumns = @JoinColumn(name = "user_id"))
    @Column
    private List<Long> readNewsIds = new ArrayList<>();

    public ApplicationUser() {
    }

    public ApplicationUser(String firstName, String lastName, String email,
        String password,
        Boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public LocalDateTime getLastFailedLogin() {
        return lastFailedLogin;
    }

    public void setLastFailedLogin(LocalDateTime lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public List<Long> getReadNewsIds() {
        return readNewsIds;
    }

    public void setReadNewsIds(List<Long> readNewsIds) {
        this.readNewsIds = readNewsIds;
    }

    public boolean isAdmin() {
        return admin;
    }
}
