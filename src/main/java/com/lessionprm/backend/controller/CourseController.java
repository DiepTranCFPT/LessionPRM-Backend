package com.lessionprm.backend.controller;

import com.lessionprm.backend.dto.course.CourseResponse;
import com.lessionprm.backend.dto.course.CreateCourseRequest;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course", description = "Course management APIs")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all published courses")
    public ResponseEntity<Page<CourseResponse>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        Page<CourseResponse> courses = courseService.getAllCourses(page, size, category, search);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new course (Admin only)")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal User instructor) {
        CourseResponse course = courseService.createCourse(request, instructor);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update course (Admin only)")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CreateCourseRequest request) {
        CourseResponse course = courseService.updateCourse(id, request);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course (Admin only)")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
    }

    @PostMapping("/{id}/enroll")
    @Operation(summary = "Enroll in course")
    public ResponseEntity<Map<String, String>> enrollInCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        courseService.enrollInCourse(id, user);
        return ResponseEntity.ok(Map.of("message", "Successfully enrolled in course"));
    }

    @GetMapping("/my-courses")
    @Operation(summary = "Get user's enrolled courses")
    public ResponseEntity<List<CourseResponse>> getUserCourses(@AuthenticationPrincipal User user) {
        List<CourseResponse> courses = courseService.getUserCourses(user);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all course categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = courseService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured courses")
    public ResponseEntity<List<CourseResponse>> getFeaturedCourses() {
        List<CourseResponse> courses = courseService.getFeaturedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular courses")
    public ResponseEntity<List<CourseResponse>> getPopularCourses() {
        List<CourseResponse> courses = courseService.getPopularCourses();
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publish course (Admin only)")
    public ResponseEntity<Map<String, String>> publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course published successfully"));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Archive course (Admin only)")
    public ResponseEntity<Map<String, String>> archiveCourse(@PathVariable Long id) {
        courseService.archiveCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course archived successfully"));
    }
}