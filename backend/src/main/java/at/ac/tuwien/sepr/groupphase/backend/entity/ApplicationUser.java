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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean admin;

    @Column(nullable = false)
    private int loginAttempts = 0;

    @Column
    private LocalDateTime lastFailedLogin;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private boolean isLoggedIn = false;

    @Column(nullable = false)
    private int points = 0;

    @ElementCollection
    @CollectionTable(name = "user_read_news", joinColumns = @JoinColumn(name = "user_id"))
    @Column
    private List<Long> readNewsIds = new ArrayList<>();
    @Column(nullable = false)
    private int numberOfRequestedResetTokens;
    @Column(nullable = false)
    private LocalDateTime latestRequestedResetTokenTime;

    @Column(nullable = false)
    private Integer version = 0;

    public ApplicationUser() {
        this.latestRequestedResetTokenTime = LocalDateTime.MIN;
    }

    public ApplicationUser(String firstName,
        String lastName,
        String email,
        String password,
        Boolean admin,
        int points) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.points = points;
        this.latestRequestedResetTokenTime = LocalDateTime.MIN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void incrementPoints(int points) {
        points += this.points;
    }

    public int getNumberOfRequestedResetTokens() {
        return numberOfRequestedResetTokens;
    }

    public void setNumberOfRequestedResetTokens(int requestedResetTokens) {
        this.numberOfRequestedResetTokens = requestedResetTokens;
    }

    public void incrementNumberOfRequestedResetTokens() {
        this.numberOfRequestedResetTokens++;
    }

    public LocalDateTime getLatestRequestedResetTokenTime() {
        return latestRequestedResetTokenTime;
    }

    public void setLatestRequestedResetTokenTime(LocalDateTime latestRequestedResetTokenTime) {
        this.latestRequestedResetTokenTime = latestRequestedResetTokenTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void incrementVersion() {
        this.version++;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
