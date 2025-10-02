package page.showmy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String content) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            System.out.println("Email sent");
        } catch (MailException e) {
            System.err.println("Error sending email to "+ toEmail + ": " + e.getMessage());
        }
    }

    public void sendVerificationEmail(String userEmail, String token) {
        String verificationLink = String.format("%s/verify-account?token=%s", frontendUrl, token);
        String subject = "Verify Your ShowMyPage Account";
        String body = String.format("""
                Hello there,

                Thank you for registering with ShowMyPage!

                Please click the link below to verify your email address and activate your account:
                
                %s

                This link will expire in 1 hour. If you did not sign up for this service, please ignore this email.

                The ShowMyPage Team
                """, verificationLink);

        sendEmail(userEmail, subject, body);
    }

    public void sendPasswordResetEmail(String userEmail, String token) {
        String resetLink = String.format("%s/reset-password?token=%s", frontendUrl, token);
        String subject = "Password Reset Request";
        String body = String.format("""
                Hello,

                You requested a password reset for your ShowMyPage account.

                Please click the link below to set a new password:
                
                %s

                This link will expire in 1 hour. If you did not request a password reset, please ignore this email.

                The ShowMyPage Team
                """, resetLink);

        sendEmail(userEmail, subject, body);
    }

}
