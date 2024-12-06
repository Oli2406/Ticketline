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
}
