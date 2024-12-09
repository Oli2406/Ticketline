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
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomResetPasswordService implements ResetPasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomResetPasswordService.class);

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final SecurityPropertiesConfig.Auth auth;

    public CustomResetPasswordService(PasswordResetRepository passwordResetRepository,
        UserRepository userRepository,
        EmailService emailService,
        UserValidator userValidator,
        PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
        SecurityPropertiesConfig.Auth auth, SecurityPropertiesConfig.Auth auth1) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.auth = auth1;
    }

    @Override
    public String sendEmailToResetPassword(String email) {
        LOGGER.info("Initiating password reset for email: {}", email);

        ApplicationUser user = userRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isLocked()) {
            throw new BadCredentialsException(
                "Account is locked");
        }

        String resetCode = generateResetCode();
        String resetToken = jwtTokenizer.getResetToken(email);
        saveResetToken(user.getEmail(), resetCode, resetToken);

        String resetLink = generateResetLink(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), resetCode, resetLink);

        LOGGER.info("Password reset email sent to: {}", email);
        return resetToken;
    }

    @Override
    public void verifyResetCode(ResetPasswordTokenDto tokenDto) {
        LOGGER.info("Verifying reset code for token: {}", tokenDto);

        PasswordResetToken resetToken = validateAndGetResetToken(
            tokenDto.getTokenFromStorage());
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
        userRepository.save(user);

        passwordResetRepository.delete(resetToken);
        LOGGER.info("Password reset successfully for email: {}", resetToken.getEmail());
    }


    private String generateResetCode() {
        return String.valueOf((int) (Math.random() * 90000000) + 10000000);
    }

    private void saveResetToken(String email, String resetCode, String resetToken) {
        PasswordResetToken token = passwordResetRepository.findByEmail(email)
            .orElse(new PasswordResetToken(email, resetCode, resetToken,
                LocalDateTime.now().plusMinutes(5)));

        token.setCode(resetCode);
        token.setToken(resetToken);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        token.setFailedAttempts(0);

        passwordResetRepository.save(token);
    }

    private String generateResetLink(String resetToken) {
        return "http://localhost:4200/reset-password?token=" + resetToken;
    }

    /**
     * Validates the token and retrieves the associated PasswordResetToken.
     *
     * @param token JWT token to validate
     * @return PasswordResetToken associated with the provided JWT token
     * @throws IllegalArgumentException if the token is expired or invalid
     * @throws NotFoundException        if no PasswordResetToken is found for the email in the
     *                                  token
     */
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

    /**
     * Validates the reset code against the associated PasswordResetToken.
     *
     * @param resetToken PasswordResetToken entity to validate
     * @param code       Reset code provided by the user
     * @throws IllegalArgumentException if the code is invalid
     */
    private void validateResetCode(PasswordResetToken resetToken, String code) {
        if (!resetToken.getCode().equals(code)) {
            resetToken.setFailedAttempts(resetToken.getFailedAttempts() + 1);
            passwordResetRepository.save(resetToken);
            throw new IllegalArgumentException("Invalid reset code");
        }
    }
}
