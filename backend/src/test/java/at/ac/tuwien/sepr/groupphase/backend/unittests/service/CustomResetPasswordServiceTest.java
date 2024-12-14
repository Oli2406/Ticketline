package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomResetPasswordService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.UserValidator;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

class CustomResetPasswordServiceTest {

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private JwtTokenizer jwtTokenizer;

    @Mock
    private SecurityPropertiesConfig.Auth auth;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomResetPasswordService customResetPasswordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmailToResetPassword_Success() {
        String email = "user@example.com";
        ApplicationUser user = new ApplicationUser();
        user.setEmail(email);
        user.setLatestRequestedResetTokenTime(LocalDateTime.now().minusHours(1));
        user.setNumberOfRequestedResetTokens(0);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenizer.getResetToken(email)).thenReturn("ResetToken");

        String response = customResetPasswordService.sendEmailToResetPassword(email);

        assertNotNull(response);
        verify(emailService, times(1)).sendPasswordResetEmail(anyString(), anyString(),
            anyString());
        verify(passwordResetRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void sendEmailToResetPassword_UserNotFound() {
        String email = "user@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> customResetPasswordService.sendEmailToResetPassword(email));
    }

    @Test
    void verifyResetCode_Success() {
        String token = "ValidToken";
        String code = "12345678";
        String userEmail = "user@example.com";
        PasswordResetToken resetPasswordToken = new PasswordResetToken(userEmail, code, token,
            LocalDateTime.now().plusMinutes(15));
        resetPasswordToken.setFailedAttempts(0);

        when(auth.getMaxResetCodeAttempts()).thenReturn(5);
        when(passwordResetRepository.findByEmail(userEmail)).thenReturn(
            Optional.of(resetPasswordToken));

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(userEmail);
        when(jwtTokenizer.getClaims(token)).thenReturn(claims);

        ResetPasswordTokenDto tokenDto = new ResetPasswordTokenDto();
        tokenDto.setTokenFromStorage(token);
        tokenDto.setCode(code);

        customResetPasswordService.verifyResetCode(tokenDto);

        verify(passwordResetRepository, never()).save(any());
    }


    @Test
    void verifyResetCode_InvalidCode() {
        String token = "ValidToken";
        String code = "12345678";
        String userEmail = "user@example.com";
        PasswordResetToken resetPasswordToken = new PasswordResetToken(userEmail, "87654321", token,
            LocalDateTime.now().plusMinutes(15));
        resetPasswordToken.setFailedAttempts(0);

        when(auth.getMaxResetCodeAttempts()).thenReturn(5);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(userEmail);
        when(jwtTokenizer.getClaims(token)).thenReturn(claims);
        when(passwordResetRepository.findByEmail(userEmail)).thenReturn(
            Optional.of(resetPasswordToken));

        ResetPasswordTokenDto tokenDto = new ResetPasswordTokenDto();
        tokenDto.setTokenFromStorage(token);
        tokenDto.setCode(code);

        assertThrows(IllegalArgumentException.class,
            () -> customResetPasswordService.verifyResetCode(tokenDto));
        verify(passwordResetRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    void resetPassword_Success() throws ValidationException {
        String email = "user@example.com";
        String token = "ValidToken";
        String newPassword = "NewPassword123";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setTokenToResetPassword(token);
        resetPasswordDto.setNewPassword(newPassword);
        resetPasswordDto.setNewConfirmedPassword(newPassword);

        ApplicationUser user = new ApplicationUser();
        user.setEmail(email);
        PasswordResetToken resetPasswordToken = new PasswordResetToken(email, "12345678", token,
            LocalDateTime.now().plusMinutes(15));

        when(auth.getMaxResetCodeAttempts()).thenReturn(5);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(email);
        when(jwtTokenizer.getClaims(token)).thenReturn(claims);
        when(passwordResetRepository.findByEmail(email)).thenReturn(
            Optional.of(resetPasswordToken));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(userValidator).validateNewPasswords(newPassword, newPassword);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded" + newPassword);

        customResetPasswordService.resetPassword(resetPasswordDto);

        verify(userRepository, times(1)).save(user);
        verify(passwordResetRepository, times(1)).delete(resetPasswordToken);
    }

    @Test
    void resetPassword_InvalidToken() {
        String token = "InvalidToken";
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setTokenToResetPassword(token);
        resetPasswordDto.setNewPassword("NewPassword123");
        resetPasswordDto.setNewConfirmedPassword("NewPassword123");

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user@example.com");
        when(jwtTokenizer.getClaims(token)).thenReturn(claims);
        when(passwordResetRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> customResetPasswordService.resetPassword(resetPasswordDto));
    }
}
