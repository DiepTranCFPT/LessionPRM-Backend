package com.lessionprm.controller;

import com.lessionprm.service.interfaces.CourseService;
import com.lessionprm.service.interfaces.InvoiceService;
import com.lessionprm.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health monitoring APIs")
public class HealthController implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @GetMapping
    @Operation(summary = "Health check", description = "Basic application health check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Health health = health();
        
        Map<String, Object> response = Map.of(
                "status", health.getStatus().toString(),
                "details", health.getDetails()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check", description = "Detailed health check with service status")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> healthStatus = Map.of(
                "application", "UP",
                "database", isDatabaseHealthy() ? "UP" : "DOWN",
                "services", Map.of(
                    "userService", isServiceHealthy(() -> userService.countUsersByRole(null)),
                    "courseService", isServiceHealthy(() -> courseService.countActiveCourses()),
                    "invoiceService", isServiceHealthy(() -> invoiceService.countByStatus(null))
                ),
                "timestamp", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(healthStatus);
    }
    
    @Override
    public Health health() {
        try {
            // Check database connectivity
            boolean dbHealthy = isDatabaseHealthy();
            
            if (dbHealthy) {
                return Health.up()
                        .withDetail("database", "UP")
                        .withDetail("timestamp", java.time.LocalDateTime.now())
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "DOWN")
                        .withDetail("timestamp", java.time.LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", java.time.LocalDateTime.now())
                    .build();
        }
    }
    
    private boolean isDatabaseHealthy() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (Exception e) {
            return false;
        }
    }
    
    private String isServiceHealthy(Runnable serviceCall) {
        try {
            serviceCall.run();
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }
}