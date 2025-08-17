package com.bika.user.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.user.dto.PasswordResetDTO;
import com.bika.user.dto.PasswordResetRequestDTO;
import com.bika.user.entity.User;
import com.bika.user.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/password")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Password Reset", description = "Password reset and recovery APIs")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(
        summary = "Request password reset",
        description = "Send a password reset email to the specified email address"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Password reset email sent successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid email or user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/reset-request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDTO request) {
        try {
            passwordResetService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of(
                "message", "If an account with that email exists, a password reset link has been sent.",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("Password reset request failed", e);
            // Always return success to prevent email enumeration attacks
            return ResponseEntity.ok(Map.of(
                "message", "If an account with that email exists, a password reset link has been sent.",
                "status", "success"
            ));
        }
    }

    @Operation(
        summary = "Validate reset token",
        description = "Check if a password reset token is valid and not expired"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token validation result"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid token format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateResetToken(@RequestParam String token) {
        try {
            boolean isValid = passwordResetService.isValidResetToken(token);
            if (isValid) {
                User user = passwordResetService.getUserByResetToken(token);
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "email", maskEmail(user.getEmail()),
                    "message", "Token is valid"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "Token is invalid or expired"
                ));
            }
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "Token is invalid or expired"
            ));
        }
    }

    @Operation(
        summary = "Reset password",
        description = "Reset user password using a valid reset token"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Password reset successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid token, passwords don't match, or validation errors",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        try {
            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Passwords do not match",
                    "status", "error"
                ));
            }

            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            
            return ResponseEntity.ok(Map.of(
                "message", "Password has been reset successfully. You can now log in with your new password.",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("Password reset failed", e);
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "status", "error"
            ));
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return "***@" + domain;
        }
        
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + domain;
    }
} 