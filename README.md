# Fintech Backend API

A comprehensive fintech backend API built with Spring Boot, featuring banking integration, transaction management, budgeting, reporting, and audit logging capabilities.

## 🚀 Features

### Core Functionality
- **User Management**: Registration, authentication, and profile management
- **Account Management**: Multiple account types with balance tracking
- **Transaction Management**: CRUD operations, CSV import, and filtering
- **Category Management**: User-specific and global categories
- **Budget Management**: Monthly budgets with spending tracking
- **Reports**: Cashflow, spending analysis, and trend reports
- **Rules Engine**: Automatic transaction categorization
- **Bank Integration**: Mock bank connector with webhook support
- **Audit Logging**: Comprehensive audit trails for all operations

### Technical Features
- **RESTful API**: Well-documented REST endpoints
- **JWT Authentication**: Secure token-based authentication
- **Database**: PostgreSQL with Flyway migrations
- **Docker Support**: Containerized deployment
- **API Documentation**: Interactive Swagger UI
- **Testing**: Comprehensive integration tests with Testcontainers
- **Validation**: Bean validation for data integrity
- **CORS Support**: Cross-origin resource sharing enabled

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.4.0**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Flyway** (Database migrations)
- **JWT** (Authentication)
- **SpringDoc OpenAPI 3** (API documentation)
- **Testcontainers** (Integration testing)
- **Docker & Docker Compose**
- **Maven** (Build tool)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL 15 (for local development)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd fintech
   ```

2. **Start the services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/docs
   - pgAdmin: http://localhost:5050 (admin@fintech.com / admin123)

### Option 2: Local Development

1. **Start PostgreSQL**
   ```bash
   # Using Docker
   docker run --name fintech-postgres -e POSTGRES_DB=fintech_db -e POSTGRES_USER=fintech_user -e POSTGRES_PASSWORD=fintech_password -p 5432:5432 -d postgres:15-alpine
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/docs

## 🗄️ Database Setup

### Automatic Setup (Docker Compose)
The database is automatically set up with the following:
- Database: `fintech_db`
- Username: `fintech_user`
- Password: `fintech_password`
- Migrations: Automatically applied via Flyway

### Manual Setup
1. Create a PostgreSQL database
2. Update `application.yml` with your database credentials
3. Run migrations: `mvn flyway:migrate`

## 🔐 Authentication

### Test Credentials
The application comes with pre-seeded test users:

| Username | Email | Password | Role |
|----------|-------|----------|------|
| testuser | test@example.com | password123 | USER |
| adminuser | admin@example.com | password123 | ADMIN |

### JWT Token
- **Secret**: `mySecretKey` (configurable via `JWT_SECRET` environment variable)
- **Expiration**: 24 hours
- **Header**: `Authorization: Bearer <token>`

### Getting a Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

## 📚 API Documentation

### Swagger UI
- **URL**: http://localhost:8080/api/docs
- **Features**:
  - Interactive API testing
  - Request/response examples
  - Authentication support
  - Download OpenAPI specification

### API Endpoints

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile

#### Transactions
- `GET /api/accounts/{id}/transactions` - Get transactions
- `POST /api/accounts/{id}/transactions` - Create transaction
- `PATCH /api/transactions/{id}` - Update transaction
- `POST /api/transactions/import` - Import CSV transactions

#### Categories
- `GET /api/categories` - Get user categories
- `POST /api/categories` - Create category
- `PATCH /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

#### Budgets
- `GET /api/budgets` - Get budgets
- `POST /api/budgets` - Create budget
- `GET /api/budgets/{id}` - Get budget details
- `PATCH /api/budgets/{id}/items/{categoryId}` - Update budget item

#### Reports
- `GET /api/reports/cashflow` - Cashflow report
- `GET /api/reports/spend-by-category` - Spending by category
- `GET /api/reports/trend` - Trend analysis

#### Banking Integration
- `POST /api/integrations/mockbank/link` - Link bank account
- `GET /api/integrations/mockbank/connections` - Get connections
- `DELETE /api/integrations/mockbank/connections/{id}` - Unlink account

#### Webhooks
- `POST /api/webhooks/mockbank` - Receive webhook
- `POST /api/webhooks/test/simulate` - Simulate webhook
- `GET /api/webhooks/events` - Get webhook events

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn test -Dtest="*IntegrationTest"
```

