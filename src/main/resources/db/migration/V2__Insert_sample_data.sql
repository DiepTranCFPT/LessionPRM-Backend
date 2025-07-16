-- Insert default admin user and sample data
-- Version: 1.1.0
-- Description: Add default admin user and sample courses

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, role, first_name, last_name, is_enabled) 
VALUES ('admin', 'admin@lessionprm.com', '$2a$10$ZwR4QvgRXZgQpGDwzN6dD.rYuNjzz8VuJ2zNVe8JqM4Yy9Zr4RKe6', 'ADMIN', 'System', 'Administrator', 1);

-- Insert sample categories and courses
INSERT INTO courses (title, description, price, duration_hours, instructor, category, level, max_students, is_active) VALUES
('Java Programming Fundamentals', 'Learn the basics of Java programming language with hands-on exercises and projects.', 299.99, 40, 'Dr. John Smith', 'Programming', 'BEGINNER', 30, 1),
('Advanced Spring Boot Development', 'Master Spring Boot framework for building enterprise-level applications.', 499.99, 60, 'Sarah Johnson', 'Programming', 'ADVANCED', 25, 1),
('Web Development with React', 'Build modern web applications using React.js and related technologies.', 399.99, 50, 'Mike Chen', 'Web Development', 'INTERMEDIATE', 35, 1),
('Database Design and SQL', 'Learn database design principles and SQL for effective data management.', 349.99, 35, 'Dr. Emily Davis', 'Database', 'BEGINNER', 40, 1),
('DevOps and Cloud Computing', 'Introduction to DevOps practices and cloud computing with AWS.', 599.99, 70, 'Alex Rodriguez', 'Cloud Computing', 'INTERMEDIATE', 20, 1),
('Mobile App Development with Flutter', 'Create cross-platform mobile applications using Flutter framework.', 449.99, 55, 'Lisa Wang', 'Mobile Development', 'INTERMEDIATE', 30, 1),
('Machine Learning Basics', 'Introduction to machine learning concepts and practical implementations.', 699.99, 80, 'Dr. Robert Kim', 'Data Science', 'BEGINNER', 25, 1),
('Cybersecurity Fundamentals', 'Learn essential cybersecurity concepts and best practices.', 379.99, 45, 'Kevin Brown', 'Security', 'BEGINNER', 35, 1);

-- Insert sample expense categories
INSERT INTO expenses (description, amount, category, date, is_approved, approved_by, approved_at) VALUES
('Office Rent - January 2024', 2500.00, 'Office Expenses', '2024-01-01', 1, 'admin', CURRENT_TIMESTAMP),
('Server Hosting Costs', 150.00, 'Technology', '2024-01-15', 1, 'admin', CURRENT_TIMESTAMP),
('Marketing Campaign - Social Media', 800.00, 'Marketing', '2024-01-20', 1, 'admin', CURRENT_TIMESTAMP),
('Software Licenses', 300.00, 'Technology', '2024-01-25', 1, 'admin', CURRENT_TIMESTAMP),
('Training Materials', 200.00, 'Education', '2024-01-30', 0, NULL, NULL);

-- Insert sample revenue records
INSERT INTO revenue (amount, source, date, notes) VALUES
(299.99, 'Course Sales', '2024-01-15', 'Java Programming Fundamentals'),
(499.99, 'Course Sales', '2024-01-16', 'Advanced Spring Boot Development'),
(399.99, 'Course Sales', '2024-01-17', 'Web Development with React'),
(349.99, 'Course Sales', '2024-01-18', 'Database Design and SQL'),
(599.99, 'Course Sales', '2024-01-19', 'DevOps and Cloud Computing');