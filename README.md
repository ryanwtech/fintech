# Fintech Backend

A Spring Boot 3 application with JWT authentication, PostgreSQL database, and CORS support.

## Features

- Spring Boot 3 with Java 17
- JWT authentication with HTTP-only cookies
- PostgreSQL database with Flyway migrations
- CORS configuration for frontend integration
- OpenAPI/Swagger documentation
- Spring Security
- JPA/Hibernate with auditing

## Prerequisites

- Java 17
- Maven 3.6+
- PostgreSQL 12+

## Environment Variables

Set the following environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/fintech_db
export DATABASE_USERNAME=fintech_user
export DATABASE_PASSWORD=fintech_password
export JWT_SECRET=your-secret-key-here
```

## Running the Application

1. Create a PostgreSQL database:
```sql
CREATE DATABASE fintech_db;
CREATE USER fintech_user WITH PASSWORD 'fintech_password';
GRANT ALL PRIVILEGES ON DATABASE fintech_db TO fintech_user;
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Access the application:
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health Check: http://localhost:8080/api/health

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login with username/password
- `POST /api/auth/logout` - Logout (clears JWT cookie)

### Health
- `GET /api/health` - Health check endpoint

## CORS Configuration

The application is configured to allow requests from `http://localhost:5173` for frontend integration.

## Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/` and will run automatically on application startup.
