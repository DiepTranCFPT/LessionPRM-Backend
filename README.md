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
- `POST /api/payment/momo/create` - Create MoMo payment
- `POST /api/payment/momo/callback` - MoMo payment callback
- `GET /api/payment/momo/status/{orderId}` - Get payment status

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