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

    public static final class ResetPasswordDtoBuilder {

        private String tokenToReset;
        private String newPassword;
        private String newConfirmedPassword;

        private ResetPasswordDtoBuilder() {
        }

        public static ResetPasswordDto.ResetPasswordDtoBuilder anResetPasswordDto() {
            return new ResetPasswordDto.ResetPasswordDtoBuilder();
        }

        public ResetPasswordDto.ResetPasswordDtoBuilder withTokenToReset(
            String tokenToReset) {
            this.tokenToReset = tokenToReset;
            return this;
        }

        public ResetPasswordDto.ResetPasswordDtoBuilder withNewPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ResetPasswordDto.ResetPasswordDtoBuilder withNewConfirmedPassword(
            String newConfirmedPassword) {
            this.newConfirmedPassword = newConfirmedPassword;
            return this;
        }

        public ResetPasswordDto build() {
            ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
            resetPasswordDto.setTokenToResetPassword(tokenToReset);
            resetPasswordDto.setNewPassword(newPassword);
            resetPasswordDto.setNewConfirmedPassword(newConfirmedPassword);
            return resetPasswordDto;
        }
    }

}
