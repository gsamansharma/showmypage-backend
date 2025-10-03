package page.showmy.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.fromEmail}")
    private String fromEmail;

    @Value("${spring.mail.fromName}")
    private String fromName;

    @Value("${frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String plainText, String htmlContent) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(plainText, htmlContent);
            mailSender.send(message);
            System.out.println("Email sent");
        } catch (MessagingException e) {
            System.err.println("Error sending email to "+ toEmail + ": " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(String userEmail, String token) {
        String verificationLink = String.format("%s/verify-account?token=%s", frontendUrl, token);
        String subject = "Verify Your ShowMyPage Account";
        String html = String.format("""
        <!DOCTYPE html>
                <html>
                    <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f7f7f7;">
                        <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%">
                            <tr>
                                <td align="center" style="padding:40px 0;">
                                    <table border="0" cellpadding="20" cellspacing="0" width="600" style="background:#ffffff; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.05);">
                                        <tr>
                                            <td style="text-align:center; border-bottom:1px solid #eee;">
                                                <h1 style="color:#2c3e50; margin:0;">ShowMyPage 🎉</h1>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="padding:30px; color:#333;">
                                                <p style="font-size:16px;">Hello,</p>
                                                <p style="font-size:16px;">Thank you for registering with <b>ShowMyPage</b>!</p>
                                                <p style="font-size:16px;">Please confirm your email address by clicking the button below:</p>
                                                <div style="text-align:center; margin:30px 0;">
                                                    <a href="%s" style="background:#4CAF50; color:#fff; padding:14px 24px; border-radius:6px; text-decoration:none; font-size:16px; font-weight:bold;">
                                                        ✅ Verify My Email
                                                    </a>
                                                </div>
                                                <p style="font-size:14px; color:#555;">
                                                    <b>Note:</b> This link will expire in <b>1 hour</b>. If you did not sign up, you can safely ignore this email.
                                                </p>
                                                <p style="font-size:14px; color:#555;">
                                                    Best regards,<br>
                                                    The ShowMyPage Team
                                                </p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="background:#f0f0f0; text-align:center; font-size:12px; color:#888; border-top:1px solid #eee; padding:15px;">
                                                © 2025 ShowMyPage. All rights reserved.
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </body>
                </html>
        """, verificationLink);
        String plain = "Thanks for registering with ShowMyPage!\n\n"
                + "Open this link to verify your account:\n" + verificationLink
                + "\n\nThis link expires in 1 hour.";

        sendEmail(userEmail, subject, plain, html);
    }


    public void sendPasswordResetEmail(String userEmail, String token) {
        String resetLink = String.format("%s/reset-password?token=%s", frontendUrl, token);
        String subject = "Password Reset Request";
        String html = String.format("""
            <!DOCTYPE html>
                <html>
                    <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f7f7f7;">
                        <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%">
                            <tr>
                                <td align="center" style="padding:40px 0;">
                                    <table border="0" cellpadding="20" cellspacing="0" width="600" style="background:#ffffff; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.05);">
                                        <tr>
                                            <td style="text-align:center; border-bottom:1px solid #eee;">
                                                <h1 style="color:#2c3e50; margin:0;">Password Reset 🔑</h1>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="padding:30px; color:#333;">
                                                <p style="font-size:16px;">Hello,</p>
                                                <p style="font-size:16px;">You requested a password reset for your <b>ShowMyPage</b> account.</p>
                                                <p style="font-size:16px;">Please click the button below to set a new password:</p>
                                                <div style="text-align:center; margin:30px 0;">
                                                    <a href="%s" style="background:#FF4C4C; color:#fff; padding:14px 24px; border-radius:6px; text-decoration:none; font-size:16px; font-weight:bold;" target="_blank">
                                                        🔐 Reset My Password
                                                    </a>
                                                </div>
                                                <p style="font-size:14px; color:#555;">
                                                    <b>Note:</b> This link will expire in <b>1 hour</b>. If you did not request this, you can safely ignore this email.
                                                </p>
                                                <p style="font-size:14px; color:#555;">
                                                    Best regards,<br>
                                                    The ShowMyPage Team
                                                </p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="background:#f0f0f0; text-align:center; font-size:12px; color:#888; border-top:1px solid #eee; padding:15px;">
                                                © 2025 ShowMyPage. All rights reserved.
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </body>
                </html>
            """, resetLink);

        String plain = "You requested a password reset for your ShowMyPage account.\n\n"
                +"Please click the link below to set a new password:\n"+ resetLink
                +"\n\nThis link will expire in 1 hour.";
        sendEmail(userEmail, subject, plain, html);
    }

}
