package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String senderEmail = "ticketline.inso8@gmail.com";

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetCode, String resetLink) {
        String subject = "Password Reset Request";
        String message = String.format(
            "Hello,\n\nYou requested a password reset. Here is your reset code: %s\n\n"
                + "Alternatively, click the link below to reset your password:\n%s\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "The Code expires in five Minutes! The reset will not work when the token expired. \n\n"
                + "Regards,\nYour Team",
            resetCode, resetLink);

        sendEmail(email, subject, message);
    }


    @Override
    public void sendHtmlEmail(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true enables HTML content
            helper.setFrom(senderEmail);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom(senderEmail);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
