package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomResetPasswordService implements ResetPasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomResetPasswordService.class);

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtTokenizer jwtTokenizer;

    public CustomResetPasswordService(PasswordResetRepository passwordResetRepository,
        UserRepository userRepository,
        EmailService emailService,
        JwtTokenizer jwtTokenizer) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    public String sendEmailToResetPassword(String email) {
        LOGGER.info("Initiating password reset for email: {}", email);

        ApplicationUser user = userRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));

        String resetCode = generateResetCode();
        String resetToken = jwtTokenizer.getResetToken(email);

        try {
            saveResetToken(user.getEmail(), resetCode, resetToken);
        } catch (Exception e) {
            LOGGER.error("Failed to save reset token for email: {}", email, e);
            throw new RuntimeException("Failed to save reset token", e);
        }

        String resetLink = generateResetLink(resetToken);
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetCode, resetLink);
            LOGGER.info("Password reset email sent to: {}", email);
            return resetToken;
        } catch (Exception e) {
            LOGGER.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void verifyResetCode(ResetPasswordTokenDto token) {
        LOGGER.info("Verify password reset code for email: {}", token.getEmail());

        PasswordResetToken passwordResetToken = passwordResetRepository.findByEmail(
                token.getEmail())
            .orElseThrow(() -> new NotFoundException("Reset token not found for user"));

        if (passwordResetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            LOGGER.error("Reset token expired for email: {}", token.getEmail());
            throw new IllegalArgumentException("Reset token has expired");
        }

        if (!passwordResetToken.getCode().equals(token.getCode())) {
            LOGGER.error("Reset token mismatch for email: {}", token.getEmail());
            throw new IllegalArgumentException("Reset code is invalid");
        }

        passwordResetRepository.delete(passwordResetToken);
        LOGGER.info("Reset token verified and deleted for email: {}", token.getEmail());
    }


    private String generateResetCode() {
        return String.valueOf((int) (Math.random() * 90000000) + 10000000);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void saveResetToken(String email, String resetCode, String resetToken) {
        PasswordResetToken token = passwordResetRepository.findByEmail(email)
            .orElse(new PasswordResetToken(email, resetCode, resetToken,
                LocalDateTime.now().plusMinutes(15)));

        token.setCode(resetCode);
        token.setToken(resetToken);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(2));

        passwordResetRepository.save(token);
    }

    private String generateResetLink(String resetToken) {
        return "http://localhost:4200/reset-password?token=" + resetToken;
    }

}
