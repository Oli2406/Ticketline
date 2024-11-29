package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
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

    public CustomResetPasswordService(PasswordResetRepository passwordResetRepository,
        UserRepository userRepository,
        EmailService emailService) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void resetPassword(String email) {
        LOGGER.info("Initiating password reset for email: {}", email);

        ApplicationUser user = userRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));

        String resetCode = generateResetCode();
        String resetToken = generateResetToken();
        saveResetToken(user.getEmail(), resetCode, resetToken);

        String resetLink = generateResetLink(resetToken);
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetCode, resetLink);
            LOGGER.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            LOGGER.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
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
        token.setExpirationTime(LocalDateTime.now().plusMinutes(15));

        passwordResetRepository.save(token);
    }

    private String generateResetLink(String resetToken) {
        return "http://localhost:4200/reset-password?token=" + resetToken;
    }
}
