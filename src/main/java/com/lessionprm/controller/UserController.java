package com.lessionprm.controller;

import com.lessionprm.entity.User;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.service.interfaces.AuthService;
import com.lessionprm.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current user profile")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User currentUser = userService.getUserById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(currentUser);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current user profile")
    public ResponseEntity<User> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, String> profileData) {
        
        User user = (User) authentication.getPrincipal();
        String firstName = profileData.get("firstName");
        String lastName = profileData.get("lastName");
        String phoneNumber = profileData.get("phoneNumber");
        
        User updatedUser = userService.updateProfile(user.getId(), firstName, lastName, phoneNumber);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @RequestBody Map<String, String> passwordData) {
        
        User user = (User) authentication.getPrincipal();
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");
        
        userService.changePassword(user.getId(), currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users with pagination (Admin only)")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (Admin only)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user details (Admin only)")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user (Admin only)")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
    
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable user", description = "Enable user account (Admin only)")
    public ResponseEntity<Map<String, String>> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok(Map.of("message", "User enabled successfully"));
    }
    
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable user", description = "Disable user account (Admin only)")
    public ResponseEntity<Map<String, String>> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok(Map.of("message", "User disabled successfully"));
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Get users by role (Admin only)")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable User.Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Get user statistics (Admin only)")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        long totalUsers = userService.countUsersByRole(User.Role.USER) + userService.countUsersByRole(User.Role.ADMIN);
        long admins = userService.countUsersByRole(User.Role.ADMIN);
        long users = userService.countUsersByRole(User.Role.USER);
        
        Map<String, Long> stats = Map.of(
                "totalUsers", totalUsers,
                "adminUsers", admins,
                "regularUsers", users
        );
        
        return ResponseEntity.ok(stats);
    }
}