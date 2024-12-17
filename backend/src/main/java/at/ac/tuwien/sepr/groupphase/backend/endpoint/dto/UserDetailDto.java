package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class UserDetailDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isLocked;
    private boolean isLoggedIn;
    private int points;

    private boolean isAdmin;

    public UserDetailDto(Long id, String firstName, String lastName, String email, boolean isLocked,
        boolean isLoggedIn, int points, boolean isAdmin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isLocked = isLocked;
        this.isLoggedIn = isLoggedIn;
        this.points = points;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
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

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
