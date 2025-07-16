package com.lessionprm.interceptor;

import com.lessionprm.config.RateLimitingConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    @Autowired
    private RateLimitingConfig.RateLimitService rateLimitService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        // Skip rate limiting for health checks and actuator endpoints
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/actuator") || requestUri.equals("/health")) {
            return true;
        }
        
        // Get client identifier (IP address or user ID if authenticated)
        String clientId = getClientId(request);
        
        if (!rateLimitService.isAllowed(clientId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "Rate limit exceeded. Please try again later.",
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now()));
            return false;
        }
        
        // Add rate limit headers
        int remainingRequests = rateLimitService.getRemainingRequests(clientId);
        response.setHeader("X-RateLimit-Limit", "60");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remainingRequests));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));
        
        return true;
    }
    
    private String getClientId(HttpServletRequest request) {
        // Try to get user ID from authentication if available
        String userId = request.getHeader("X-User-ID");
        if (userId != null) {
            return "user:" + userId;
        }
        
        // Fall back to IP address
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        
        return "ip:" + clientIp;
    }
}