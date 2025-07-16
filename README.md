# LessionPRM Backend

A comprehensive Learning Management System backend built with Spring Boot 3.2, featuring MoMo payment integration, JWT authentication, and complete API endpoints for course management.

## 🚀 Features

- **Complete Authentication System**: JWT-based authentication with registration, login, email verification, and password reset
- **MoMo Payment Integration**: Full payment flow with signature verification and callback handling
- **Course Management**: CRUD operations for courses with enrollment and review systems
- **User Management**: Role-based access control (Admin/User) with profile management
- **Invoice & Payment Tracking**: Complete invoice lifecycle with payment status tracking
- **Expense Management**: Admin tools for expense tracking and reporting
- **Revenue Analytics**: Dashboard with revenue statistics and reporting
- **Comprehensive API Documentation**: Swagger/OpenAPI documentation
- **Global Exception Handling**: Structured error responses with validation
- **Database Support**: SQL Server with JPA/Hibernate
- **Sample Data Seeding**: Auto-populated test data for development

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: SQL Server (with H2 for testing)
- **ORM**: JPA/Hibern=======
# Course Selling App Backend - Spring Boot Application

A comprehensive backend system for an online course selling platform built with Spring Boot 3.x, featuring JWT authentication, MoMo payment integration, and following SOLID principles.

## 🚀 Features

### Core Functionality
- **User Management**: Registration, login, profile management, role-based access control
- **Course Management**: CRUD operations, search, categorization, enrollment tracking
- **Payment Processing**: MoMo payment gateway integration with callback handling
- **Invoice Management**: Order processing, payment tracking, statistics
- **Analytics**: Revenue tracking, expense management, dashboard statistics

### Technical Features
- **JWT Authentication**: Secure token-based authentication with refresh tokens
- **Role-Based Authorization**: USER and ADMIN roles with appropriate permissions
- **Database Integration**: SQL Server with JPA/Hibernate
- **API Documentation**: Swagger/OpenAPI integration
- **Error Handling**: Global exception handling with standardized responses
- **Security**: CORS configuration, password encryption, input validation

## 🏗️ Architecture

The application follows SOLID principles with a clean architecture:

```
src/main/java/com/lessionprm/
├── LessionPrmApplication.java          # Main application class
├── config/                             # Configuration classes
│   ├── SecurityConfig.java            # Spring Security configuration
│   ├── DatabaseConfig.java            # Database configuration
│   └── MoMoConfig.java                # MoMo payment configuration
├── controller/                         # REST API controllers
│   ├── AuthController.java            # Authentication endpoints
│   ├── CourseController.java          # Course management endpoints
│   ├── UserController.java            # User management endpoints
│   ├── InvoiceController.java         # Invoice management endpoints
│   └── PaymentController.java         # Payment processing endpoints
├── service/                            # Business logic layer
│   ├── interfaces/                     # Service interfaces
│   └── impl/                          # Service implementations
├── repository/                         # Data access layer
├── entity/                            # JPA entities
├── dto/                               # Data transfer objects
│   ├── request/                       # Request DTOs
│   └── response/                      # Response DTOs
├── exception/                         # Custom exceptions
├── security/                          # Security components
│   ├── JwtAuthenticationFilter.java   # JWT filter
│   ├── JwtAuthenticationEntryPoint.java
│   └── CustomUserDetailsService.java
└── util/                              # Utility classes
    └── JwtUtil.java                   # JWT utilities
```

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: SQL Server with JPA/Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Payment**: MoMo API integration
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+ 
- SQL Server (or Docker for local development)
- Git

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/DiepTranCFPT/LessionPRM-Backend.git
cd LessionPRM-Backend
```

### 2. Configure Database
Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=LessionPRM;encrypt=true;trustServerCertificate=true
    username: sa
    password: yourpassword
```

