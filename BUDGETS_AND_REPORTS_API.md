# Budgets and Reports API Documentation

## Overview
The Budgets and Reports API provides comprehensive financial planning and analytics capabilities for your fintech application. It includes monthly budget management, spending analysis, and detailed financial reports with chart-ready data.

## Features
- **Monthly Budgets**: One budget per month per user with category-based budget items
- **Spending Analysis**: Real-time calculation of spent amounts vs. planned amounts
- **Financial Reports**: Cashflow, spending by category, and trend analysis
- **Chart-Ready Data**: DTOs optimized for frontend chart libraries
- **Performance Optimized**: SQL queries with strategic indexes for fast data retrieval
- **Audit Logging**: Complete audit trail of budget changes

## Budgets API

### 1. Get Budget by Month
```
GET /api/budgets?userId={userId}&month=YYYY-MM
```

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "123e4567-e89b-12d3-a456-426614174001",
  "name": "Budget for 2024-01",
  "description": "Monthly budget for January 2024",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "totalAmount": 3000.00,
  "spentAmount": 1250.50,
  "remainingAmount": 1749.50,
  "isActive": true,
  "items": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174002",
      "budgetId": "123e4567-e89b-12d3-a456-426614174000",
      "categoryId": "123e4567-e89b-12d3-a456-426614174003",
      "categoryName": "Food",
      "plannedAmount": 800.00,
      "actualAmount": 650.50,
      "remainingAmount": 149.50,
      "spentPercentage": 81.31
    }
  ],
  "createdAt": "2024-01-01",
  "updatedAt": "2024-01-15"
}
```

### 2. Create Budget
```
POST /api/budgets?userId={userId}
```

**Request Body:**
```json
{
  "name": "January 2024 Budget",
  "description": "Monthly budget for January 2024",
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "totalAmount": 3000.00,
  "items": [
    {
      "categoryId": "123e4567-e89b-12d3-a456-426614174003",
      "plannedAmount": 800.00
    },
    {
      "categoryId": "123e4567-e89b-12d3-a456-426614174004",
      "plannedAmount": 200.00
    }
  ]
}
```

### 3. Get Budget by ID
```
GET /api/budgets/{budgetId}?userId={userId}
```

### 4. Update Budget Item
```
PATCH /api/budgets/{budgetId}/items/{categoryId}?userId={userId}
```

**Request Body:**
```json
{
  "plannedAmount": 900.00
}
```

### 5. Delete Budget
```
DELETE /api/budgets/{budgetId}?userId={userId}
```

## Reports API

### 1. Cashflow Report
```
GET /api/reports/cashflow?userId={userId}&from=2024-01-01&to=2024-01-31
```

**Response:**
```json
{
  "fromDate": "2024-01-01",
  "toDate": "2024-01-31",
  "totalIncome": 5000.00,
  "totalExpenses": 3200.50,
  "netCashflow": 1799.50,
  "dataPoints": [
    {
      "date": "2024-01-01",
      "income": 2000.00,
      "expenses": 0.00,
      "netCashflow": 2000.00
    },
    {
      "date": "2024-01-15",
      "income": 0.00,
      "expenses": 150.50,
      "netCashflow": -150.50
    }
  ]
}
```

### 2. Spending by Category Report
```
GET /api/reports/spend-by-category?userId={userId}&from=2024-01-01&to=2024-01-31
```

**Response:**
```json
{
  "fromDate": "2024-01-01",
  "toDate": "2024-01-31",
  "totalSpent": 3200.50,
  "categoryData": [
    {
      "categoryId": "123e4567-e89b-12d3-a456-426614174003",
      "categoryName": "Food",
      "categoryColor": "#FF6B6B",
      "amount": 1200.50,
      "percentage": 37.52,
      "transactionCount": 15
    },
    {
      "categoryId": "123e4567-e89b-12d3-a456-426614174004",
      "categoryName": "Transport",
      "categoryColor": "#4ECDC4",
      "amount": 800.00,
      "percentage": 25.00,
      "transactionCount": 8
    }
  ]
}
```

### 3. Trend Report
```
GET /api/reports/trend?userId={userId}&months=6
```

**Response:**
```json
{
  "months": 6,
  "startDate": "2023-08-01",
  "endDate": "2024-01-31",
  "monthlyData": [
    {
      "month": "2023-08",
      "monthStart": "2023-08-01",
      "monthEnd": "2023-08-31",
      "totalIncome": 4500.00,
      "totalExpenses": 2800.00,
      "netCashflow": 1700.00,
      "transactionCount": 45
    },
    {
      "month": "2023-09",
      "monthStart": "2023-09-01",
      "monthEnd": "2023-09-30",
      "totalIncome": 4800.00,
      "totalExpenses": 3200.00,
      "netCashflow": 1600.00,
      "transactionCount": 52
    }
  ],
  "summary": {
    "averageIncome": 4650.00,
    "averageExpenses": 3000.00,
    "averageNetCashflow": 1650.00,
    "totalIncome": 27900.00,
    "totalExpenses": 18000.00,
    "totalNetCashflow": 9900.00,
    "incomeGrowthRate": 6.67,
    "expenseGrowthRate": 14.29
  }
}
```

## Database Optimization

### Strategic Indexes
The API includes comprehensive database indexes for optimal performance:

```sql
-- Budget indexes
CREATE INDEX idx_budget_user_id ON budgets(user_id);
CREATE INDEX idx_budget_start_date ON budgets(start_date);
CREATE INDEX idx_budget_user_start_date ON budgets(user_id, start_date);

