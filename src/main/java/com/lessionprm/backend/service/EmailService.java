package com.lessionprm.backend.service;

import com.lessionprm.backend.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@lessionprm.com}")
    private String fromEmail;

    public void sendVerificationEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Verify Your Email - LessionPRM");
            message.setText("Dear " + user.getFullName() + ",\n\n" +
                    "Please click the link below to verify your email address:\n" +
                    "http://localhost:3000/verify-email/" + user.getEmailVerificationToken() + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "Best regards,\n" +
                    "LessionPRM Team");

            mailSender.send(message);
            logger.info("Verification email sent to {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
            // In production, you might want to implement retry logic or queue the email
        }
    }

    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Password Reset - LessionPRM");
            message.setText("Dear " + user.getFullName() + ",\n\n" +
                    "You requested a password reset. Please click the link below to reset your password:\n" +
                    "http://localhost:3000/reset-password/" + resetToken + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you did not request this password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "LessionPRM Team");

            mailSender.send(message);
            logger.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to LessionPRM!");
            message.setText("Dear " + user.getFullName() + ",\n\n" +
                    "Welcome to LessionPRM! Your account has been successfully created.\n\n" +
                    "You can now explore our courses and start your learning journey.\n\n" +
                    "Best regards,\n" +
                    "LessionPRM Team");

            mailSender.send(message);
            logger.info("Welcome email sent to {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}