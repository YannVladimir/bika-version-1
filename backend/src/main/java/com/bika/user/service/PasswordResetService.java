package com.bika.user.service;

import com.bika.email.service.EmailService;
import com.bika.user.entity.PasswordResetToken;
import com.bika.user.entity.User;
import com.bika.user.repository.PasswordResetTokenRepository;
import com.bika.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${email.reset-password-expiry}")
    private long tokenExpiryMinutes;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Async
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Check if user already has a valid reset token
        if (tokenRepository.existsByUserAndUsedFalseAndExpiresAtAfter(user, LocalDateTime.now())) {
            throw new RuntimeException("A password reset request is already pending for this email. Please check your email or wait before requesting again.");
        }

        // Mark all existing tokens for this user as used
        tokenRepository.markAllTokensAsUsedForUser(user);

        // Generate new token
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenExpiryMinutes / 1000);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Send email asynchronously
        emailService.sendPasswordResetEmail(user, token);

        log.info("Password reset initiated for user: {}", email);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (!resetToken.isValid()) {
            throw new RuntimeException("Reset token has expired or has already been used");
        }

        User user = resetToken.getUser();
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Send confirmation email
        emailService.sendPasswordChangedNotification(user);

        log.info("Password successfully reset for user: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public boolean isValidResetToken(String token) {
        return tokenRepository.findByToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public User getUserByResetToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(PasswordResetToken::isValid)
                .map(PasswordResetToken::getUser)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));
    }

    // Clean up expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired password reset tokens");
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Completed cleanup of expired password reset tokens");
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public String generateTemporaryPassword() {
        // Generate a random 12-character password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return password.toString();
    }
} 