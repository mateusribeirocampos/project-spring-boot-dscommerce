# DSCommerce - E-Commerce REST API

A professional-grade e-commerce REST API built with Spring Boot, featuring comprehensive authentication, authorization, and complete CRUD operations for products, categories, orders, and users.

Developed as part of the **Spring Boot Professional Course** by **Prof. Nelio Alves** at [DevSuperior](https://devsuperior.com.br/).

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Key Features](#key-features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Authentication & Authorization](#authentication--authorization)
- [Database](#database)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Author](#author)

## 🎯 About the Project

DSCommerce is a complete e-commerce backend application demonstrating enterprise-level practices in Spring Boot development. The application implements a full REST API with OAuth2 authentication, JWT tokens, role-based access control, and comprehensive business logic for managing products, categories, orders, and users.

### What Makes This Project Professional

- **Clean Architecture**: Well-organized layered architecture (Controller → Service → Repository)
- **Security First**: OAuth2 Authorization Server with JWT tokens and RSA encryption
- **Role-Based Access Control**: Granular permissions using Spring Security's @PreAuthorize
- **Data Validation**: Bean Validation with custom error messages
- **Exception Handling**: Centralized exception handling with proper HTTP status codes
- **Complex Relationships**: Multiple JPA relationships (OneToMany, ManyToMany, composite keys)
- **Best Practices**: DTOs, transactional services, pagination, and proper logging

## 🚀 Technologies

### Core Stack

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.4.3** - Latest Spring Boot framework
- **Spring Data JPA** - Database abstraction and ORM
- **Spring Security** - Authentication and authorization
- **Spring Validation** - Bean validation with Hibernate Validator
- **H2 Database** - In-memory database for development/testing

### Security & Authentication

- **Spring Security OAuth2 Authorization Server** - OAuth2 provider implementation
- **Spring Security OAuth2 Resource Server** - JWT validation
- **JWT with RSA** - Secure token-based authentication
- **BCrypt** - Password encryption

### Development Tools

- **Maven** - Dependency management and build tool
- **Spring DevTools** - Hot reload during development
- **H2 Console** - In-browser database viewer

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                         │
│                    (Postman, Frontend, etc.)                │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                        │
│  - ProductController      - CategoryController              │
│  - OrderController        - UserController                  │
│  (REST endpoints, @PreAuthorize, @Valid)                    │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                          │
│  - ProductService         - CategoryService                 │
│  - OrderService           - UserService                     │
│  - AuthService                                              │
│  (Business logic, @Transactional, authorization)            │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                         │
│  - ProductRepository      - CategoryRepository              │
│  - OrderRepository        - UserRepository                  │
│  (JPA repositories, custom queries)                         │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                         │
│                    (H2 In-Memory Database)                  │
└─────────────────────────────────────────────────────────────┘
```

### Security Architecture

```
┌──────────────┐                    ┌─────────────────────────┐
│   Client     │──── Login ────────▶│ Authorization Server    │
│              │                    │ (OAuth2 + JWT)          │
└──────────────┘                    └─────────────────────────┘
       │                                       │
       │                                       ▼
       │                            ┌─────────────────────────┐
       │                            │   Generate JWT Token    │
       │                            │   (RSA signed)          │
       │                            └─────────────────────────┘
       │                                       │
       │◀──────── JWT Token ────────────────────┘
       │
       │
       ├──── Authenticated Request (with JWT) ────────┐
       │                                              │
       ▼                                              ▼
┌─────────────────────────────┐          ┌──────────────────────┐
│   Resource Server           │          │  @PreAuthorize       │
│   (Validates JWT)           │          │  (Checks roles)      │
└─────────────────────────────┘          └──────────────────────┘
```

## ✨ Key Features

### 🔐 Security Features

- **OAuth2 Password Grant**: Custom password grant implementation
- **JWT Tokens**: Stateless authentication with 24-hour validity
- **RSA Encryption**: Public/private key encryption for JWT
- **Role-Based Authorization**: CLIENT and ADMIN roles with different permissions
- **Method-Level Security**: @PreAuthorize annotations on endpoints
- **Custom Authorization Logic**: Self-or-admin pattern for resource access

### 📦 Product Management

- Full CRUD operations for products
- Pagination and filtering by name
- Multiple categories per product (many-to-many)
- Bean validation (name, description, price, image URL)
- Admin-only modifications

### 📋 Category Management

- Complete category CRUD
- Product-category relationships
- Admin-only modifications

### 🛒 Order Management

- Create and manage orders
- Order items with quantities and prices
- Payment tracking
- Order status management (WAITING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELED)
- **Authorization Logic**:
  - Clients can only view their own orders
  - Admins can view all orders

### 👥 User Management

- User authentication with email/password
- Multiple roles per user
- GET /users/me endpoint for current user info
- Spring Security UserDetails integration

### 🛡️ Exception Handling

- Centralized exception handling with @RestControllerAdvice
- Proper HTTP status codes:
  - 404 NOT FOUND - Resource not found
  - 400 BAD REQUEST - Database integrity violation
  - 422 UNPROCESSABLE ENTITY - Validation errors
  - 403 FORBIDDEN - Access denied

## 📋 Prerequisites

- **JDK 21** or higher
- **Maven 3.8+**
- **Git** (for cloning the repository)
- **Postman or Insomnia** (for API testing)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/dscommerce.git
cd dscommerce
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Optional)

Navigate to: `http://localhost:8080/h2-console`

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## 📡 API Endpoints

### Authentication

#### Login (Get JWT Token)

```http
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
username=maria@gmail.com&
password=123456&
client_id=myclientid&
client_secret=myclientsecret
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 86399
}
```

### Products

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| GET | `/products` | No | - | List all products (paginated, filterable by name) |
| GET | `/products/{id}` | No | - | Get product by ID |
| POST | `/products` | Yes | ADMIN | Create new product |
| PUT | `/products/{id}` | Yes | ADMIN | Update product |
| DELETE | `/products/{id}` | Yes | ADMIN | Delete product |

#### Example: List Products with Pagination

```http
GET /products?page=0&size=12&sort=name,asc&name=computer
```

#### Example: Create Product (ADMIN only)

```http
POST /products
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop with RTX 4070 GPU, 32GB RAM, and 1TB SSD",
  "price": 1999.99,
  "imgUrl": "https://example.com/gaming-laptop.jpg",
  "categories": [
    {"id": 2},
    {"id": 3}
  ]
}
```

### Categories

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| GET | `/categories` | No | - | List all categories |
| GET | `/categories/{id}` | No | - | Get category by ID |
| POST | `/categories` | Yes | ADMIN | Create category |
| PUT | `/categories/{id}` | Yes | ADMIN | Update category |
| DELETE | `/categories/{id}` | Yes | ADMIN | Delete category |

### Orders

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| GET | `/orders` | Yes | ADMIN | List all orders |
| GET | `/orders/{id}` | Yes | CLIENT, ADMIN | Get order by ID* |
| POST | `/orders` | Yes | CLIENT, ADMIN | Create new order |
| PUT | `/orders/{id}` | Yes | ADMIN | Update order |
| DELETE | `/orders/{id}` | Yes | ADMIN | Delete order |

**Authorization Rules for GET /orders/{id}:*
- Clients can only view their own orders
- Admins can view any order

#### Example: Create Order

```http
POST /orders
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

### Users

| Method | Endpoint | Auth Required | Role Required | Description |
|--------|----------|---------------|---------------|-------------|
| GET | `/users/me` | Yes | CLIENT, ADMIN | Get current authenticated user |

#### Example: Get Current User

```http
GET /users/me
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "id": 1,
  "name": "Maria Brown",
  "email": "maria@gmail.com",
  "phone": "977777777",
  "birthDate": "2001-07-25",
  "roles": ["ROLE_CLIENT"]
}
```

## 🔐 Authentication & Authorization

### User Roles

- **ROLE_CLIENT**: Regular customer
  - Can view products and categories
  - Can create orders
  - Can view only their own orders
  - Can view their own profile

- **ROLE_ADMIN**: Administrator
  - All CLIENT permissions
  - Can create/update/delete products
  - Can create/update/delete categories
  - Can view all orders
  - Can update/delete orders

### Test Users

**Client User:**
- Email: `maria@gmail.com`
- Password: `123456`
- Role: CLIENT

**Admin User:**
- Email: `alex@gmail.com`
- Password: `123456`
- Roles: CLIENT + ADMIN

### Authorization Examples

#### Scenario 1: Client tries to create a product (DENIED)

```http
POST /products
Authorization: Bearer {client_jwt_token}

Response: 403 Forbidden
{
  "timestamp": "2025-12-17T20:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/products"
}
```

#### Scenario 2: Admin creates a product (ALLOWED)

```http
POST /products
Authorization: Bearer {admin_jwt_token}

Response: 201 Created
```

#### Scenario 3: Client tries to view another client's order (DENIED)

```http
GET /orders/2
Authorization: Bearer {maria_jwt_token}

Response: 403 Forbidden
{
  "timestamp": "2025-12-17T20:00:00Z",
  "status": 403,
  "error": "Unauthorized",
  "message": "Access denied",
  "path": "/orders/2"
}
```

## 💾 Database

### Database Schema

The application uses an H2 in-memory database with the following entities:

**Main Entities:**
- `tb_user` - User accounts
- `tb_role` - User roles (CLIENT, ADMIN)
- `tb_product` - Product catalog
- `tb_category` - Product categories
- `tb_order` - Customer orders
- `tb_order_item` - Order line items
- `tb_payment` - Order payments

**Relationships:**
- User ↔ Role (Many-to-Many)
- Product ↔ Category (Many-to-Many)
- User → Order (One-to-Many)
- Order → OrderItem (One-to-Many)
- Product → OrderItem (One-to-Many)
- Order → Payment (One-to-One with shared primary key)

### Sample Data

The application comes pre-loaded with:
- 2 users (maria@gmail.com, alex@gmail.com)
- 2 roles (CLIENT, ADMIN)
- 3 categories (Books, Electronics, Computers)
- 25 products
- 3 orders with items
- 2 payments

## 🧪 Testing

### Quick Test Script

1. **Get JWT Token:**
```bash
curl -X POST http://localhost:8080/oauth2/token \
  -u myclientid:myclientsecret \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&username=alex@gmail.com&password=123456"
```

2. **Get Current User:**
```bash
curl http://localhost:8080/users/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

3. **List Products:**
```bash
curl http://localhost:8080/products?page=0&size=5
```

4. **Get Order (requires authentication):**
```bash
curl http://localhost:8080/orders/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Testing Authorization

**Test 1: Client accessing own order (SUCCESS)**
- Login as maria@gmail.com
- GET /orders/1 → 200 OK

**Test 2: Client accessing another client's order (FORBIDDEN)**
- Login as maria@gmail.com
- GET /orders/2 → 403 Forbidden

**Test 3: Admin accessing any order (SUCCESS)**
- Login as alex@gmail.com
- GET /orders/1 → 200 OK
- GET /orders/2 → 200 OK

**Test 4: Client trying to create product (FORBIDDEN)**
- Login as maria@gmail.com
- POST /products → 403 Forbidden

**Test 5: Admin creating product (SUCCESS)**
- Login as alex@gmail.com
- POST /products → 201 Created

## 📁 Project Structure

```
dscommerce/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── dscommerce/
│   │   │           ├── config/
│   │   │           │   ├── AuthorizationServerConfig.java
│   │   │           │   ├── ResourceServerConfig.java
│   │   │           │   └── customgrant/
│   │   │           │       ├── CustomPasswordAuthenticationConverter.java
│   │   │           │       ├── CustomPasswordAuthenticationProvider.java
│   │   │           │       ├── CustomPasswordAuthenticationToken.java
│   │   │           │       └── CustomUserAuthorities.java
│   │   │           ├── controllers/
│   │   │           │   ├── CategoryController.java
│   │   │           │   ├── OrderController.java
│   │   │           │   ├── ProductController.java
│   │   │           │   ├── UserController.java
│   │   │           │   └── exceptions/
│   │   │           │       ├── StandardError.java
│   │   │           │       └── handler/
│   │   │           │           └── ResourceExceptionHandler.java
│   │   │           ├── dto/
│   │   │           │   ├── CategoryDTO.java
│   │   │           │   ├── ClientDTO.java
│   │   │           │   ├── OrderDTO.java
│   │   │           │   ├── OrderItemDTO.java
│   │   │           │   ├── PaymentDTO.java
│   │   │           │   ├── ProductDTO.java
│   │   │           │   ├── UserDTO.java
│   │   │           │   └── exceptions/
│   │   │           │       ├── CustomError.java
│   │   │           │       ├── FieldMessage.java
│   │   │           │       └── ValidationError.java
│   │   │           ├── entities/
│   │   │           │   ├── Category.java
│   │   │           │   ├── Order.java
│   │   │           │   ├── OrderItem.java
│   │   │           │   ├── Payment.java
│   │   │           │   ├── Product.java
│   │   │           │   ├── Role.java
│   │   │           │   ├── User.java
│   │   │           │   ├── enums/
│   │   │           │   │   └── OrderStatus.java
│   │   │           │   └── pk/
│   │   │           │       └── OrderItemPk.java
│   │   │           ├── projections/
│   │   │           │   └── UserDetailsProjection.java
│   │   │           ├── repositories/
│   │   │           │   ├── CategoryRepository.java
│   │   │           │   ├── OrderItemRepository.java
│   │   │           │   ├── OrderRepository.java
│   │   │           │   ├── ProductRepository.java
│   │   │           │   └── UserRepository.java
│   │   │           ├── services/
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── CategoryService.java
│   │   │           │   ├── OrderService.java
│   │   │           │   ├── ProductService.java
│   │   │           │   ├── UserService.java
│   │   │           │   └── exceptions/
│   │   │           │       ├── DatabaseException.java
│   │   │           │       ├── ForbiddenException.java
│   │   │           │       └── ResourceNotFoundException.java
│   │   │           └── DscommerceApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-test.yaml
│   │       └── import.sql
│   └── test/
│       └── java/
└── pom.xml
```

## 👨‍💻 Author

**Mateus Campos**
- GitHub: [@mateusribeirocampos](https://github.com/mateusribeirocampos)
- LinkedIn: [Mateus Ribeiro de Campos](https://www.linkedin.com/in/mateus-ribeiro-de-campos-6a135331/)

## 🎓 Credits

This project was developed as part of the **Spring Boot Professional Course** by **Professor Nelio Alves** at [DevSuperior](https://devsuperior.com.br/).

The course covers professional-level Spring Boot development, including:
- RESTful API design
- Spring Security with OAuth2 and JWT
- JPA/Hibernate advanced topics
- Clean Architecture
- Exception handling
- Bean Validation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

⭐ If you found this project helpful, please give it a star!
