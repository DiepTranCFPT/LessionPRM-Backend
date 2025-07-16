package com.lessionprm.backend.service;

import com.lessionprm.backend.dto.auth.AuthResponse;
import com.lessionprm.backend.dto.auth.LoginRequest;
import com.lessionprm.backend.dto.auth.RegisterRequest;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.repository.UserRepository;
import com.lessionprm.backend.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.Role.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setEmailVerificationToken(UUID.randomUUID().toString());

        userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(user);

        // Generate tokens
        String accessToken = jwtTokenUtil.generateToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        // Create response
        AuthResponse.UserResponse userResponse = new AuthResponse.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatar(),
                user.getRole().name(),
                user.isEmailVerified()
        );

        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) auth.getPrincipal();

        // Generate tokens
        String accessToken = jwtTokenUtil.generateToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        // Create response
        AuthResponse.UserResponse userResponse = new AuthResponse.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatar(),
                user.getRole().name(),
                user.isEmailVerified()
        );

        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpires(java.time.LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user, resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getPasswordResetExpires().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtTokenUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtTokenUtil.isTokenValid(refreshToken, user)) {
            String newAccessToken = jwtTokenUtil.generateToken(user);
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(user);

            AuthResponse.UserResponse userResponse = new AuthResponse.UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getAvatar(),
                    user.getRole().name(),
                    user.isEmailVerified()
            );

            return new AuthResponse(newAccessToken, newRefreshToken, userResponse);
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}