package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ResetPasswordDto {

    private String tokenToResetPassword;
    private String newPassword;
    private String newConfirmedPassword;

    public String getTokenToResetPassword() {
        return tokenToResetPassword;
    }

    public void setTokenToResetPassword(String tokenToResetPassword) {
        this.tokenToResetPassword = tokenToResetPassword;
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