### 3. Configure MoMo Payment (Optional)
```yaml
momo:
  partner-code: YOUR_PARTNER_CODE
  access-key: YOUR_ACCESS_KEY
  secret-key: YOUR_SECRET_KEY
  endpoint: https://test-payment.momo.vn/v2/gateway/api/create
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/api-docs

## 🔐 Default Users

The application comes with pre-seeded users for testing:

| Email | Password | Role | Description |
|-------|----------|------|-------------|
| admin@lessionprm.com | admin123 | ADMIN | Full system access |
| user@lessionprm.com | user123 | USER | Regular user access |
| instructor@lessionprm.com | instructor123 | ADMIN | Course instructor |

## 🎯 API Endpoints Overview

### Authentication APIs
```
POST /api/auth/register        # User registration
POST /api/auth/login          # User login
POST /api/auth/refresh        # Refresh access token
POST /api/auth/logout         # User logout
GET  /api/auth/verify-email/{token}  # Email verification
POST /api/auth/forgot-password # Password reset request
POST /api/auth/reset-password  # Password reset
```

### Course Management APIs
```
GET    /api/courses           # Get all published courses
GET    /api/courses/{id}      # Get course details
POST   /api/courses           # Create course (Admin)
PUT    /api/courses/{id}      # Update course (Admin)
DELETE /api/courses/{id}      # Delete course (Admin)
POST   /api/courses/{id}/enroll # Enroll in course
GET    /api/courses/my-courses  # Get user's courses
GET    /api/courses/categories  # Get course categories
GET    /api/courses/featured    # Get featured courses
```

### User Management APIs
```
GET    /api/users/profile     # Get current user profile
PUT    /api/users/profile     # Update user profile
GET    /api/users             # Get all users (Admin)
GET    /api/users/{id}        # Get user by ID (Admin)
PUT    /api/users/{id}/status # Update user status (Admin)
DELETE /api/users/{id}        # Delete user (Admin)
```

### Payment APIs (MoMo Integration)
```
POST /api/payment/momo/create     # Create payment
POST /api/payment/momo/callback   # Handle payment callback
GET  /api/payment/momo/status/{orderId} # Get payment status
POST /api/payment/momo/refund     # Process refund
```

### Invoice APIs
```
GET  /api/invoices             # Get user's invoices
GET  /api/invoices/{id}        # Get invoice details
GET  /api/invoices/all         # Get all invoices (Admin)
PUT  /api/invoices/{id}/status # Update invoice status (Admin)
GET  /api/invoices/statistics  # Get invoice statistics (Admin)
```

### Expense Management APIs (Admin Only)
```
GET    /api/expenses           # Get all expenses
POST   /api/expenses           # Create expense
PUT    /api/expenses/{id}      # Update expense
DELETE /api/expenses/{id}      # Delete expense
GET    /api/expenses/categories # Get expense categories
GET    /api/expenses/monthly-total # Get monthly total
GET    /api/expenses/by-category   # Get expenses by category
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_USERNAME=sa
DB_PASSWORD=yourpassword

# JWT
JWT_SECRET=your-secret-key

# Email
MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# MoMo Payment
MOMO_PARTNER_CODE=your-partner-code
MOMO_ACCESS_KEY=your-access-key
MOMO_SECRET_KEY=your-secret-key
```

### Application Profiles
- `default`: Development profile with SQL Server
- `test`: Testing profile with H2 in-memory database

## 🧪 Testing

Run tests with:
```bash
mvn test
```

The test suite includes:
- Application context loading
- Basic integration tests
- Sample data seeding verification

## 🔄 MoMo Payment Integration

The system includes complete MoMo payment integration with:

1. **Payment Creation**: Generate signed payment requests
2. **Callback Handling**: Verify and process payment results
3. **Status Tracking**: Real-time payment status updates
4. **Signature Verification**: Secure callback validation
5. **Course Enrollment**: Automatic enrollment on successful payment

### MoMo Payment Flow
1. User initiates course purchase
2. System creates invoice and payment record
3. MoMo payment request generated with signature
4. User completes payment on MoMo platform
5. MoMo sends callback with payment result
6. System verifies signature and updates status
7. User automatically enrolled in course on success

## 📊 Sample Data

The application automatically seeds sample data including:
- **Users**: Admin, regular user, and instructor accounts
- **Courses**: 6 sample courses across different categories
- **Categories**: Programming, Web Development, Mobile Development, Data Science, Marketing, Design

## 🐳 Docker Support

Create a `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
mvn clean package
docker build -t lessionprm-backend .
docker run -p 8080:8080 lessionprm-backend
```

## 🚀 Production Deployment

1. **Build the application**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Configure production database and secrets**

3. **Run with production profile**:
   ```bash
   java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```

## 📝 Development Notes

### Project Structure
```
src/main/java/com/lessionprm/backend/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Exception handling
├── repository/      # Data access layer
├── security/        # Security configuration
├── service/         # Business logic
└── util/           # Utility classes
```

### Key Features Implementation
- **JWT Security**: Custom JWT token provider with refresh tokens
- **Global Exception Handling**: Centralized error handling with proper HTTP status codes
- **Data Validation**: Bean Validation with custom validation messages
- **Audit Trail**: Automatic createdAt/updatedAt timestamps
- **CORS Configuration**: Production-ready CORS setup
- **Pagination**: Consistent pagination across all list endpoints

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- **Email**: support@lessionprm.com
- **Documentation**: http://localhost:8080/swagger-ui.html
- **Issues**: [GitHub Issues](https://github.com/DiepTranCFPT/LessionPRM-Backend/issues)

## 🔄 Version History

- **v1.0.0**: Initial release with complete LMS functionality and MoMo integration

---

**Built with ❤️ for education and learning**
=======
- SQL Server (local or remote)
- MoMo API credentials (for payment processing)

## ⚙️ Configuration

### Database Configuration

Update `application.yml` with your SQL Server credentials:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=LessionPRM;encrypt=false;trustServerCertificate=true
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Environment Variables

Set the following environment variables or update `application.yml`:

```bash
# Database
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key_here_minimum_32_characters

