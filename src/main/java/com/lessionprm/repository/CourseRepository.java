package com.lessionprm.repository;

import com.lessionprm.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByIsActiveTrue();
    
    Page<Course> findByIsActiveTrue(Pageable pageable);
    
    List<Course> findByCategory(String category);
    
    Page<Course> findByCategory(String category, Pageable pageable);
    
    List<Course> findByInstructor(String instructor);
    
    Page<Course> findByInstructor(String instructor, Pageable pageable);
    
    List<Course> findByLevel(Course.Level level);
    
    Page<Course> findByLevel(Course.Level level, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.price BETWEEN :minPrice AND :maxPrice")
    List<Course> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                 @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c.price BETWEEN :minPrice AND :maxPrice")
    Page<Course> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                 @Param("maxPrice") BigDecimal maxPrice, 
                                 Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.instructor) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Course> searchCourses(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.instructor) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.isActive = true ORDER BY c.category")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT c.instructor FROM Course c WHERE c.isActive = true ORDER BY c.instructor")
    List<String> findAllInstructors();
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.isActive = true")
    long countActiveCourses();
    
    @Query("SELECT c FROM Course c WHERE c.enrolledStudents < c.maxStudents AND c.isActive = true")
    List<Course> findAvailableCourses();
}