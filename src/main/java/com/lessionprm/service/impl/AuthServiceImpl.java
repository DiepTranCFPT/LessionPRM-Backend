package com.lessionprm.service.impl;

import com.lessionprm.dto.request.LoginRequest;
import com.lessionprm.dto.request.RegisterRequest;
import com.lessionprm.dto.response.AuthResponse;
import com.lessionprm.entity.User;
import com.lessionprm.exception.BadRequestException;
import com.lessionprm.exception.UnauthorizedException;
import com.lessionprm.service.interfaces.AuthService;
import com.lessionprm.service.interfaces.UserService;
import com.lessionprm.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    private final Set<String> blacklistedTokens = new HashSet<>();
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        
        if (userService.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        
        User savedUser = userService.createUser(user);
        
        String accessToken = jwtUtil.generateToken(savedUser, savedUser.getId(), savedUser.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser, savedUser.getId());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole().name()
        );
        
        return new AuthResponse(accessToken, refreshToken, jwtUtil.getExpirationTime(), userInfo);
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = (User) userDetails;
            
            String accessToken = jwtUtil.generateToken(user, user.getId(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user, user.getId());
            
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name()
            );
            
            return new AuthResponse(accessToken, refreshToken, jwtUtil.getExpirationTime(), userInfo);
            
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid username/email or password");
        }
    }
    
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Token is not a refresh token");
        }
        
        if (isTokenBlacklisted(refreshToken)) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        
        String username = jwtUtil.extractUsername(refreshToken);
        Long userId = jwtUtil.extractUserId(refreshToken);
        
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        String newAccessToken = jwtUtil.generateToken(user, user.getId(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user, user.getId());
        
        // Blacklist the old refresh token
        blacklistedTokens.add(refreshToken);
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
        
        return new AuthResponse(newAccessToken, newRefreshToken, jwtUtil.getExpirationTime(), userInfo);
    }
    
    @Override
    public void logout(String token) {
        blacklistedTokens.add(token);
    }
    
    @Override
    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token) && !isTokenBlacklisted(token);
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
    
    @Override
    public Long getUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token);
    }
    
    private boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}