### Run Specific Test Class
```bash
mvn test -Dtest="TransactionServiceIntegrationTest"
```

### Test Coverage
```bash
mvn jacoco:report
```

## 🐳 Docker Commands

### Build and Run
```bash
# Build the application
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Individual Services
```bash
# Start only PostgreSQL
docker-compose up -d postgres

# Start only the backend
docker-compose up -d backend

# Rebuild and restart backend
docker-compose up -d --build backend
```

## 📊 Monitoring and Logs

### Application Logs
```bash
# Docker Compose
docker-compose logs -f backend

# Local development
tail -f logs/application.log
```

### Database Access
- **pgAdmin**: http://localhost:5050
  - Email: admin@fintech.com
  - Password: admin123
- **Direct Connection**: localhost:5432
  - Database: fintech_db
  - Username: fintech_user
  - Password: fintech_password

### Health Check
```bash
curl http://localhost:8080/api/health
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/fintech_db
DATABASE_USERNAME=fintech_user
DATABASE_PASSWORD=fintech_password

# JWT
JWT_SECRET=mySecretKey

# Application
SPRING_PROFILES_ACTIVE=docker
```

### Application Profiles
- `default` - Local development
- `docker` - Docker deployment
- `test` - Testing environment

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/fintech/
│   │   ├── config/          # Configuration classes
│   │   ├── domain/          # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   ├── repo/            # Repository interfaces
│   │   ├── service/         # Business logic
│   │   ├── web/             # REST controllers
│   │   └── cli/             # CLI tools
│   └── resources/
│       ├── application.yml  # Main configuration
│       ├── application-docker.yml
│       └── db/migration/    # Flyway migrations
└── test/
    ├── java/com/fintech/
    │   └── integration/     # Integration tests
    └── resources/
        ├── application-test.yml
        └── test-data.sql    # Test data
```

## 🚀 Deployment

### Production Considerations
1. **Environment Variables**: Set production database credentials
2. **JWT Secret**: Use a strong, random JWT secret
3. **CORS**: Configure allowed origins for production
4. **Logging**: Configure appropriate log levels
5. **Database**: Use managed PostgreSQL service
6. **Monitoring**: Set up application monitoring
7. **Security**: Enable HTTPS and security headers

### Docker Production Build
```bash
# Build production image
docker build -t fintech-backend:latest .

# Run production container
docker run -d \
  --name fintech-backend \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://your-db:5432/fintech_db \
  -e DATABASE_USERNAME=your-username \
  -e DATABASE_PASSWORD=your-password \
  -e JWT_SECRET=your-secret \
  fintech-backend:latest
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation at http://localhost:8080/api/docs
- Review the logs for error details

## 🔄 API Examples

### Create a Transaction
```bash
curl -X POST http://localhost:8080/api/accounts/33333333-3333-3333-3333-333333333333/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{
    "amount": -25.50,
    "description": "Coffee purchase",
    "merchant": "Starbucks",
    "postedAt": "2024-01-15T09:30:00",
    "categoryId": "66666666-6666-6666-6666-666666666666"
  }'
```

### Import CSV Transactions
```bash
curl -X POST http://localhost:8080/api/transactions/import \
  -H "Authorization: Bearer <your-token>" \
  -F "file=@transactions.csv"
```

### Get Cashflow Report
```bash
curl -X GET "http://localhost:8080/api/reports/cashflow?from=2024-01-01&to=2024-01-31" \
  -H "Authorization: Bearer <your-token>"
```

### Simulate Webhook
```bash
curl -X POST http://localhost:8080/api/webhooks/test/simulate \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "transactions.new",
    "accountId": "ext_account_123",
    "transactions": [
      {
        "transactionId": "ext_txn_001",
        "amount": -50.00,
        "description": "Test transaction",
        "merchant": "Test Merchant",
        "postedAt": "2024-01-15T09:30:00",
        "currency": "USD",
        "category": "Food",
        "status": "cleared"
      }
    ]
  }'
```

---

**Happy coding! 🎉**