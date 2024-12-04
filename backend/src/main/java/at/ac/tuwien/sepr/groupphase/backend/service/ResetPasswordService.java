package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordTokenDto;

public interface ResetPasswordService {

    /**
     * Resets the password for the given email.
     *
     * @param email The email address of the user.
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials
     *                                                                             are invalid
     */
    String sendEmailToResetPassword(String email);

    /**
     * Verifies the code sent from frontend.
     *
     * @param token as given Dto with email and code
     */
    void verifyResetCode(ResetPasswordTokenDto token);
}
