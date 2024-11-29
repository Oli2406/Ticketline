package at.ac.tuwien.sepr.groupphase.backend.service;

public interface EmailService {

    /**
     * Sends an email with HTML content.
     *
     * @param to      Recipient's email address
     * @param subject Email subject
     * @param html    Email content as HTML
     */
    void sendHtmlEmail(String to, String subject, String html);

    /**
     * Sends an email with HTML content.
     *
     * @param to        Recipient's email address
     * @param resetCode Code to reset password
     */
    void sendPasswordResetEmail(String to, String resetCode, String resetLink);

}
