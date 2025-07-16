package com.lessionprm.service.impl;

import com.lessionprm.entity.Course;
import com.lessionprm.entity.User;
import com.lessionprm.exception.BadRequestException;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.repository.CourseRepository;
import com.lessionprm.service.interfaces.CourseService;
import com.lessionprm.service.interfaces.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Override
    public Course createCourse(Course course) {
        course.setIsActive(true);
        course.setEnrolledStudents(0);
        return courseRepository.save(course);
    }
    
    @Override
    public Course updateCourse(Long id, Course course) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setPrice(course.getPrice());
        existingCourse.setDurationHours(course.getDurationHours());
        existingCourse.setInstructor(course.getInstructor());
        existingCourse.setCategory(course.getCategory());
        existingCourse.setImageUrl(course.getImageUrl());
        existingCourse.setLevel(course.getLevel());
        existingCourse.setMaxStudents(course.getMaxStudents());
        
        return courseRepository.save(existingCourse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getActiveCourses() {
        return courseRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Course> getActiveCourses(Pageable pageable) {
        return courseRepository.findByIsActiveTrue(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Course> getCoursesByCategory(String category, Pageable pageable) {
        return courseRepository.findByCategory(category, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByInstructor(String instructor) {
        return courseRepository.findByInstructor(instructor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByLevel(Course.Level level) {
        return courseRepository.findByLevel(level);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return courseRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Course> getCoursesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return courseRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Course> searchCourses(String keyword, Pageable pageable) {
        return courseRepository.searchCourses(keyword, pageable);
    }
    
    @Override
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
    
    @Override
    public void activateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsActive(true);
        courseRepository.save(course);
    }
    
    @Override
    public void deactivateCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setIsActive(false);
        courseRepository.save(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return courseRepository.findAllCategories();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllInstructors() {
        return courseRepository.findAllInstructors();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveCourses() {
        return courseRepository.countActiveCourses();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }
    
    @Override
    public Course enrollStudent(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (!course.getIsActive()) {
            throw new BadRequestException("Course is not active");
        }
        
        if (course.getMaxStudents() != null && course.getEnrolledStudents() >= course.getMaxStudents()) {
            throw new BadRequestException("Course is full");
        }
        
        course.setEnrolledStudents(course.getEnrolledStudents() + 1);
        return courseRepository.save(course);
    }
    
    @Override
    public Course unenrollStudent(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (course.getEnrolledStudents() > 0) {
            course.setEnrolledStudents(course.getEnrolledStudents() - 1);
            return courseRepository.save(course);
        }
        
        return course;
    }
    
    @Override
    public void enrollUserInCourse(Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (!course.getIsActive()) {
            throw new BadRequestException("Course is not active");
        }
        
        // Check if user already enrolled (purchased the course)
        if (invoiceService.hasUserPurchasedCourse(currentUser.getId(), courseId)) {
            throw new BadRequestException("User is already enrolled in this course");
        }
        
        throw new BadRequestException("Course enrollment requires payment. Please purchase the course first.");
    }
    
    @Override
    public List<Map<String, Object>> getCourseReviews(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        // For now, return mock reviews since we don't have a Review entity yet
        // In a real implementation, you would have a Review entity and repository
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        // Mock review data
        Map<String, Object> review1 = new HashMap<>();
        review1.put("id", 1L);
        review1.put("rating", 5);
        review1.put("comment", "Excellent course! Very well structured and informative.");
        review1.put("userName", "John Doe");
        review1.put("createdAt", LocalDateTime.now().minusDays(7));
        reviews.add(review1);
        
        Map<String, Object> review2 = new HashMap<>();
        review2.put("id", 2L);
        review2.put("rating", 4);
        review2.put("comment", "Good content, but could use more practical examples.");
        review2.put("userName", "Jane Smith");
        review2.put("createdAt", LocalDateTime.now().minusDays(3));
        reviews.add(review2);
        
        return reviews;
    }
    
    @Override
    public void addCourseReview(Long courseId, Integer rating, String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        // Check if user has purchased the course
        if (!invoiceService.hasUserPurchasedCourse(currentUser.getId(), courseId)) {
            throw new BadRequestException("You must purchase the course before leaving a review");
        }
        
        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        
        // For now, just validate the input since we don't have a Review entity yet
        // In a real implementation, you would save the review to a Review entity
        // Review review = new Review(currentUser, course, rating, comment);
        // reviewRepository.save(review);
        
        // Mock implementation - just log that review was added
        System.out.println("Review added for course " + courseId + " by user " + currentUser.getUsername() + 
                          " with rating " + rating + " and comment: " + comment);
    }
}