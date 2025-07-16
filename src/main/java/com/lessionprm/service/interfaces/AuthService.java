package com.lessionprm.service.interfaces;

import com.lessionprm.dto.request.LoginRequest;
import com.lessionprm.dto.request.RegisterRequest;
import com.lessionprm.dto.response.AuthResponse;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(String refreshToken);
    
    void logout(String token);
    
    boolean isTokenValid(String token);
    
    String getUsernameFromToken(String token);
    
    Long getUserIdFromToken(String token);
}