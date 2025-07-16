package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.Course;
import com.lessionprm.backend.entity.Enrollment;
import com.lessionprm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    
    Page<Enrollment> findByUser(User user, Pageable pageable);
    
    Page<Enrollment> findByCourse(Course course, Pageable pageable);
    
    Page<Enrollment> findByStatus(Enrollment.EnrollmentStatus status, Pageable pageable);
    
    List<Enrollment> findByUserAndStatus(User user, Enrollment.EnrollmentStatus status);
    
    boolean existsByUserAndCourse(User user, Course course);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.enrolledAt BETWEEN :startDate AND :endDate")
    Long countEnrollments(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Enrollment e WHERE e.user = :user AND e.status = 'ACTIVE' ORDER BY e.enrolledAt DESC")
    List<Enrollment> findActiveEnrollmentsByUser(@Param("user") User user);
}