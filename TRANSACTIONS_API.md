# Transactions API Documentation

## Overview
The Transactions API provides comprehensive functionality for managing financial transactions, including CRUD operations, CSV import, and audit logging.

## Endpoints

### 1. Get Transactions by Account
```
GET /api/accounts/{accountId}/transactions
```

**Query Parameters:**
- `from` (optional): Start date filter (ISO 8601 format)
- `to` (optional): End date filter (ISO 8601 format)
- `q` (optional): Search query for description or merchant
- `categoryId` (optional): Filter by category ID
- `page` (default: 0): Page number
- `size` (default: 20): Page size
- `sortBy` (default: "postedAt"): Sort field
- `sortDir` (default: "desc"): Sort direction (asc/desc)

**Example:**
```
GET /api/accounts/123e4567-e89b-12d3-a456-426614174000/transactions?from=2024-01-01T00:00:00&to=2024-01-31T23:59:59&q=coffee&page=0&size=10
```

### 2. Create Transaction
```
POST /api/accounts/{accountId}/transactions
```

**Request Body:**
```json
{
  "postedAt": "2024-01-15T09:30:00",
  "amount": 25.50,
  "currency": "USD",
  "merchant": "Starbucks",
  "description": "Coffee and pastry",
  "categoryId": "123e4567-e89b-12d3-a456-426614174001",
  "notes": "Morning coffee"
}
```

### 3. Update Transaction
```
PATCH /api/transactions/{transactionId}
```

**Request Body:**
```json
{
  "amount": 30.00,
  "description": "Updated description",
  "merchant": "Updated Merchant"
}
```

### 4. Delete Transaction
```
DELETE /api/transactions/{transactionId}
```

### 5. Import Transactions from CSV
```
POST /api/transactions/import
```

**Form Data:**
- `accountId`: UUID of the account
- `file`: CSV file

**CSV Format:**
```csv
postedAt,amount,merchant,description,categoryId,notes
2024-01-15 09:30:00,25.50,Starbucks,Coffee and pastry,
2024-01-15 14:20:00,-150.00,Shell,Gas station fill-up,
2024-01-16 12:00:00,1200.00,Employer,Salary deposit,
```

**CSV Fields:**
- `postedAt`: Transaction date (yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
- `amount`: Transaction amount (positive for credit, negative for debit)
- `merchant`: Merchant name (optional)
- `description`: Transaction description (optional)
- `categoryId`: Category UUID (optional)
- `notes`: Additional notes (optional)

## Features

### 1. CSV Import
- **Duplicate Detection**: Automatically detects and prevents duplicate transactions
- **Validation**: Comprehensive validation of CSV data
- **Error Reporting**: Detailed error messages for failed imports
- **Batch Processing**: Efficient processing of large CSV files

### 2. Audit Logging
- **Transaction Changes**: All create, update, and delete operations are logged
- **Import Tracking**: CSV import operations are logged with success/failure counts
- **Change History**: Old and new values are stored for updates

### 3. Validation
- **Amount Validation**: Ensures amounts are greater than 0
- **Date Validation**: Validates date formats and ranges
- **String Length**: Enforces maximum length limits
- **Required Fields**: Validates required fields are present

### 4. Search and Filtering
- **Date Range**: Filter transactions by date range
- **Text Search**: Search by description or merchant name
- **Category Filter**: Filter by specific category
- **Pagination**: Efficient pagination for large datasets

## Response Formats

### Transaction DTO
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "accountId": "123e4567-e89b-12d3-a456-426614174001",
  "categoryId": "123e4567-e89b-12d3-a456-426614174002",
  "amount": 25.50,
  "description": "Coffee and pastry",
  "merchant": "Starbucks",
  "postedAt": "2024-01-15T09:30:00",
  "transactionType": "CREDIT",
  "status": "PENDING",
  "externalId": "TXN_1705312200000_abc12345",
  "metadata": null,
  "createdAt": "2024-01-15T09:30:00",
  "updatedAt": "2024-01-15T09:30:00"
}
```

### CSV Import Result
```json
{
  "totalRows": 6,
  "successfulImports": 5,
  "failedImports": 1,
  "errors": [
    "Line 3: Invalid date format: 2024-13-45"
  ],
  "importedTransactions": [
    // Array of successfully imported TransactionDto objects
  ]
}
```

## Error Handling

### Common Error Responses
- **400 Bad Request**: Invalid request data or validation errors
- **404 Not Found**: Account or transaction not found
- **500 Internal Server Error**: Server-side errors

### Validation Errors
- Amount must be greater than 0
- Invalid date format
- String length exceeds maximum
- Required fields missing

## Usage Examples

### 1. Get Recent Transactions
```bash
curl -X GET "http://localhost:8080/api/accounts/123e4567-e89b-12d3-a456-426614174000/transactions?page=0&size=10&sortBy=postedAt&sortDir=desc"
```

### 2. Create a Transaction
```bash
curl -X POST "http://localhost:8080/api/accounts/123e4567-e89b-12d3-a456-426614174000/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "postedAt": "2024-01-15T09:30:00",
    "amount": 25.50,
    "merchant": "Starbucks",
    "description": "Coffee and pastry"
  }'
```

### 3. Import CSV
```bash
curl -X POST "http://localhost:8080/api/transactions/import" \
  -F "accountId=123e4567-e89b-12d3-a456-426614174000" \
  -F "file=@transactions.csv"
```

## Security Considerations

- All endpoints require proper authentication
- Account ownership is validated for all operations
- Input validation prevents injection attacks
- Audit logging provides compliance tracking

## Performance Notes

- Pagination is implemented for large datasets
- Database queries are optimized with proper indexing
- CSV import is processed in batches for efficiency
- Caching can be added for frequently accessed data
