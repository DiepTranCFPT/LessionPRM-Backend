package com.lessionprm.backend.config;

import com.lessionprm.backend.entity.Course;
import com.lessionprm.backend.entity.User;
import com.lessionprm.backend.repository.CourseRepository;
import com.lessionprm.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (shouldSeedData()) {
            seedUsers();
            seedCourses();
            logger.info("Sample data seeded successfully!");
        } else {
            logger.info("Data already exists, skipping seeding");
        }
    }

    private boolean shouldSeedData() {
        return userRepository.count() == 0;
    }

    private void seedUsers() {
        // Create admin user
        if (!userRepository.existsByEmail("admin@lessionprm.com")) {
            User admin = new User();
            admin.setEmail("admin@lessionprm.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setPhone("0123456789");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setEmailVerified(true);
            userRepository.save(admin);
            logger.info("Created admin user: admin@lessionprm.com / admin123");
        }

        // Create regular user
        if (!userRepository.existsByEmail("user@lessionprm.com")) {
            User user = new User();
            user.setEmail("user@lessionprm.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setFullName("Test User");
            user.setPhone("0987654321");
            user.setRole(User.Role.USER);
            user.setStatus(User.UserStatus.ACTIVE);
            user.setEmailVerified(true);
            userRepository.save(user);
            logger.info("Created regular user: user@lessionprm.com / user123");
        }

        // Create instructor user
        if (!userRepository.existsByEmail("instructor@lessionprm.com")) {
            User instructor = new User();
            instructor.setEmail("instructor@lessionprm.com");
            instructor.setPassword(passwordEncoder.encode("instructor123"));
            instructor.setFullName("John Instructor");
            instructor.setPhone("0555123456");
            instructor.setRole(User.Role.ADMIN); // For now, instructors are admins
            instructor.setStatus(User.UserStatus.ACTIVE);
            instructor.setEmailVerified(true);
            userRepository.save(instructor);
            logger.info("Created instructor user: instructor@lessionprm.com / instructor123");
        }
    }

    private void seedCourses() {
        User instructor = userRepository.findByEmail("instructor@lessionprm.com").orElse(null);
        if (instructor == null) {
            instructor = userRepository.findByEmail("admin@lessionprm.com").orElse(null);
        }

        if (instructor != null) {
            createCourse(
                "Java Programming Fundamentals",
                "Learn the basics of Java programming from scratch. This comprehensive course covers variables, data types, control structures, methods, and object-oriented programming concepts.",
                "Master Java programming basics",
                new BigDecimal("99.99"),
                new BigDecimal("79.99"),
                "Programming",
                "BEGINNER",
                "8 weeks",
                "No prior programming experience required",
                "Variables and data types, Control structures, Methods and functions, Object-oriented programming, Exception handling",
                true,
                instructor
            );

            createCourse(
                "Web Development with React",
                "Build modern web applications using React.js. Learn components, state management, hooks, routing, and best practices for creating responsive user interfaces.",
                "Build modern web apps with React",
                new BigDecimal("149.99"),
                new BigDecimal("119.99"),
                "Web Development",
                "INTERMEDIATE",
                "10 weeks",
                "Basic JavaScript knowledge",
                "React components, State and props, Hooks, Routing, API integration, Testing",
                true,
                instructor
            );

            createCourse(
                "Mobile App Development with Flutter",
                "Create cross-platform mobile applications using Flutter and Dart. Learn to build beautiful, natively compiled applications for mobile from a single codebase.",
                "Build cross-platform mobile apps",
                new BigDecimal("199.99"),
                null,
                "Mobile Development",
                "INTERMEDIATE",
                "12 weeks",
                "Basic programming knowledge",
                "Flutter widgets, Dart language, State management, Navigation, API integration, Publishing apps",
                false,
                instructor
            );

            createCourse(
                "Data Science with Python",
                "Dive into data science using Python. Learn data analysis, visualization, machine learning basics, and how to work with popular libraries like pandas and numpy.",
                "Master data science with Python",
                new BigDecimal("179.99"),
                new BigDecimal("139.99"),
                "Data Science",
                "INTERMEDIATE",
                "14 weeks",
                "Basic Python knowledge",
                "Data analysis, Data visualization, Machine learning basics, pandas and numpy, Statistical analysis",
                true,
                instructor
            );

            createCourse(
                "Digital Marketing Essentials",
                "Learn the fundamentals of digital marketing including SEO, social media marketing, email marketing, and analytics to grow your business online.",
                "Master digital marketing strategies",
                new BigDecimal("89.99"),
                new BigDecimal("69.99"),
                "Marketing",
                "BEGINNER",
                "6 weeks",
                "No prior marketing experience required",
                "SEO basics, Social media marketing, Email marketing, Google Analytics, Content marketing",
                false,
                instructor
            );

            createCourse(
                "UI/UX Design Principles",
                "Master the art of user interface and user experience design. Learn design thinking, prototyping, and how to create user-centered digital experiences.",
                "Create amazing user experiences",
                new BigDecimal("129.99"),
                new BigDecimal("99.99"),
                "Design",
                "BEGINNER",
                "8 weeks",
                "No design experience required",
                "Design thinking, User research, Prototyping, Visual design, Usability testing",
                true,
                instructor
            );

            logger.info("Created sample courses");
        }
    }

    private void createCourse(String title, String description, String shortDescription,
                            BigDecimal price, BigDecimal discountPrice, String category,
                            String level, String duration, String requirements,
                            String whatYoullLearn, boolean featured, User instructor) {
        
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setShortDescription(shortDescription);
        course.setImageUrl("https://via.placeholder.com/400x300");
        course.setPrice(price);
        course.setDiscountPrice(discountPrice);
        course.setCategory(category);
        course.setLevel(level);
        course.setDuration(duration);
        course.setRequirements(requirements);
        course.setWhatYoullLearn(whatYoullLearn);
        course.setFeatured(featured);
        course.setInstructor(instructor);
        course.setStatus(Course.CourseStatus.PUBLISHED);
        
        courseRepository.save(course);
    }
}