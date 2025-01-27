package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import io.jsonwebtoken.Claims;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordServiceImpl.class);
    private static final String deployUrl = "https://24ws-se-pr-inso-08-acf05sgmk6doonfn65ksq.apps.student.inso-w.at";
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final SecurityPropertiesConfig.Auth auth;

    public ResetPasswordServiceImpl(PasswordResetRepository passwordResetRepository,
        UserRepository userRepository, EmailService emailService, UserValidator userValidator,
        PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
        SecurityPropertiesConfig.Auth auth) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.auth = auth;
    }

    @Override
    public String sendEmailToResetPassword(String email) {
        LOGGER.info("Initiating password reset for email: {}", email);

        ApplicationUser user = validateUser(email);

        String resetCode = generateResetCode();
        String resetToken = jwtTokenizer.getResetToken(email);
        saveResetToken(user.getEmail(), resetCode, resetToken);

        user.setLatestRequestedResetTokenTime(
            LocalDateTime.now().plusMinutes(auth.getResetTokenResendInterval()));
        user.incrementNumberOfRequestedResetTokens();
        userRepository.save(user);

        String resetLink = generateResetLink(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), resetCode, resetLink);

        LOGGER.info("Password reset email sent to: {}", email);
        return resetToken;
    }

    @Override
    public void verifyResetCode(ResetPasswordTokenDto tokenDto) {
        LOGGER.info("Verifying reset code for token: {}", tokenDto);

        PasswordResetToken resetToken = validateAndGetResetToken(tokenDto.getTokenFromStorage());
        validateResetCode(resetToken, tokenDto.getCode());

        LOGGER.info("Reset code verified successfully for email: {}", resetToken.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordDto tokenDto) throws ValidationException {
        LOGGER.info("Resetting password for token: {}", tokenDto);

        PasswordResetToken resetToken = validateAndGetResetToken(
            tokenDto.getTokenToResetPassword());
        ApplicationUser user = userRepository.findUserByEmail(resetToken.getEmail())
            .orElseThrow(() -> new NotFoundException("User not found"));

        userValidator.validateNewPasswords(tokenDto.getNewPassword(),
            tokenDto.getNewConfirmedPassword());

        user.setPassword(passwordEncoder.encode(tokenDto.getNewPassword()));
        user.setIsLoggedIn(false);
        userRepository.save(user);

        passwordResetRepository.delete(resetToken);
        LOGGER.info("Password reset successfully for email: {}", resetToken.getEmail());
    }

    private ApplicationUser validateUser(String email) {
        ApplicationUser user = userRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isLocked()) {
            throw new BadCredentialsException("Account is locked");
        }

        boolean hasExceededMaxResetTokenRequests =
            user.getNumberOfRequestedResetTokens() >= auth.getMaxResetTokenRequests();

        boolean isBeforeResendIntervalEnd = LocalDateTime.now()
            .isBefore(user.getLatestRequestedResetTokenTime());

        boolean isAfterResendIntervalEnd = LocalDateTime.now()
            .isAfter(user.getLatestRequestedResetTokenTime());

        if (hasExceededMaxResetTokenRequests && isBeforeResendIntervalEnd) {
            throw new BadCredentialsException(
                "Too many requests to reset password. Please try again in "
                    + auth.getResetTokenResendInterval() + "h.");
        }

        if (hasExceededMaxResetTokenRequests && isAfterResendIntervalEnd) {
            user.setNumberOfRequestedResetTokens(0);
        }

        return user;
    }

    private String generateResetCode() {
        SecureRandom secureRandom = new SecureRandom();
        int resetCode = secureRandom.nextInt(90000000) + 10000000;
        return String.valueOf(resetCode);
    }

    private void saveResetToken(String email, String resetCode, String resetToken) {
        PasswordResetToken token = passwordResetRepository.findByEmail(email).orElse(
            new PasswordResetToken(email, resetCode, resetToken,
                LocalDateTime.now().plusMinutes(auth.getExpirationTimeResetToken())));

        token.setCode(resetCode);
        token.setToken(resetToken);
        token.setExpirationTime(
            LocalDateTime.now().plusMinutes(auth.getExpirationTimeResetToken()));
        token.setFailedAttempts(0);

        passwordResetRepository.save(token);
    }

    private String generateResetLink(String resetToken) {
        return deployUrl + "/?reset-password=true&token=" + resetToken;
    }

    private PasswordResetToken validateAndGetResetToken(String token) {
        Claims claims = jwtTokenizer.getClaims(token);
        String email = claims.getSubject();

        PasswordResetToken resetToken = passwordResetRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Reset token not found"));

        if (resetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        if (resetToken.getFailedAttempts() >= auth.getMaxResetCodeAttempts()) {
            throw new BadCredentialsException(
                "Too many failed attempts. Please request a new reset code.");
        }

        return resetToken;
    }

    private void validateResetCode(PasswordResetToken resetToken, String code) {
        if (!resetToken.getCode().equals(code)) {
            resetToken.setFailedAttempts(resetToken.getFailedAttempts() + 1);
            passwordResetRepository.save(resetToken);
            throw new IllegalArgumentException("Invalid reset code");
        }
    }
}
