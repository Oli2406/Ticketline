package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class ResetPasswordTokenDto {

    private String email;
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ResetPasswordTokenDto(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public ResetPasswordTokenDto() {
    }
}
