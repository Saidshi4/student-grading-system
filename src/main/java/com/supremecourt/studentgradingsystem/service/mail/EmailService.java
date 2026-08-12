package com.supremecourt.studentgradingsystem.service.mail;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend.reset-password-url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    public void sendOtp(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("Student Grading System OTP");
        String body = "Your OTP code is: " + otp + "\nIt expires in 180 seconds.";
        helper.setText(body, false);
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String to, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String fullResetLink = resetPasswordBaseUrl + "?code=" + resetLink;
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("Student Grading System Password Reset");
        String body = "Click the following link to reset your password: " + fullResetLink + "\nThis link will expire in 3 minutes.";
        helper.setText(body, false);
        mailSender.send(message);
    }

    public void testConnection() throws MessagingException {
        mailSender.createMimeMessage();
    }

    public void sendPasswordChangedNotification(String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Student Grading System Password Changed");
            String body = "Your password has been successfully changed. If you did not perform this action, please contact support immediately.";
            helper.setText(body, false);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send password change notification: " + e.getMessage());
        }
    }
}