-- Transaction indexes for reports
CREATE INDEX idx_transaction_posted_at ON transactions(posted_at);
CREATE INDEX idx_transaction_category_id ON transactions(category_id);
CREATE INDEX idx_transaction_posted_at_amount ON transactions(posted_at, amount);

-- Composite indexes for complex queries
CREATE INDEX idx_transaction_user_posted_amount ON transactions(account_id, posted_at, amount);
CREATE INDEX idx_transaction_user_category_posted ON transactions(account_id, category_id, posted_at);
```

### Query Optimization
- **Aggregation Queries**: Use SUM, COUNT, and GROUP BY with proper indexes
- **Date Range Queries**: Optimized with posted_at indexes
- **User-Scoped Queries**: All queries filtered by user_id for security
- **Category Joins**: Efficient joins between transactions and categories

## Chart Integration

### Frontend Chart Libraries
The DTOs are designed to work seamlessly with popular chart libraries:

#### Chart.js Integration
```javascript
// Cashflow Chart
const cashflowData = {
  labels: report.dataPoints.map(point => point.date),
  datasets: [{
    label: 'Income',
    data: report.dataPoints.map(point => point.income),
    borderColor: 'rgb(75, 192, 192)'
  }, {
    label: 'Expenses',
    data: report.dataPoints.map(point => point.expenses),
    borderColor: 'rgb(255, 99, 132)'
  }]
};

