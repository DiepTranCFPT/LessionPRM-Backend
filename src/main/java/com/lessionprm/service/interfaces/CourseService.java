package com.lessionprm.service.interfaces;

import com.lessionprm.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseService {
    
    Course createCourse(Course course);
    
    Course updateCourse(Long id, Course course);
    
    Optional<Course> getCourseById(Long id);
    
    List<Course> getAllCourses();
    
    Page<Course> getAllCourses(Pageable pageable);
    
    List<Course> getActiveCourses();
    
    Page<Course> getActiveCourses(Pageable pageable);
    
    List<Course> getCoursesByCategory(String category);
    
    Page<Course> getCoursesByCategory(String category, Pageable pageable);
    
    List<Course> getCoursesByInstructor(String instructor);
    
    List<Course> getCoursesByLevel(Course.Level level);
    
    List<Course> getCoursesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    Page<Course> getCoursesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    List<Course> searchCourses(String keyword);
    
    Page<Course> searchCourses(String keyword, Pageable pageable);
    
    void deleteCourse(Long id);
    
    void activateCourse(Long id);
    
    void deactivateCourse(Long id);
    
    List<String> getAllCategories();
    
    List<String> getAllInstructors();
    
    long countActiveCourses();
    
    List<Course> getAvailableCourses();
    
    void enrollUserInCourse(Long courseId);
    
    List<Map<String, Object>> getCourseReviews(Long courseId);
    
    void addCourseReview(Long courseId, Integer rating, String comment);
    
    Course enrollStudent(Long courseId);
    
    Course unenrollStudent(Long courseId);
}