# DSCommerce - Spring Boot E-commerce Backend

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-Secured-6DB33F?logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-black?logo=jsonwebtokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-InMemory_DB-09476B?logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-Production_DB-3ECF8E?logo=supabase&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-Tests-25A162?logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-Mocking-78A641?logoColor=white)
![Render](https://img.shields.io/badge/Render-Deploy-000000?logo=render&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI/CD-2088FF?logo=githubactions&logoColor=white)
![Resend](https://img.shields.io/badge/Resend-Email_API-black?logo=resend&logoColor=white)

A backend-focused e-commerce API built with Spring Boot, designed to showcase secure authentication and authorization, role-based access control, layered architecture, JPA entity relationships, validation, exception handling, automated tests, and production-oriented deployment.

This is the main backend project in my portfolio and reflects my focus on building secure, maintainable RESTful APIs with Java and Spring Boot.

**Live API:** https://project-spring-boot-dscommerce.onrender.com/
**Frontend demo:** [dscommerce-frontend.vercel.app](https://dscommerce-frontend.vercel.app/) — ([GitHub repo](https://github.com/mateusribeirocampos/dscommerce-frontend))

![DSCommerce](https://img.shields.io/badge/DSCommerce-FF5500?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIj4KICA8Y2lyY2xlIGN4PSIzNSIgY3k9Ijc1IiByPSI4IiBmaWxsPSJ3aGl0ZSIvPgogIDxjaXJjbGUgY3g9IjcwIiBjeT0iNzUiIHI9IjgiIGZpbGw9IndoaXRlIi8+CiAgPHBhdGggZD0iTTIwIDI1aDEwbDggMzVoMzJsMTAtMjVINDIiIGZpbGw9Im5vbmUiIHN0cm9rZT0id2hpdGUiIHN0cm9rZS13aWR0aD0iOCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIi8+Cjwvc3ZnPg==&logoColor=white)

---

## Overview

DSCommerce is a RESTful backend for an e-commerce domain, covering core business entities such as users, roles, products, categories, orders, payments, and order items.

The project emphasizes:

- secure API access with JWT-based authentication
- role-based authorization for protected resources
- layered backend architecture
- JPA domain modeling and entity relationships
- validation and centralized exception handling
- environment-based configuration for local and production usage
- deployment to a cloud environment with PostgreSQL

---

## Key Features

- JWT-based authentication and authorization
- Role-based access control (public, authenticated, and admin routes)
- OAuth2 Authorization Server integration
- Layered architecture with Controllers, Services, and Repositories
- JPA/Hibernate mapping for a relational e-commerce domain
- Validation and consistent error handling
- H2 profile for local/test execution
- PostgreSQL in production
- Deployed API running on Render
- Separate frontend client consuming the backend API

---

## Tech Stack

- Java 21
- Spring Boot 3.4
- Spring Security
- OAuth2 Authorization Server
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- H2
- Maven
- Supabase (production database)
- Render.com (cloud hosting)

---

## Architecture

```mermaid
flowchart LR
    FE[Frontend / API Client] --> SEC[Spring Security]
    SEC --> CTRL[Controllers]
    CTRL --> SVC[Services]
    SVC --> REPO[Repositories]
    REPO --> H2[(H2 - local/test)]
    REPO --> DB[(Supabase PostgreSQL - production)]
```

The project follows a layered backend architecture with clear separation of concerns:

- **Controllers** handle HTTP requests and responses
- **Services** encapsulate business rules and authorization checks
- **Repositories** manage persistence with Spring Data JPA
- **Security** is implemented with Spring Security, OAuth2, and JWT

In production, the API is deployed on Render and connected to a Supabase PostgreSQL database. Locally, the `test` profile uses an H2 in-memory database for simplified execution and testing.

---

## Security Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant AS as Authorization Server
    participant RS as Resource Server

    C->>AS: POST /oauth2/token
    AS->>AS: Validate client credentials
    AS->>AS: Validate user credentials
    AS-->>C: Return JWT access token

    C->>RS: Request with Bearer token
    RS->>RS: Validate JWT
    RS->>RS: Extract username and authorities
    RS-->>C: Return protected resource
```

Authentication is based on JWT access tokens issued by the authorization server.

Authorization combines endpoint-level role checks with business rules such as owner-or-admin access.

---

## Domain Model

```mermaid
erDiagram
    USER ||--o{ ORDER : places
    ORDER ||--o| PAYMENT : has
    ORDER ||--o{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : appears_in
    PRODUCT }o--o{ CATEGORY : belongs_to
    USER }o--o{ ROLE : has
```

---

## API Overview

### Public Endpoints

- `POST /oauth2/token`
- `GET /products`
- `GET /products/{id}`
- `GET /categories`
- `POST /users/register`

### Authenticated Endpoints

- `GET /users/me`
- `PUT /users/me`
- `GET /orders/{id}`
- `POST /orders`

### Admin Endpoints

- `POST /products`
- `PUT /products/{id}`
- `DELETE /products/{id}`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`
- `GET /orders`

---

## Example Authentication Request

```http
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&username=maria@gmail.com
&password=123456
&client_id=myclientid
&client_secret=myclientsecret
```

---

## Production Notes

- Deployed on [Render](https://render.com)
- Supabase PostgreSQL as production database
- Separate frontend client available at [dscommerce-frontend.vercel.app](https://dscommerce-frontend.vercel.app/) for integration and demo

---

## Running Locally

### Requirements

- Java 21
- Maven 3.8+
- PostgreSQL, if you want to use the `dev` or `prod` profile

### Default Profile

The application uses the `test` profile by default:

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:test}
```

This means the project can start with H2 unless another profile is explicitly selected.

### Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or using Maven directly:

```bash
mvn spring-boot:run
```

### Run with a Specific Profile

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

---

## Testing

The test suite covers three layers:

- **Repository** — `@DataJpaTest` with H2 (query validation)
- **Service** — `@ExtendWith(MockitoExtension.class)` (business logic isolation)
- **Controller** — `@WebMvcTest` (HTTP behavior, validation, exception handling)

Main testing tools used in the project include:

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

The test suite is continuously being refined to improve consistency and maintainability in the current Java 21 environment.

---

## Frontend Integration

A separate frontend repository is available to provide a visual client for the API:

- **Live demo:** [dscommerce-frontend.vercel.app](https://dscommerce-frontend.vercel.app/)
- **GitHub:** [mateusribeirocampos/dscommerce-frontend](https://github.com/mateusribeirocampos/dscommerce-frontend)

The backend remains the main focus of this portfolio project.

---

## Design Notes

- This project prioritizes backend architecture, security, and API design
- The custom password grant flow was kept for learning and portfolio purposes
- Some authentication and token-related decisions were simplified compared to a full enterprise IAM solution
- The frontend is complementary and serves as a client for API interaction

---

## Project Evolution

This project was initially inspired by coursework from the DevSuperior Spring Boot Professional program and later evolved into a broader portfolio backend through additional implementation, integration of new features, documentation improvements, testing efforts, and production deployment.

---

## Author

**Mateus Ribeiro de Campos**  
[![GitHub](https://img.shields.io/badge/GitHub-mateusribeirocampos-181717?logo=github&logoColor=white)](https://github.com/mateusribeirocampos)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Mateus_Ribeiro_de_Campos-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/mateus-ribeiro-de-campos-6a135331/)
[![Portfolio](https://img.shields.io/badge/Portfolio-Visit-111111?logo=vercel&logoColor=white)](https://portfolio-mateusribeirocampos.vercel.app/)