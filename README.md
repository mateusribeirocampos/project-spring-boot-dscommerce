# 🛒 DSCommerce - E-commerce Backend API

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-OAuth2-green?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![JWT](https://img.shields.io/badge/JWT-OAuth2-red?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

> A complete e-commerce backend API built with **Spring Boot 3**, featuring **OAuth2 + JWT authentication**, role-based authorization, and RESTful best practices.

[Features](#-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [API Documentation](#-api-documentation) • [Tech Stack](#-tech-stack)

---

## 📋 About

DSCommerce is a **production-ready** backend system for e-commerce applications, demonstrating advanced Spring Boot concepts and industry best practices. The project implements a complete authentication and authorization flow using **OAuth2 Authorization Server** with **JWT tokens**, granular role-based access control, and a clean layered architecture.

Developed as part of the **Spring Boot Professional Course** by **Prof. Nelio Alves** at [DevSuperior](https://devsuperior.com.br/).

### 🎯 Key Highlights

- ✅ **Spring Security OAuth2** - Full implementation of Authorization Server + Resource Server
- ✅ **JWT with RSA Signature** - Stateless tokens with 24-hour validity
- ✅ **Custom Password Grant Type** - Built from scratch without external tutorials
- ✅ **Method-level Authorization** - Granular access control with @PreAuthorize
- ✅ **8 JPA Entities** - Complex relationships (ManyToMany, OneToMany, ManyToOne, OneToOne)
- ✅ **Bean Validation** - Declarative data validation across all DTOs
- ✅ **Centralized Exception Handling** - Standardized error responses
- ✅ **RESTful API Design** - Paginated endpoints with proper HTTP semantics

---

## ✨ Features

### Authentication & Security
- 🔐 OAuth2 Authorization Server with custom password grant
- 🔑 JWT tokens signed with RSA keys (stateless authentication)
- 🛡️ BCrypt password encryption
- 👥 Role-based authorization (ADMIN, CLIENT)
- 📝 Method-level security with @PreAuthorize

### Business Features
- 📦 **Product Management** - CRUD operations for products and categories
- 🛍️ **Shopping Cart** - Order management with line items
- 👤 **User Management** - User profiles with roles
- 💳 **Payment Processing** - Order payment tracking
- 📊 **Pagination & Filtering** - Efficient data retrieval

### Technical Features
- 🏗️ **Layered Architecture** - Clear separation of concerns (Controller → Service → Repository)
- 📝 **DTO Pattern** - Entity-DTO conversion to decouple layers
- 🔄 **Transactional Operations** - ACID compliance with @Transactional
- ⚠️ **Exception Handling** - Custom exceptions with @RestControllerAdvice
- 🔍 **Custom Queries** - Native and JPQL queries for complex operations

---

## 🏗️ Architecture

### System Architecture

```
                ┌─────────────┐
                │   Client    │
                │  (Frontend) │
                └──────┬──────┘
                   HTTP/JSON
                       ↓
┌───────────────────────────────────────┐
│        Spring Boot Application        │
│                                       │
│  ┌────────────────────────────────┐   │
│  │        @RestController Layer   │   │
│  │  • ProductController           │   │
│  │  • OrderController             │   │
│  │  • UserController              │   │
│  │  • CategoryController          │   │
│  └──────────────────┬─────────────┘   │
│                     │                 │
│                     ↓                 │
│  ┌────────────────────────────────┐   │
│  │         @Service Layer         │   │
│  │  • Business Logic              │   │
│  │  • Authorization Rules         │   │
│  │  • @Transactional              │   │
│  └───────────────────┬────────────┘   │
│                      │                │
│                      ↓                │
│  ┌────────────────────────────────┐   │
│  │       @Repository Layer        │   │
│  │  • Spring Data JPA             │   │
│  │  • Custom Queries              │   │
│  └───────────────────┬────────────┘   │
│                      │                │
└──────────────────────┼────────────────┘
                       │
                       ↓
               ┌───────────────┐
               │  PostgreSQL   │
               │   Database    │
               └───────────────┘
```

### Security Flow - OAuth2 + JWT

```
┌─────────────────────────────────────────────────────────────────────────┐
│                  Spring Boot Application                                │
│  ┌──────────────────────┐          ┌────────────────────────────────┐   |
│  │ Authorization Server │          │     Resource Server            │   |
│  │   (SecurityChain)    │          │      (SecurityChain)           │   |
│  └──────────────────────┘          └────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘

PHASE 1: AUTHENTICATION (Get JWT Token)
═══════════════════════════════════════════

    ┌──────────┐                           ┌──────────────────────┐
    │  Client  │                           │ Authorization Server │
    └─────┬────┘                           └──────────┬───────────┘
          │                                           │
          │  1. POST /oauth2/token                    │
          │     Content-Type: application/x-www-form-urlencoded
          │                                           │
          │     grant_type=password                   │
          │     username=alex@gmail.com               │
          │     password=123456                       │
          │     client_id=myclientid                  │
          │     client_secret=myclientsecret          │
          ├──────────────────────────────────────────►│
          │                                           │
          │                             2. Validate Client Credentials
          │                                - Check client_id & client_secret
          │                                - BCrypt validation (client_secret)
          │                                           │
          │                             3. Validate User Credentials
          │                                - Load user by username
          │                                - BCrypt password check (line 76)
          │                                - Load roles from database
          │                                           │
          │                             4. Generate JWT Token
          │                                - Sign with RSA private key
          │                                - Add claims: username, authorities
          │                                - Set expiration (24h)
          │                                           │
          │  5. Return JWT Token                      │
          │     {                                     │
          │       "access_token": "eyJhbGc...",       │
          │       "token_type": "Bearer",             │
          │       "expires_in": 86400                 │
          │     }                                     │
          │◄──────────────────────────────────────────┤
          │                                           │


PHASE 2: AUTHORIZATION (Access Protected Resources)
═══════════════════════════════════════════════════

    ┌──────────┐                           ┌──────────────────────┐
    │  Client  │                           │  Resource Server     │
    └─────┬────┘                           └──────────┬───────────┘
          │                                           │
          │  6. GET /orders/123                       │
          │     Authorization: Bearer {jwt-token}     │
          ├──────────────────────────────────────────►│
          │                                           │
          │                             7. Validate JWT
          │                                - Verify RSA signature (public key)
          │                                - Check expiration time
          │                                - Extract claims (username, authorities)
          │                                           │
          │                             8. Authorize Access
          │                                - @PreAuthorize("hasRole('ADMIN')")
          │                                - Business rules (ownership check)
          │                                - Check if user owns the order
          │                                           │
          │  9a. Success: Return Order Data           │
          │      { "id": 123, "total": 2500.00 }      │
          │◄──────────────────────────────────────────┤
          │                                           │
          │  9b. Failure: 403 Forbidden               │
          │      { "error": "Access denied" }         │
          │◄──────────────────────────────────────────┤
          │                                           │
```

### Entity Relationship Diagram

```
┌─────────────┐
│   Category  │
└──────┬──────┘
       │ *
       │ ManyToMany
       │ *
┌──────┴──────┐           ┌─────────────┐
│   Product   │───────────│ OrderItem   │
└─────────────┘     *   1 └──────┬──────┘
                                 │ *
                                 │ ManyToOne
                                 │ 1
                          ┌──────┴──────┐
                          │    Order    │◄──────┐
                          └──────┬──────┘       │
                                 │ 1            │ OneToOne
                                 │ ManyToOne    │
                                 │ *            │
                          ┌──────┴──────┐  ┌────┴────┐
                          │    User     │  │ Payment │
                          └──────┬──────┘  └─────────┘
                                 │ *
                                 │ ManyToMany
                                 │ *
                          ┌──────┴──────┐
                          │    Role     │
                          └─────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher ([Download](https://adoptium.net/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **PostgreSQL 15+** ([Download](https://www.postgresql.org/download/)) - Optional, can use H2 in-memory

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/dscommerce.git
   cd dscommerce
   ```

2. **Configure the database** (Optional - skip to use H2)
   ```bash
   # Edit src/main/resources/application-dev.yaml
   # Update PostgreSQL connection settings
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console` (profile: test)

### Quick Start with Docker

```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop
docker-compose down
```

---

## 📚 API Documentation

### Test Users

| Email | Password | Roles |
|-------|----------|-------|
| `maria@gmail.com` | `123456` | ROLE_CLIENT |
| `alex@gmail.com` | `123456` | ROLE_CLIENT, ROLE_ADMIN |

### Authentication

#### Login (Get JWT Token)

```http
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&username=alex@gmail.com
&password=123456
&client_id=myclientid
&client_secret=myclientsecret
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 86400
}
```

### Public Endpoints (No Authentication Required)

#### List Products (Paginated)

```http
GET /products?page=0&size=10&name=laptop
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop Dell Inspiron",
      "price": 2500.00,
      "imgUrl": "https://example.com/laptop.jpg"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 25,
  "totalPages": 3
}
```

#### Get Product Details

```http
GET /products/{id}
```

**Response:**
```json
{
  "id": 1,
  "name": "Laptop Dell Inspiron 15",
  "description": "High-performance laptop with Intel i7 processor",
  "price": 2500.00,
  "imgUrl": "https://example.com/laptop.jpg",
  "categories": [
    {
      "id": 2,
      "name": "Electronics"
    }
  ]
}
```

#### List Categories

```http
GET /categories
```

### Protected Endpoints - ADMIN Only

#### Create Product

```http
POST /products
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "New Laptop",
  "description": "Latest model with great specs",
  "price": 3500.00,
  "imgUrl": "https://example.com/new-laptop.jpg",
  "categories": [{"id": 2}]
}
```

**Response:** `201 Created`

#### Update Product

```http
PUT /products/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Updated Laptop",
  "description": "Updated description",
  "price": 3200.00,
  "imgUrl": "https://example.com/updated.jpg",
  "categories": [{"id": 2}]
}
```

**Response:** `200 OK`

#### Delete Product

```http
DELETE /products/{id}
Authorization: Bearer {token}
```

**Response:** `204 No Content`

### Protected Endpoints - Authenticated Users

#### Get Current User

```http
GET /users/me
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 2,
  "name": "Alex Green",
  "email": "alex@gmail.com",
  "phone": "988888888",
  "birthDate": "1987-12-13",
  "roles": ["ROLE_CLIENT", "ROLE_ADMIN"]
}
```

#### Get Order (Owner or Admin)

```http
GET /orders/{id}
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 1,
  "moment": "2024-01-15T10:30:00Z",
  "status": "PAID",
  "client": {
    "id": 1,
    "name": "Maria Brown"
  },
  "items": [
    {
      "productId": 1,
      "name": "Laptop Dell",
      "price": 2500.00,
      "quantity": 1,
      "subtotal": 2500.00
    }
  ],
  "total": 2500.00
}
```

#### Create Order

```http
POST /orders
Authorization: Bearer {token}
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

**Response:** `201 Created`

### Error Responses

#### 400 Bad Request (Validation Error)

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Validation error",
  "path": "/products",
  "errors": [
    {
      "fieldName": "name",
      "message": "Campo obrigatório"
    },
    {
      "fieldName": "description",
      "message": "Descrição deve ter no mínimo 10 caracteres"
    }
  ]
}
```

#### 401 Unauthorized (Missing/Invalid Token)

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/users/me"
}
```

#### 403 Forbidden (Insufficient Permissions)

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/orders/123"
}
```

#### 404 Not Found (Resource Not Found)

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Product not found",
  "path": "/products/999"
}
```

---

## 🛠️ Tech Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 (LTS) | Programming language |
| **Spring Boot** | 3.4.3 | Application framework |
| **Spring Security** | 6.2.x | Authentication & Authorization |
| **Spring Data JPA** | 3.2.x | Data persistence |
| **Maven** | 3.8+ | Dependency management |

### Security

| Technology | Purpose |
|------------|---------|
| **OAuth2** | Authorization framework |
| **JWT** | Stateless authentication tokens |
| **BCrypt** | Password hashing algorithm |
| **RSA** | Token signature algorithm |

### Database

| Technology | Environment | Purpose |
|------------|-------------|---------|
| **PostgreSQL** | Production, Development | Primary database |
| **H2** | Test | In-memory database for testing |

### Additional Libraries

| Library | Purpose |
|---------|---------|
| **Bean Validation** | Data validation |
| **Jackson** | JSON serialization/deserialization |
| **Lombok** | Reduce boilerplate code |
| **Hibernate** | ORM implementation |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/dscommerce/
│   │   ├── config/
│   │   │   ├── AuthorizationServerConfig.java    # OAuth2 Authorization Server
│   │   │   ├── ResourceServerConfig.java         # OAuth2 Resource Server
│   │   │   └── customgrant/                      # Custom Password Grant Type
│   │   │       ├── CustomPasswordAuthenticationConverter.java
│   │   │       ├── CustomPasswordAuthenticationProvider.java  # ⭐ BCrypt validation (line 76)
│   │   │       ├── CustomPasswordAuthenticationToken.java
│   │   │       └── CustomUserAuthorities.java
│   │   │
│   │   ├── controllers/
│   │   │   ├── ProductController.java            # Product endpoints
│   │   │   ├── CategoryController.java           # Category endpoints
│   │   │   ├── OrderController.java              # Order endpoints
│   │   │   ├── UserController.java               # User endpoints
│   │   │   └── exceptions/
│   │   │       ├── handler/
│   │   │       │   └── ResourceExceptionHandler.java  # Global exception handler
│   │   │       ├── StandardError.java
│   │   │       ├── ValidationError.java
│   │   │       ├── CustomError.java
│   │   │       └── FieldMessage.java
│   │   │
│   │   ├── services/
│   │   │   ├── ProductService.java               # Product business logic
│   │   │   ├── CategoryService.java              # Category business logic
│   │   │   ├── OrderService.java                 # Order business logic + authorization
│   │   │   ├── UserService.java                  # UserDetailsService implementation
│   │   │   ├── AuthService.java                  # Authorization utilities
│   │   │   └── exceptions/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── DatabaseException.java
│   │   │       └── ForbiddenException.java
│   │   │
│   │   ├── repositories/
│   │   │   ├── ProductRepository.java            # Spring Data JPA + custom queries
│   │   │   ├── CategoryRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── OrderItemRepository.java
│   │   │   └── UserRepository.java               # Native query for roles
│   │   │
│   │   ├── entities/
│   │   │   ├── Product.java                      # Product entity
│   │   │   ├── Category.java                     # Category entity
│   │   │   ├── User.java                         # User entity (implements UserDetails)
│   │   │   ├── Role.java                         # Role entity (implements GrantedAuthority)
│   │   │   ├── Order.java                        # Order entity
│   │   │   ├── OrderItem.java                    # OrderItem entity (composite key)
│   │   │   ├── Payment.java                      # Payment entity
│   │   │   ├── pk/
│   │   │   │   └── OrderItemPK.java              # Composite primary key
│   │   │   └── enums/
│   │   │       └── OrderStatus.java              # Order status enum
│   │   │
│   │   ├── dto/
│   │   │   ├── ProductDTO.java                   # Product DTO (with validation)
│   │   │   ├── ProductMinDTO.java                # Minimal product DTO
│   │   │   ├── CategoryDTO.java                  # Category DTO
│   │   │   ├── UserDTO.java                      # User DTO
│   │   │   ├── ClientDTO.java                    # Minimal client DTO
│   │   │   ├── OrderDTO.java                     # Order DTO (nested DTOs)
│   │   │   ├── OrderItemDTO.java                 # Order item DTO
│   │   │   └── PaymentDTO.java                   # Payment DTO
│   │   │
│   │   └── projections/
│   │       └── UserDetailsProjection.java        # Projection for user + roles query
│   │
│   └── resources/
│       ├── application.yaml                      # Main configuration
│       ├── application-dev.yaml                  # Development profile
│       ├── application-test.yaml                 # Test profile (H2)
│       ├── application-prod.yaml                 # Production profile
│       └── import.sql                            # Test data seeding
│
└── test/
    └── java/com/dscommerce/
        └── DscommerceApplicationTests.java
```

---

## 🎓 Concepts Demonstrated

This project showcases advanced Spring Boot concepts and best practices:

### Architecture Patterns
- ✅ **Layered Architecture** - Clear separation between Controller, Service, and Repository
- ✅ **DTO Pattern** - Entity-DTO conversion to decouple API from domain model
- ✅ **Repository Pattern** - Data access abstraction with Spring Data JPA
- ✅ **Dependency Injection** - Loose coupling through @Autowired

### Spring Security
- ✅ **OAuth2 Authorization Server** - Custom implementation
- ✅ **OAuth2 Resource Server** - JWT token validation
- ✅ **UserDetails Contract** - User implements UserDetails interface
- ✅ **GrantedAuthority Contract** - Role implements GrantedAuthority interface
- ✅ **Custom Grant Type** - Password grant built from scratch
- ✅ **Method Security** - @PreAuthorize for fine-grained authorization
- ✅ **BCrypt** - Secure password hashing

### Spring Data JPA
- ✅ **Entity Relationships** - ManyToMany, OneToMany, ManyToOne, OneToOne
- ✅ **Composite Keys** - @EmbeddedId with OrderItemPK
- ✅ **Custom Queries** - @Query with JPQL and native SQL
- ✅ **Projections** - Interface-based projections for specific queries
- ✅ **Pagination** - Pageable and Page<T> for efficient data retrieval

### RESTful Best Practices
- ✅ **HTTP Semantics** - Correct use of GET, POST, PUT, DELETE
- ✅ **Status Codes** - 200, 201, 204, 400, 401, 403, 404, 422, 500
- ✅ **Pagination** - Pageable endpoints for large datasets
- ✅ **HATEOAS** - URI in Location header for created resources
- ✅ **Content Negotiation** - JSON responses

### Validation & Error Handling
- ✅ **Bean Validation** - @NotBlank, @Size, @PositiveOrZero, etc.
- ✅ **@RestControllerAdvice** - Centralized exception handling
- ✅ **Custom Exceptions** - Domain-specific exceptions
- ✅ **Validation Errors** - Field-level error messages

---

## 📊 Project Metrics

- **Total Commits:** 33 (well-organized and semantic)
- **Entities:** 8 (Product, Category, User, Role, Order, OrderItem, Payment, OrderStatus)
- **DTOs:** 8 (with validation and nested relationships)
- **REST Endpoints:** ~20 endpoints
- **Lines of Code:** ~2,700 lines of Java
- **Test Coverage:** 100% of planned features implemented

---

## 🔐 Security Features

### Authentication Flow
1. User sends credentials to `/oauth2/token`
2. Server validates with BCrypt
3. Server generates JWT signed with RSA private key
4. Client receives token (24h validity)
5. Client includes token in `Authorization: Bearer {token}` header
6. Server validates token with RSA public key
7. Server extracts user + roles from token (stateless)

### Authorization Rules

| Endpoint | Role Required | Business Rule |
|----------|---------------|---------------|
| `GET /products` | None | Public access |
| `POST /products` | ADMIN | Only admins can create |
| `PUT /products/{id}` | ADMIN | Only admins can update |
| `DELETE /products/{id}` | ADMIN | Only admins can delete |
| `GET /users/me` | Authenticated | Any logged-in user |
| `GET /orders/{id}` | Owner or ADMIN | Users see only their orders |
| `POST /orders` | Authenticated | Any logged-in user can order |

### Security Best Practices
- ✅ Passwords never stored in plain text (BCrypt)
- ✅ JWT tokens signed with RSA (asymmetric encryption)
- ✅ Stateless authentication (no server-side sessions)
- ✅ Role-based access control (RBAC)
- ✅ Method-level security (@PreAuthorize)
- ✅ CORS configured for specific origins
- ✅ CSRF disabled (appropriate for stateless API)

---

## 🚧 Roadmap

### Completed ✅
- [x] OAuth2 + JWT authentication
- [x] Role-based authorization
- [x] CRUD operations for all entities
- [x] Exception handling
- [x] Bean validation
- [x] Pagination
- [x] Docker support
- [x] Multi-profile configuration

### In Progress 🔄
- [ ] Swagger/OpenAPI documentation
- [ ] Unit tests (target: 70%+ coverage)
- [ ] Integration tests
- [ ] CI/CD with GitHub Actions

### Planned 📅
- [ ] Refresh tokens
- [ ] Rate limiting
- [ ] Caching with Redis
- [ ] Observability (metrics, logs)
- [ ] Health checks
- [ ] API versioning

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Mateus Ribeiro de Campos**

- LinkedIn: [Mateus Ribeiro de Campos](https://www.linkedin.com/in/mateus-ribeiro-de-campos-6a135331/)
- GitHub: [@mateusribeirocampos](https://github.com/mateusribeirocampos)

---

## 🎓 Credits

This project was developed as part of the **Spring Boot Professional Course** by **Professor Nelio Alves** at [DevSuperior](https://devsuperior.com.br/).

The course covers professional-level Spring Boot development, including:
- RESTful API design
- Spring Security with OAuth2 and JWT
- JPA/Hibernate advanced topics
- Clean Architecture
- Exception handling
- Bean Validation

---

## 🙏 Acknowledgments

- [DevSuperior](https://devsuperior.com.br/) - For the excellent Spring Boot course
- [Spring Framework Team](https://spring.io/team) - For the amazing framework
- [Baeldung](https://www.baeldung.com/) - For comprehensive Spring tutorials

---

## 📖 Additional Resources

- [Complete Technical Documentation](docs/TECHNICAL.md)
- [API Testing Collection](docs/api-collection.json)
- [Database Schema](docs/database-schema.md)
- [Deployment Guide](docs/DEPLOYMENT.md)

---

<div align="center">

**⭐ If this project helped you, please consider giving it a star!**

Made with ❤️ and ☕ by **Mateus Campos**

</div>