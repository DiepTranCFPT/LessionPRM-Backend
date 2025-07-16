package com.lessionprm.backend.controller;

import com.lessionprm.backend.dto.auth.AuthResponse;
import com.lessionprm.backend.dto.auth.LoginRequest;
import com.lessionprm.backend.dto.auth.RegisterRequest;
import com.lessionprm.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Map<String, String>> logout() {
        // In a real implementation, you might want to blacklist the token
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/verify-email/{token}")
    @Operation(summary = "Verify email address")
    public ResponseEntity<Map<String, String>> verifyEmail(@PathVariable String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset email")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}