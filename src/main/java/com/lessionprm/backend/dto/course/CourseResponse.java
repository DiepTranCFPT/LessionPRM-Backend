package com.lessionprm.backend.dto.course;

import com.lessionprm.backend.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String shortDescription;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String category;
    private String level;
    private String duration;
    private String requirements;
    private String whatYoullLearn;
    private String status;
    private boolean featured;
    private int enrollmentCount;
    private double averageRating;
    private InstructorInfo instructor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CourseResponse() {}

    public CourseResponse(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.shortDescription = course.getShortDescription();
        this.imageUrl = course.getImageUrl();
        this.price = course.getPrice();
        this.discountPrice = course.getDiscountPrice();
        this.category = course.getCategory();
        this.level = course.getLevel();
        this.duration = course.getDuration();
        this.requirements = course.getRequirements();
        this.whatYoullLearn = course.getWhatYoullLearn();
        this.status = course.getStatus().name();
        this.featured = course.isFeatured();
        this.enrollmentCount = course.getEnrollmentCount();
        this.averageRating = course.getAverageRating();
        this.createdAt = course.getCreatedAt();
        this.updatedAt = course.getUpdatedAt();
        
        if (course.getInstructor() != null) {
            this.instructor = new InstructorInfo(
                course.getInstructor().getId(),
                course.getInstructor().getFullName(),
                course.getInstructor().getEmail(),
                course.getInstructor().getAvatar()
            );
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getWhatYoullLearn() {
        return whatYoullLearn;
    }

    public void setWhatYoullLearn(String whatYoullLearn) {
        this.whatYoullLearn = whatYoullLearn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public InstructorInfo getInstructor() {
        return instructor;
    }

    public void setInstructor(InstructorInfo instructor) {
        this.instructor = instructor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Inner class for instructor info
    public static class InstructorInfo {
        private Long id;
        private String fullName;
        private String email;
        private String avatar;

        public InstructorInfo() {}

        public InstructorInfo(Long id, String fullName, String email, String avatar) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.avatar = avatar;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}