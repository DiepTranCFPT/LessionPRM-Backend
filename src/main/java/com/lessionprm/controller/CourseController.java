package com.lessionprm.controller;

import com.lessionprm.entity.Course;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.service.interfaces.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management APIs")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @GetMapping
    @Operation(summary = "Get all active courses", description = "Get all active courses with pagination")
    public ResponseEntity<Page<Course>> getAllActiveCourses(Pageable pageable) {
        Page<Course> courses = courseService.getActiveCourses(pageable);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Get course details by ID")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return ResponseEntity.ok(course);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new course", description = "Create a new course (Admin only)")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.ok(createdCourse);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update course", description = "Update course details (Admin only)")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        return ResponseEntity.ok(updatedCourse);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course", description = "Delete course (Admin only)")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate course", description = "Activate course (Admin only)")
    public ResponseEntity<Map<String, String>> activateCourse(@PathVariable Long id) {
        courseService.activateCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course activated successfully"));
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate course", description = "Deactivate course (Admin only)")
    public ResponseEntity<Map<String, String>> deactivateCourse(@PathVariable Long id) {
        courseService.deactivateCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course deactivated successfully"));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search courses", description = "Search courses by keyword")
    public ResponseEntity<Page<Course>> searchCourses(
            @RequestParam String keyword, 
            Pageable pageable) {
        Page<Course> courses = courseService.searchCourses(keyword, pageable);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get courses by category", description = "Get courses by category")
    public ResponseEntity<Page<Course>> getCoursesByCategory(
            @PathVariable String category, 
            Pageable pageable) {
        Page<Course> courses = courseService.getCoursesByCategory(category, pageable);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/instructor/{instructor}")
    @Operation(summary = "Get courses by instructor", description = "Get courses by instructor")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable String instructor) {
        List<Course> courses = courseService.getCoursesByInstructor(instructor);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/level/{level}")
    @Operation(summary = "Get courses by level", description = "Get courses by level")
    public ResponseEntity<List<Course>> getCoursesByLevel(@PathVariable Course.Level level) {
        List<Course> courses = courseService.getCoursesByLevel(level);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/price-range")
    @Operation(summary = "Get courses by price range", description = "Get courses by price range")
    public ResponseEntity<Page<Course>> getCoursesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            Pageable pageable) {
        Page<Course> courses = courseService.getCoursesByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Get all available course categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = courseService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/instructors")
    @Operation(summary = "Get all instructors", description = "Get all course instructors")
    public ResponseEntity<List<String>> getAllInstructors() {
        List<String> instructors = courseService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available courses", description = "Get courses with available spots")
    public ResponseEntity<List<Course>> getAvailableCourses() {
        List<Course> courses = courseService.getAvailableCourses();
        return ResponseEntity.ok(courses);
    }
    
    @PostMapping("/{id}/enroll")
    @Operation(summary = "Enroll in course", description = "Enroll current user in a course")
    public ResponseEntity<Map<String, String>> enrollInCourse(@PathVariable Long id) {
        courseService.enrollUserInCourse(id);
        return ResponseEntity.ok(Map.of("message", "Successfully enrolled in course"));
    }
    
    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get course reviews", description = "Get reviews for a specific course")
    public ResponseEntity<List<Map<String, Object>>> getCourseReviews(@PathVariable Long id) {
        List<Map<String, Object>> reviews = courseService.getCourseReviews(id);
        return ResponseEntity.ok(reviews);
    }
    
    @PostMapping("/{id}/reviews")
    @Operation(summary = "Add course review", description = "Add a review for a course")
    public ResponseEntity<Map<String, String>> addCourseReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData) {
        Integer rating = (Integer) reviewData.get("rating");
        String comment = (String) reviewData.get("comment");
        courseService.addCourseReview(id, rating, comment);
        return ResponseEntity.ok(Map.of("message", "Review added successfully"));
    }
}