package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.AuthenticationEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto.UserLoginDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto.UserLogoutDtoBuilder;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

public class AuthenticationEndpointTest {

    @Mock
    private UserService userService;
    @Mock
    private ResetPasswordService resetPasswordService;

    @InjectMocks
    private AuthenticationEndpoint authenticationEndpoint;

    public AuthenticationEndpointTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmailToUser_ReturnsToken() {
        String responseToken = "TestToken 1234567890";
        String userEmail = "user@email.com";

        when(resetPasswordService.sendEmailToResetPassword(userEmail)).thenReturn(
            responseToken);

        String response = authenticationEndpoint.sendEmailToResetPassword(userEmail);

        assertNotNull(response);
        assertEquals(responseToken, response);

        verify(resetPasswordService, times(1)).sendEmailToResetPassword(userEmail);
    }

    @Test
    void sendEmailToUser_ThrowsNotFound() {
        String userEmail = "user@email.com";

        when(resetPasswordService.sendEmailToResetPassword(userEmail)).thenThrow(
            new NotFoundException("User not found"));

        assertThrows(NotFoundException.class,
            () -> authenticationEndpoint.sendEmailToResetPassword(userEmail));

        verify(resetPasswordService, times(1)).sendEmailToResetPassword(userEmail);
    }

    @Test
    void sendEmailToUser_ThrowsBadCredentialsException() {
        String userEmail = "user@email.com";

        when(resetPasswordService.sendEmailToResetPassword(userEmail)).thenThrow(
            new BadCredentialsException("Account is locked"));

        assertThrows(BadCredentialsException.class,
            () -> authenticationEndpoint.sendEmailToResetPassword(userEmail));

        verify(resetPasswordService, times(1)).sendEmailToResetPassword(userEmail);
    }

    @Test
    void login_SuccessfulLogin() throws Exception {
        UserLoginDto userLoginDto = UserLoginDtoBuilder.anUserLoginDto().withEmail("user@email.com")
            .withPassword("password123").build();

        String token = "ValidToken";
        when(userService.login(userLoginDto)).thenReturn(token);

        String response = authenticationEndpoint.login(userLoginDto);

        assertNotNull(response);
        assertEquals(token, response);
        verify(userService, times(1)).login(userLoginDto);
    }

    @Test
    void login_ThrowsException() throws Exception {
        UserLoginDto userLoginDto = UserLoginDtoBuilder.anUserLoginDto().withEmail("user@email.com")
            .withPassword("password123").build();

        when(userService.login(userLoginDto)).thenThrow(new RuntimeException("Login failed"));

        assertThrows(RuntimeException.class, () -> authenticationEndpoint.login(userLoginDto));
        verify(userService, times(1)).login(userLoginDto);
    }

    @Test
    void logout_SuccessfulLogout() {
        UserLogoutDto userLogoutDto = UserLogoutDtoBuilder.anUserLogoutDto()
            .withEmail("user@email.com").withAuthToken("TestToken 1234567890").build();

        doNothing().when(userService).logout(userLogoutDto);

        authenticationEndpoint.logout(userLogoutDto);

        verify(userService, times(1)).logout(userLogoutDto);
    }

    @Test
    void verifyResetCode_SuccessfulVerification() {
        ResetPasswordTokenDto tokenDto = ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder.anResetPasswordTokenDto()
            .withTokenFromStorage("Token 1234567890").withCode("12345678").build();

        doNothing().when(resetPasswordService).verifyResetCode(tokenDto);

        ResponseEntity<Void> response = authenticationEndpoint.verifyResetCode(tokenDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(resetPasswordService, times(1)).verifyResetCode(tokenDto);
    }

    @Test
    void verifyResetCode_ThrowsException() {
        ResetPasswordTokenDto tokenDto = ResetPasswordTokenDto.ResetPasswordTokenDtoBuilder.anResetPasswordTokenDto()
            .withTokenFromStorage("Token 1234567890").withCode("12345678").build();

        doThrow(new RuntimeException("Invalid token"))
            .when(resetPasswordService).verifyResetCode(tokenDto);

        assertThrows(RuntimeException.class,
            () -> authenticationEndpoint.verifyResetCode(tokenDto));
        verify(resetPasswordService, times(1)).verifyResetCode(tokenDto);
    }

    @Test
    void resetPassword_SuccessfulReset() throws Exception {
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.ResetPasswordDtoBuilder.anResetPasswordDto()
            .withTokenToReset("Token 1234567890").withNewPassword("newPassword")
            .withNewConfirmedPassword("newPassword").build();

        doNothing().when(resetPasswordService).resetPassword(resetPasswordDto);

        ResponseEntity<Void> response = authenticationEndpoint.resetPassword(resetPasswordDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(resetPasswordService, times(1)).resetPassword(resetPasswordDto);
    }

    @Test
    void resetPassword_ThrowsValidationException() throws Exception {
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.ResetPasswordDtoBuilder.anResetPasswordDto()
            .withTokenToReset("Invalid Token 1234567890").withNewPassword("newPassword")
            .withNewConfirmedPassword("newPassword").build();
        List<String> errors = new ArrayList<>();

        doThrow(new ValidationException("Invalid reset token", errors))
            .when(resetPasswordService).resetPassword(resetPasswordDto);

        assertThrows(ValidationException.class,
            () -> authenticationEndpoint.resetPassword(resetPasswordDto));
        verify(resetPasswordService, times(1)).resetPassword(resetPasswordDto);
    }
}
