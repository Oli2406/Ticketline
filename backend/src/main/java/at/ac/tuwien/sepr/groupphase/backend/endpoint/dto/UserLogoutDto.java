package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto.UserLoginDtoBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class UserLogoutDto {

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    private String authToken;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public static final class UserLogoutDtoBuilder {

        private String email;
        private String authToken;

        private UserLogoutDtoBuilder() {
        }

        public static UserLogoutDto.UserLogoutDtoBuilder anUserLogoutDto() {
            return new UserLogoutDtoBuilder();
        }

        public UserLogoutDto.UserLogoutDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserLogoutDto.UserLogoutDtoBuilder withAuthToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        public UserLogoutDto build() {
            UserLogoutDto userLogoutDto = new UserLogoutDto();
            userLogoutDto.setEmail(email);
            userLogoutDto.setAuthToken(authToken);
            return userLogoutDto;
        }
    }
}
