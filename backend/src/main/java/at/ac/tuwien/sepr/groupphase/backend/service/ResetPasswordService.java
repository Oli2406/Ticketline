package at.ac.tuwien.sepr.groupphase.backend.service;

public interface ResetPasswordService {

    /**
     * Resets the password for the given email.
     *
     * @param email The email address of the user.
     */
    void resetPassword(String email);
}