// Spending by Category Pie Chart
const categoryData = {
  labels: report.categoryData.map(cat => cat.categoryName),
  datasets: [{
    data: report.categoryData.map(cat => cat.amount),
    backgroundColor: report.categoryData.map(cat => cat.categoryColor)
  }]
};
```

#### D3.js Integration
```javascript
// Trend Line Chart
const trendData = report.monthlyData.map(d => ({
  month: d.month,
  income: d.totalIncome,
  expenses: d.totalExpenses,
  netCashflow: d.netCashflow
}));
```

## Business Logic

### Budget Management
1. **One Budget Per Month**: Users can only have one active budget per month
2. **Auto-Creation**: Default budget created when accessing a month without budget
3. **Real-Time Calculations**: Spent amounts calculated from actual transactions
4. **Category-Based Items**: Budget items are organized by spending categories

### Spending Analysis
1. **Real-Time Updates**: Spent amounts update automatically with new transactions
2. **Percentage Calculations**: Automatic calculation of spending percentages
3. **Remaining Amounts**: Calculated as planned - actual amounts
4. **Visual Indicators**: Color-coded spending status (under/over budget)

### Report Generation
1. **Date Range Filtering**: All reports support custom date ranges
2. **User Isolation**: All data scoped to individual users
3. **Performance Optimized**: Queries designed for large datasets
4. **Chart-Ready Format**: Data structured for immediate chart consumption

## Error Handling

### Validation Errors
- **Budget Creation**: Duplicate month validation
- **Amount Validation**: Non-negative amounts for budget items
- **Date Validation**: Valid date ranges and formats
- **Category Validation**: Existing category IDs

### Business Logic Errors
- **Budget Not Found**: When accessing non-existent budgets
- **Unauthorized Access**: User can only access their own budgets
- **Invalid Date Ranges**: Start date must be before end date

## Performance Considerations

### Database Performance
- **Indexed Queries**: All major queries use appropriate indexes
- **Efficient Joins**: Optimized joins between related tables
- **Aggregation Optimization**: Pre-calculated sums and counts
- **Pagination Support**: Large datasets handled efficiently

### Caching Strategy
- **Budget Data**: Consider caching frequently accessed budget data
- **Report Data**: Cache expensive report calculations
- **Category Data**: Cache category information for performance

### Scalability
- **User Partitioning**: Data naturally partitioned by user
- **Time-Based Partitioning**: Consider partitioning by date ranges
- **Read Replicas**: Use read replicas for report queries

## Testing

### Unit Tests
- **BudgetService**: Complete CRUD operations testing
- **ReportsService**: All report generation methods
- **Data Validation**: Input validation and error handling
- **Business Logic**: Budget calculations and spending analysis

### Integration Tests
- **End-to-End Workflows**: Complete budget creation and reporting flows
- **Database Integration**: Real database testing with Testcontainers
- **Performance Testing**: Query performance validation
- **Error Scenarios**: Comprehensive error handling testing

### Test Examples
```java
@Test
void testCreateBudget() {
    CreateBudgetRequest request = new CreateBudgetRequest();
    request.setName("January 2024 Budget");
    request.setStartDate(LocalDate.of(2024, 1, 1));
    request.setEndDate(LocalDate.of(2024, 1, 31));
    request.setTotalAmount(new BigDecimal("3000.00"));
    
    BudgetDto result = budgetService.createBudget(userId, request);
    
    assertThat(result.getName()).isEqualTo("January 2024 Budget");
    assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("3000.00"));
}
```

## Security

### Data Isolation
- **User Scoping**: All queries filtered by user_id
- **Authorization**: Users can only access their own data
- **Input Validation**: All inputs validated and sanitized

### Audit Logging
- **Budget Changes**: All budget modifications logged
- **Budget Item Updates**: Track changes to budget items
- **Report Access**: Log report generation for compliance

## API Examples

### Create Monthly Budget
```bash
curl -X POST "http://localhost:8080/api/budgets?userId=123e4567-e89b-12d3-a456-426614174001" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "January 2024 Budget",
    "description": "Monthly budget for January 2024",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "totalAmount": 3000.00,
    "items": [
      {
        "categoryId": "123e4567-e89b-12d3-a456-426614174003",
        "plannedAmount": 800.00
      }
    ]
  }'
```

### Get Cashflow Report
```bash
curl -X GET "http://localhost:8080/api/reports/cashflow?userId=123e4567-e89b-12d3-a456-426614174001&from=2024-01-01&to=2024-01-31"
```

### Update Budget Item
```bash
curl -X PATCH "http://localhost:8080/api/budgets/123e4567-e89b-12d3-a456-426614174000/items/123e4567-e89b-12d3-a456-426614174003?userId=123e4567-e89b-12d3-a456-426614174001" \
  -H "Content-Type: application/json" \
  -d '{
    "plannedAmount": 900.00
  }'
```

This Budgets and Reports API provides a comprehensive foundation for financial planning and analysis in your fintech application, with optimized performance and chart-ready data structures.