# MoMo Payment
MOMO_PARTNER_CODE=your_momo_partner_code
MOMO_ACCESS_KEY=your_momo_access_key
MOMO_SECRET_KEY=your_momo_secret_key
MOMO_ENDPOINT=https://test-payment.momo.vn/v2/gateway/api/create
MOMO_REDIRECT_URL=http://localhost:3000/payment/success
MOMO_NOTIFY_URL=http://localhost:8080/api/payment/momo/callback

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/DiepTranCFPT/LessionPRM-Backend.git
cd LessionPRM-Backend
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access API Documentation

Once the application is running, visit:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- API Docs: `http://localhost:8080/api/v3/api-docs`

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - User logout

### Courses (Public)
- `GET /api/courses` - Get all active courses
- `GET /api/courses/{id}` - Get course by ID
- `GET /api/courses/search?keyword=` - Search courses
- `GET /api/courses/category/{category}` - Get courses by category
- `GET /api/courses/categories` - Get all categories

### Courses (Admin Only)
- `POST /api/courses` - Create new course
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course
- `PUT /api/courses/{id}/activate` - Activate course
- `PUT /api/courses/{id}/deactivate` - Deactivate course

### Users
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile
- `PUT /api/users/change-password` - Change password

### Users (Admin Only)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `PUT /api/users/{id}/enable` - Enable user
- `PUT /api/users/{id}/disable` - Disable user

### Invoices
- `GET /api/invoices` - Get user's invoices
- `GET /api/invoices/{id}` - Get invoice by ID
- `POST /api/invoices` - Create new invoice
- `PUT /api/invoices/{id}/cancelled` - Cancel invoice

### Invoices (Admin Only)
- `GET /api/invoices/all` - Get all invoices
- `GET /api/invoices/statistics` - Get invoice statistics
- `PUT /api/invoices/{id}/paid` - Mark invoice as paid
- `PUT /api/invoices/{id}/failed` - Mark invoice as failed

### Payment
- `POST /api/payments/create` - Create MoMo payment
- `POST /api/payments/callback` - MoMo payment callback
- `GET /api/payments/{orderId}/status` - Get payment status
- `POST /api/payments/{orderId}/refund` - Process refund

### Expenses (Admin Only)
- `GET /api/expenses` - Get all expenses
- `POST /api/expenses` - Create expense
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense
- `PUT /api/expenses/{id}/approve` - Approve expense
- `GET /api/expenses/categories` - Get expense categories
- `GET /api/expenses/totals` - Get expense totals

### Statistics (Admin Only)
- `GET /api/statistics/dashboard` - Dashboard statistics
- `GET /api/statistics/revenue` - Revenue analytics
- `GET /api/statistics/courses` - Course statistics
- `GET /api/statistics/users` - User statistics
- `GET /api/statistics/financial` - Financial overview

### Health Monitoring
- `GET /api/health` - Basic health check
- `GET /api/health/detailed` - Detailed health status
- `GET /api/actuator/health` - Spring Boot actuator health

## 🔐 Security

### JWT Authentication
- Access tokens expire in 24 hours
- Refresh tokens expire in 7 days
- Tokens are blacklisted on logout
- Role-based authorization (USER, ADMIN)

### Password Security
- Passwords are encrypted using BCrypt
- Minimum password length: 6 characters

### API Security
- CORS configuration for frontend integration
- Input validation on all endpoints
- Global exception handling

## 🗄️ Database Schema

### Main Entities
- **Users**: User accounts with roles and profile information
- **Courses**: Course catalog with pricing and enrollment tracking
- **Invoices**: Payment records and transaction history
- **Expenses**: Business expense tracking (Admin only)
- **Revenues**: Monthly revenue analytics (Admin only)

### Relationships
- Users (1:N) Invoices
- Courses (1:N) Invoices
- Proper foreign key constraints and indexing

## 💳 Payment Integration

### MoMo Payment Gateway
- Secure payment processing with HMAC-SHA256 signatures
- Automatic callback handling for payment status updates
- Support for payment status tracking
- Invoice management integration

### Payment Flow
1. User selects course and initiates payment
2. System creates invoice with pending status
3. MoMo payment URL is generated and returned
4. User completes payment on MoMo platform
5. MoMo sends callback to update payment status
6. System updates invoice and enrollment status

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
- Unit tests for service layer
- Integration tests for controllers
- Security tests for authentication

## 📊 Monitoring and Analytics

### Available Statistics
- User registration trends
- Course enrollment statistics
- Revenue and expense tracking
- Payment method analytics
- Monthly/yearly financial summaries

## 🐳 Docker Support (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY target/lession-prm-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📞 Support

For support, please contact [your-email@example.com](mailto:your-email@example.com) or create an issue in the repository.

## 🔄 Version History

- **v1.0.0** - Initial release with core functionality
  - User authentication and authorization
  - Course management system
  - MoMo payment integration
  - Invoice and analytics system
