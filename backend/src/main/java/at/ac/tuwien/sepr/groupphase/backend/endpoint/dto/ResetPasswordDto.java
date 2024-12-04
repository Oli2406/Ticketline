package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ResetPasswordDto {

    private String email;
    private String newPassword;
    private String newConfirmedPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewConfirmedPassword() {
        return newConfirmedPassword;
    }

    public void setNewConfirmedPassword(String newConfirmedPassword) {
        this.newConfirmedPassword = newConfirmedPassword;
    }

}
