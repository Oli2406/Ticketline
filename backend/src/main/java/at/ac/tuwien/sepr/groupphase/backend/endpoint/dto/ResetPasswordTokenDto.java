package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ResetPasswordTokenDto {

    private String tokenFromStorage;
    private String code;

    public String getTokenFromStorage() {
        return tokenFromStorage;
    }

    public void setTokenFromStorage(String tokenFromStorage) {
        this.tokenFromStorage = tokenFromStorage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ResetPasswordTokenDto(String tokenFromStorage, String code) {
        this.tokenFromStorage = tokenFromStorage;
        this.code = code;
    }

    public ResetPasswordTokenDto() {
    }

    public static final class ResetPasswordTokenDtoBuilder {

        private String tokenFromStorage;
        private String code;

        private ResetPasswordTokenDtoBuilder() {
        }

        public static ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder anResetPasswordTokenDto() {
            return new ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder();
        }

        public ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder withTokenFromStorage(
            String tokenFromStorage) {
            this.tokenFromStorage = tokenFromStorage;
            return this;
        }

        public ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ResetPasswordTokenDto build() {
            ResetPasswordTokenDto resetPasswordTokenDto = new ResetPasswordTokenDto();
            resetPasswordTokenDto.setTokenFromStorage(tokenFromStorage);
            resetPasswordTokenDto.setCode(code);
            return resetPasswordTokenDto;
        }
    }
}
