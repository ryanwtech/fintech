# Fintech Personal Finance Management

[![Backend CI](https://github.com/yourusername/fintech/actions/workflows/backend.yml/badge.svg)](https://github.com/yourusername/fintech/actions/workflows/backend.yml)
[![Frontend CI](https://github.com/yourusername/fintech/actions/workflows/frontend.yml/badge.svg)](https://github.com/yourusername/fintech/actions/workflows/frontend.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-blue.svg)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)

A comprehensive personal finance management application built with Spring Boot and React. Track your accounts, transactions, budgets, and financial goals with powerful reporting and automation features.

## 🚀 Features

### 💰 **Account Management**
- Multiple account types (Checking, Savings, Credit, Investment)
- Real-time balance tracking
- Account linking and management
- Transaction categorization

### 📊 **Transaction Management**
- Global transaction view with advanced filtering
- Inline editing and bulk operations
- CSV import/export functionality
- Automatic categorization with rules
- Search and filter capabilities

### 📈 **Budgeting & Planning**
- Monthly budget creation and tracking
- Per-category budget allocation
- Visual progress bars and status indicators
- Over/under budget alerts
- Month-by-month budget switching

### 📋 **Reports & Analytics**
- Interactive charts with Recharts
- Cashflow analysis with area charts
- Spending by category with pie charts
- Monthly trends with line charts
- Exportable reports in CSV format

### 🔧 **Automation & Rules**
- Smart transaction categorization
- Pattern-based rule creation
- Rule testing and validation
- Priority-based rule processing
- Drag-and-drop rule reordering

### 🏦 **Bank Integration**
- Mock bank connector for testing
- Webhook simulation and testing
- Real-time transaction import
- Connection status monitoring

### 🎨 **User Experience**
- Dark mode support
- Responsive design
- Error boundaries and 404 pages
- Toast notifications
- Skeleton loaders
- Optimistic updates

## 🛠️ Tech Stack

### Backend
- **Java 17** - Modern Java features
- **Spring Boot 3.4.0** - Rapid application development
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL 15** - Relational database
- **Flyway** - Database migrations
- **JWT** - Token-based authentication
- **Testcontainers** - Integration testing
- **Maven** - Dependency management

### Frontend
- **React 18** - User interface library
- **TypeScript 5.0** - Type safety
- **Vite** - Build tool and dev server
- **TanStack Query** - Server state management
- **React Router** - Client-side routing
- **React Hook Form** - Form management
- **Zod** - Schema validation
- **Recharts** - Data visualization
- **Tailwind CSS** - Utility-first styling
- **Lucide React** - Icon library
- **Zustand** - Client state management

### DevOps & Tools
- **Docker & Docker Compose** - Containerization
- **GitHub Actions** - CI/CD pipeline
- **Lighthouse CI** - Performance monitoring
- **OWASP Dependency Check** - Security scanning
- **pnpm** - Fast package manager

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

## 🔑 Default Credentials

| Username    | Email             | Password    | Role  |
|-------------|-------------------|-------------|-------|
| `testuser`  | `test@example.com`| `password123` | USER  |
| `adminuser` | `admin@example.com`| `password123` | ADMIN |

## 📚 API Documentation

The API is fully documented with OpenAPI/Swagger UI available at `/api/docs` when running the application.

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
mvn test

# Run integration tests
mvn test -Dtest="*IntegrationTest"

# Run with coverage
mvn test jacoco:report
```

### Frontend Tests
```bash
cd fintech-frontend

# Run unit tests
pnpm test

# Run tests with coverage
pnpm test:coverage

# Run E2E tests
pnpm test:e2e
```

## 🐳 Docker Deployment

### Development
```bash
docker-compose up --build -d
```

### Production
```bash
# Build production images
docker build -t fintech-backend .
docker build -t fintech-frontend ./fintech-frontend

# Run with production compose
docker-compose -f docker-compose.prod.yml up -d
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/fintech_db
DATABASE_USERNAME=fintech_user
DATABASE_PASSWORD=fintech_password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Frontend
VITE_API_URL=http://localhost:8080/api
```

### Application Profiles
- `dev` - Development with H2 database
- `docker` - Docker environment
- `test` - Test environment with Testcontainers
- `prod` - Production environment

## 📊 Monitoring & Observability

- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Audit Logs**: Comprehensive audit trail
- **Error Tracking**: Centralized error handling
- **Performance**: Lighthouse CI integration

## 🔒 Security Features

- JWT-based authentication
- CORS configuration
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CSRF protection
- Rate limiting (configurable)
- Audit logging for all operations

---

Made with ❤️ by the Fintech Team