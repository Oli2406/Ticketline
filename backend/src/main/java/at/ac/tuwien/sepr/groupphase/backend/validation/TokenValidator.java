package at.ac.tuwien.sepr.groupphase.backend.validation;

import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TokenValidator {

    private final JwtTokenizer jwtTokenizer;
    private final PasswordResetRepository passwordResetRepository;

    public TokenValidator(JwtTokenizer jwtTokenizer,
        PasswordResetRepository passwordResetRepository) {
        this.jwtTokenizer = jwtTokenizer;
        this.passwordResetRepository = passwordResetRepository;
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
    public PasswordResetToken validateAndGetResetToken(String token) {
        Claims claims = jwtTokenizer.getClaims(token);
        String email = claims.getSubject();

        PasswordResetToken resetToken = passwordResetRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Reset token not found"));

        if (resetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
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
    public void validateResetCode(PasswordResetToken resetToken, String code) {
        if (!resetToken.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid reset code");
        }
    }
}
