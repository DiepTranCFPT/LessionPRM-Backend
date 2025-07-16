package com.lessionprm.backend.repository;

import com.lessionprm.backend.entity.Course;
import com.lessionprm.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Page<Course> findByStatus(Course.CourseStatus status, Pageable pageable);
    
    Page<Course> findByCategory(String category, Pageable pageable);
    
    Page<Course> findByLevel(String level, Pageable pageable);
    
    Page<Course> findByInstructor(User instructor, Pageable pageable);
    
    Page<Course> findByFeatured(boolean featured, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Course> findByStatusAndSearchTerm(@Param("status") Course.CourseStatus status, 
                                          @Param("search") String search, 
                                          Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.category = :category")
    Page<Course> findByStatusAndCategory(@Param("status") Course.CourseStatus status, 
                                        @Param("category") String category, 
                                        Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.category = :category AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Course> findByStatusAndCategoryAndSearchTerm(@Param("status") Course.CourseStatus status,
                                                      @Param("category") String category,
                                                      @Param("search") String search,
                                                      Pageable pageable);
    
    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.status = 'PUBLISHED'")
    List<String> findDistinctCategories();
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    Long countCoursesCreated(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Course c JOIN c.enrollments e GROUP BY c ORDER BY COUNT(e) DESC")
    List<Course> findMostPopularCourses(Pageable pageable);
}