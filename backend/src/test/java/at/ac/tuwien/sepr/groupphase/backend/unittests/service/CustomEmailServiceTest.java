package at.ac.tuwien.sepr.groupphase.backend.unittests.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

class CustomEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private CustomEmailService customEmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendPasswordResetEmail_Success() {
        String email = "user@example.com";
        String resetCode = "12345678";
        String resetLink = "http://example.com/reset";

        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        customEmailService.sendPasswordResetEmail(email, resetCode, resetLink);

        verify(mailSender, times(1)).send(message);
    }

    @Test
    void sendPasswordResetEmail_Failure() {
        String email = "user@example.com";
        String resetCode = "12345678";
        String resetLink = "http://example.com/reset";

        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        doAnswer(invocation -> {
            throw new MessagingException("Failed to send");
        }).when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customEmailService.sendPasswordResetEmail(email, resetCode, resetLink);
        });

        assertEquals("Failed to send email", exception.getMessage());
    }

    @Test
    void sendHtmlEmail_Success() {
        String email = "user@example.com";
        String subject = "Test Subject";
        String htmlContent = "<h1>Test HTML</h1>";

        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        customEmailService.sendHtmlEmail(email, subject, htmlContent);

        verify(mailSender, times(1)).send(message);
    }

    @Test
    void sendHtmlEmail_Failure() {
        String email = "user@example.com";
        String subject = "Test Subject";
        String htmlContent = "<h1>Test HTML</h1>";

        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        doAnswer(invocation -> {
            throw new MessagingException("Failed to send");
        }).when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customEmailService.sendHtmlEmail(email, subject, htmlContent);
        });

        assertEquals("Failed to send email", exception.getMessage());
    }
}
