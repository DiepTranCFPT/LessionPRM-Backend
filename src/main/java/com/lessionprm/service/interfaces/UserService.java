package com.lessionprm.service.interfaces;

import com.lessionprm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User createUser(User user);
    
    User updateUser(Long id, User user);
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    Optional<User> getUserByEmail(String email);
    
    List<User> getAllUsers();
    
    Page<User> getAllUsers(Pageable pageable);
    
    List<User> getUsersByRole(User.Role role);
    
    void deleteUser(Long id);
    
    void enableUser(Long id);
    
    void disableUser(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long countUsersByRole(User.Role role);
    
    long countNewUsersBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    User changePassword(Long id, String currentPassword, String newPassword);
    
    User updateProfile(Long id, String firstName, String lastName, String phoneNumber);
}