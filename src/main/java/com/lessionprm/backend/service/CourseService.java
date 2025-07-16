package com.lessionprm.backend.service;

import com.lessionprm.backend.dto.course.CourseResponse;
import com.lessionprm.backend.dto.course.CreateCourseRequest;
import com.lessionprm.backend.entity.Course;
import com.lessionprm.backend.entity.Enrollment;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.repository.CourseRepository;
import com.lessionprm.backend.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public Page<CourseResponse> getAllCourses(int page, int size, String category, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> coursePage;

        if (StringUtils.hasText(category) && StringUtils.hasText(search)) {
            coursePage = courseRepository.findByStatusAndCategoryAndSearchTerm(
                Course.CourseStatus.PUBLISHED, category, search, pageable);
        } else if (StringUtils.hasText(category)) {
            coursePage = courseRepository.findByStatusAndCategory(
                Course.CourseStatus.PUBLISHED, category, pageable);
        } else if (StringUtils.hasText(search)) {
            coursePage = courseRepository.findByStatusAndSearchTerm(
                Course.CourseStatus.PUBLISHED, search, pageable);
        } else {
            coursePage = courseRepository.findByStatus(Course.CourseStatus.PUBLISHED, pageable);
        }

        return coursePage.map(CourseResponse::new);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return new CourseResponse(course);
    }

    public CourseResponse createCourse(CreateCourseRequest request, User instructor) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setImageUrl(request.getImageUrl());
        course.setPrice(request.getPrice());
        course.setDiscountPrice(request.getDiscountPrice());
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setDuration(request.getDuration());
        course.setRequirements(request.getRequirements());
        course.setWhatYoullLearn(request.getWhatYoullLearn());
        course.setFeatured(request.isFeatured());
        course.setInstructor(instructor);
        course.setStatus(Course.CourseStatus.DRAFT);

        course = courseRepository.save(course);
        return new CourseResponse(course);
    }

    public CourseResponse updateCourse(Long id, CreateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setImageUrl(request.getImageUrl());
        course.setPrice(request.getPrice());
        course.setDiscountPrice(request.getDiscountPrice());
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setDuration(request.getDuration());
        course.setRequirements(request.getRequirements());
        course.setWhatYoullLearn(request.getWhatYoullLearn());
        course.setFeatured(request.isFeatured());

        course = courseRepository.save(course);
        return new CourseResponse(course);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Check if course has enrollments
        if (!course.getEnrollments().isEmpty()) {
            throw new RuntimeException("Cannot delete course with enrollments");
        }
        
        courseRepository.delete(course);
    }

    public void enrollInCourse(Long courseId, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getStatus() != Course.CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available for enrollment");
        }

        // Check if user is already enrolled
        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("User is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment(user, course);
        enrollmentRepository.save(enrollment);
    }

    public List<CourseResponse> getUserCourses(User user) {
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByUser(user);
        return enrollments.stream()
                .map(enrollment -> new CourseResponse(enrollment.getCourse()))
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        return courseRepository.findDistinctCategories();
    }

    public List<CourseResponse> getFeaturedCourses() {
        Pageable topFive = PageRequest.of(0, 5);
        Page<Course> featuredCourses = courseRepository.findByFeatured(true, topFive);
        return featuredCourses.stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
    }

    public List<CourseResponse> getPopularCourses() {
        Pageable topFive = PageRequest.of(0, 5);
        List<Course> popularCourses = courseRepository.findMostPopularCourses(topFive);
        return popularCourses.stream()
                .map(CourseResponse::new)
                .collect(Collectors.toList());
    }

    public void publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(Course.CourseStatus.PUBLISHED);
        courseRepository.save(course);
    }

    public void archiveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(Course.CourseStatus.ARCHIVED);
        courseRepository.save(course);
    }
}