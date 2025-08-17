package com.bika.email.service;

import com.bika.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.from-name}")
    private String fromName;

    @Value("${email.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFirstName() + " " + user.getLastName());
            context.setVariable("resetLink", frontendUrl + "/reset-password?token=" + resetToken);
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("password-reset", context);

            sendHtmlEmail(
                user.getEmail(),
                "Password Reset Request - Bika Document Management",
                htmlContent
            );

            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendWelcomeEmail(User user, String temporaryPassword) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFirstName() + " " + user.getLastName());
            context.setVariable("email", user.getEmail());
            context.setVariable("temporaryPassword", temporaryPassword);
            context.setVariable("loginLink", frontendUrl + "/login");
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("role", getRoleDisplayName(user.getRole()));

            String htmlContent = templateEngine.process("welcome-email", context);

            sendHtmlEmail(
                user.getEmail(),
                "Welcome to Bika Document Management System",
                htmlContent
            );

            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    public void sendAccountActivationEmail(User user) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFirstName() + " " + user.getLastName());
            context.setVariable("email", user.getEmail());
            context.setVariable("loginLink", frontendUrl + "/login");
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("role", getRoleDisplayName(user.getRole()));

            String htmlContent = templateEngine.process("account-activation", context);

            sendHtmlEmail(
                user.getEmail(),
                "Account Activated - Bika Document Management",
                htmlContent
            );

            log.info("Account activation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send account activation email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send account activation email", e);
        }
    }

    public void sendPasswordChangedNotification(User user) {
        try {
            Context context = new Context();
            context.setVariable("userName", user.getFirstName() + " " + user.getLastName());
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("password-changed", context);

            sendHtmlEmail(
                user.getEmail(),
                "Password Changed Successfully - Bika Document Management",
                htmlContent
            );

            log.info("Password changed notification sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password changed notification to: {}", user.getEmail(), e);
            // Don't throw exception for notification emails
        }
    }

    public void sendCustomEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariable("frontendUrl", frontendUrl);
            variables.forEach(context::setVariable);

            String htmlContent = templateEngine.process(templateName, context);

            sendHtmlEmail(to, subject, htmlContent);

            log.info("Custom email sent to: {} with template: {}", to, templateName);
        } catch (Exception e) {
            log.error("Failed to send custom email to: {} with template: {}", to, templateName, e);
            throw new RuntimeException("Failed to send custom email", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (java.io.UnsupportedEncodingException e) {
            // Fallback to email without name if encoding fails
            helper.setFrom(fromEmail);
        }
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String getRoleDisplayName(com.bika.user.entity.UserRole role) {
        switch (role) {
            case SUPER_ADMIN:
                return "Super Administrator";
            case COMPANY_ADMIN:
                return "Company Administrator";
            case MANAGER:
                return "Manager";
            case USER:
                return "User";
            default:
                return "User";
        }
    }
} 