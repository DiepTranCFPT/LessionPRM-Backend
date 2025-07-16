-- Initial database schema for LessionPRM
-- Version: 1.0.0
-- Description: Create initial tables for users, courses, invoices, expenses, and revenue

-- Create users table
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL DEFAULT 'USER',
    first_name NVARCHAR(50),
    last_name NVARCHAR(50),
    phone_number NVARCHAR(20),
    is_enabled BIT DEFAULT 1,
    created_at DATETIME2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME2(6) DEFAULT CURRENT_TIMESTAMP
);

-- Create courses table
CREATE TABLE courses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    description NTEXT,
    price DECIMAL(10,2) NOT NULL,
    duration_hours INT,
    instructor NVARCHAR(100) NOT NULL,
    category NVARCHAR(100) NOT NULL,
    image_url NVARCHAR(255),
    level NVARCHAR(20) CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    max_students INT,
    enrolled_students INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    created_at DATETIME2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME2(6) DEFAULT CURRENT_TIMESTAMP
);

-- Create invoices table
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
    paid_at DATETIME2(6),
    created_at DATETIME2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes NTEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Create expenses table
CREATE TABLE expenses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    description NVARCHAR(200) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category NVARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    receipt_url NVARCHAR(255),
    is_approved BIT DEFAULT 0,
    approved_by NVARCHAR(100),
    approved_at DATETIME2(6),
    created_at DATETIME2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes NTEXT
);

-- Create revenue table
CREATE TABLE revenue (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    source NVARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    invoice_id BIGINT,
    created_at DATETIME2(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes NTEXT,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);

CREATE INDEX idx_courses_category ON courses(category);
CREATE INDEX idx_courses_instructor ON courses(instructor);
CREATE INDEX idx_courses_is_active ON courses(is_active);
CREATE INDEX idx_courses_level ON courses(level);

CREATE INDEX idx_invoices_user_id ON invoices(user_id);
CREATE INDEX idx_invoices_course_id ON invoices(course_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_invoices_transaction_id ON invoices(transaction_id);
CREATE INDEX idx_invoices_created_at ON invoices(created_at);

CREATE INDEX idx_expenses_category ON expenses(category);
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_expenses_is_approved ON expenses(is_approved);

CREATE INDEX idx_revenue_date ON revenue(date);
CREATE INDEX idx_revenue_source ON revenue(source);
CREATE INDEX idx_revenue_invoice_id ON revenue(invoice_id);