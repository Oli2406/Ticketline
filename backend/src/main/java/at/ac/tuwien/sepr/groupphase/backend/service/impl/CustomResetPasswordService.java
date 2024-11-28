package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class CustomResetPasswordService implements ResetPasswordService {

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
        ApplicationUser user = userRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));

        String resetCode = generateResetCode();
        saveResetToken(user.getEmail(), resetCode);

        emailService.sendPasswordResetEmail(user.getEmail(), resetCode);
    }

    private String generateResetCode() {
        return String.valueOf((int) (Math.random() * 90000000) + 10000000);
    }

    private void saveResetToken(String email, String resetCode) {
        PasswordResetToken token = passwordResetRepository.findByEmail(email)
            .orElse(new PasswordResetToken(email, resetCode, LocalDateTime.now().plusMinutes(15)));

        token.setCode(resetCode);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(15));

        passwordResetRepository.save(token);
    }

}
