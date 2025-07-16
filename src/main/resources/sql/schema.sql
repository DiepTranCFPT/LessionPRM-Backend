-- Database schema initialization for Course Selling App
-- SQL Server specific script

-- Create database if not exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'LessionPRM')
BEGIN
    CREATE DATABASE LessionPRM;
END
GO

USE LessionPRM;
GO

-- Users table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL UNIQUE,
        email NVARCHAR(100) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        role NVARCHAR(20) NOT NULL DEFAULT 'USER',
        is_enabled BIT NOT NULL DEFAULT 1,
        first_name NVARCHAR(50),
        last_name NVARCHAR(50),
        phone_number NVARCHAR(20),
        created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        updated_at DATETIME2 DEFAULT GETUTCDATE()
    );
    
    CREATE INDEX idx_users_username ON users(username);
    CREATE INDEX idx_users_email ON users(email);
    CREATE INDEX idx_users_role ON users(role);
    CREATE INDEX idx_users_created_at ON users(created_at);
END
GO

-- Courses table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'courses')
BEGIN
    CREATE TABLE courses (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(200) NOT NULL,
        description NTEXT,
        price DECIMAL(10,2) NOT NULL,
        duration_hours INT,
        instructor NVARCHAR(100) NOT NULL,
        category NVARCHAR(100) NOT NULL,
        image_url NVARCHAR(500),
        is_active BIT NOT NULL DEFAULT 1,
        level NVARCHAR(20) DEFAULT 'BEGINNER',
        max_students INT,
        enrolled_students INT DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        updated_at DATETIME2 DEFAULT GETUTCDATE()
    );
    
    CREATE INDEX idx_courses_category ON courses(category);
    CREATE INDEX idx_courses_instructor ON courses(instructor);
    CREATE INDEX idx_courses_level ON courses(level);
    CREATE INDEX idx_courses_is_active ON courses(is_active);
    CREATE INDEX idx_courses_price ON courses(price);
    CREATE INDEX idx_courses_created_at ON courses(created_at);
END
GO

-- Invoices table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'invoices')
BEGIN
    CREATE TABLE invoices (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        course_id BIGINT NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
        payment_method NVARCHAR(20) NOT NULL,
        transaction_id NVARCHAR(100) UNIQUE,
        order_id NVARCHAR(100) UNIQUE,
        payment_url NVARCHAR(500),
        paid_at DATETIME2,
        created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        notes NTEXT,
        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (course_id) REFERENCES courses(id)
    );
    
    CREATE INDEX idx_invoices_user_id ON invoices(user_id);
    CREATE INDEX idx_invoices_course_id ON invoices(course_id);
    CREATE INDEX idx_invoices_status ON invoices(status);
    CREATE INDEX idx_invoices_payment_method ON invoices(payment_method);
    CREATE INDEX idx_invoices_order_id ON invoices(order_id);
    CREATE INDEX idx_invoices_transaction_id ON invoices(transaction_id);
    CREATE INDEX idx_invoices_created_at ON invoices(created_at);
END
GO

-- Expenses table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'expenses')
BEGIN
    CREATE TABLE expenses (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        description NVARCHAR(200) NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        category NVARCHAR(100) NOT NULL,
        date DATE NOT NULL,
        receipt_url NVARCHAR(500),
        is_approved BIT NOT NULL DEFAULT 0,
        approved_by NVARCHAR(100),
        approved_at DATETIME2,
        created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        notes NTEXT
    );
    
    CREATE INDEX idx_expenses_category ON expenses(category);
    CREATE INDEX idx_expenses_date ON expenses(date);
    CREATE INDEX idx_expenses_is_approved ON expenses(is_approved);
    CREATE INDEX idx_expenses_created_at ON expenses(created_at);
END
GO

-- Revenues table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'revenues')
BEGIN
    CREATE TABLE revenues (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        month INT NOT NULL,
        year INT NOT NULL,
        total_revenue DECIMAL(15,2) NOT NULL DEFAULT 0,
        total_expense DECIMAL(15,2) NOT NULL DEFAULT 0,
        profit DECIMAL(15,2) NOT NULL DEFAULT 0,
        course_sales_count INT DEFAULT 0,
        new_users_count INT DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT GETUTCDATE(),
        updated_at DATETIME2 DEFAULT GETUTCDATE(),
        UNIQUE(month, year)
    );
    
    CREATE INDEX idx_revenues_year ON revenues(year);
    CREATE INDEX idx_revenues_month_year ON revenues(month, year);
    CREATE INDEX idx_revenues_profit ON revenues(profit);
END
GO

-- Insert sample admin user (password is 'admin123')
IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, email, password, role, first_name, last_name, is_enabled)
    VALUES ('admin', 'admin@lessionprm.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', 'System', 'Administrator', 1);
END
GO

-- Insert sample courses
IF NOT EXISTS (SELECT * FROM courses WHERE title = 'Spring Boot Fundamentals')
BEGIN
    INSERT INTO courses (title, description, price, duration_hours, instructor, category, level, max_students)
    VALUES 
    ('Spring Boot Fundamentals', 'Learn the basics of Spring Boot framework for Java development', 99.99, 20, 'John Doe', 'Programming', 'BEGINNER', 100),
    ('Advanced React Development', 'Master advanced React concepts and patterns', 149.99, 30, 'Jane Smith', 'Web Development', 'ADVANCED', 80),
    ('Database Design with SQL Server', 'Complete guide to SQL Server database design and optimization', 199.99, 25, 'Mike Johnson', 'Database', 'INTERMEDIATE', 60),
    ('JavaScript ES6+ Modern Features', 'Learn modern JavaScript features and best practices', 79.99, 15, 'Sarah Wilson', 'Programming', 'BEGINNER', 120);
END
GO

PRINT 'Database schema initialized successfully!';