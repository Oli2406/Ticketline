package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthenticationEndpoint.BASE_PATH)
public class AuthenticationEndpoint {

    public static final String BASE_PATH = "/api/v1/authentication";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;

    public AuthenticationEndpoint(UserService userService,
        ResetPasswordService resetPasswordService) {
        this.userService = userService;
        this.resetPasswordService = resetPasswordService;
    }

    @PermitAll
    @PostMapping
    public String login(@RequestBody UserLoginDto userLoginDto) throws NoSuchAlgorithmException {
        LOGGER.trace("POST " + BASE_PATH + "{}", userLoginDto);
        return userService.login(userLoginDto);
    }

    @PermitAll
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void logout(@RequestBody UserLogoutDto userLogoutDto) {
        LOGGER.trace("DELETE " + BASE_PATH + "{}", userLogoutDto);
        userService.logout(userLogoutDto);
    }

    @PermitAll
    @PostMapping("/get-login-status")
    public ResponseEntity<Boolean> isUserLoggedIn(@RequestBody UserLogoutDto userLogoutDto) {
        LOGGER.trace("GET " + BASE_PATH + "/get-login-status {}", userLogoutDto);
        return ResponseEntity.ok(userService.isUserLoggedIn(userLogoutDto));
    }

    @PermitAll
    @PostMapping("/send-email")
    public String sendEmailToResetPassword(@RequestBody String email) {
        LOGGER.trace("POST " + BASE_PATH + "/send-email {}", email);
        return resetPasswordService.sendEmailToResetPassword(email);
    }

    @PermitAll
    @PostMapping("/verify-reset-code")
    public ResponseEntity<Void> verifyResetCode(@RequestBody ResetPasswordTokenDto token) {
        LOGGER.trace("POST " + BASE_PATH + "/verify-reset-code {}", token);
        resetPasswordService.verifyResetCode(token);
        return ResponseEntity.ok().build();
    }

    @PermitAll
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDto token)
        throws ValidationException {
        LOGGER.trace("POST " + BASE_PATH + "/verify-reset-code {}", token);
        resetPasswordService.resetPassword(token);
        return ResponseEntity.ok().build();
    }
}
