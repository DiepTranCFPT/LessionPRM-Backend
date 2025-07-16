package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    Optional<User> findByPasswordResetToken(String token);
    
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:search% OR u.email LIKE %:search%")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Long countUserRegistrations(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") User.Role role);
}