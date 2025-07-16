package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.Course;
import com.lessionprm.backend.entity.CourseReview;
import com.lessionprm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    
    Page<CourseReview> findByCourse(Course course, Pageable pageable);
    
    Page<CourseReview> findByUser(User user, Pageable pageable);
    
    Optional<CourseReview> findByUserAndCourse(User user, Course course);
    
    boolean existsByUserAndCourse(User user, Course course);
    
    @Query("SELECT AVG(cr.rating) FROM CourseReview cr WHERE cr.course = :course")
    Double findAverageRatingByCourse(@Param("course") Course course);
    
    @Query("SELECT COUNT(cr) FROM CourseReview cr WHERE cr.course = :course")
    Long countByCourse(@Param("course") Course course);
}