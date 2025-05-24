# Mock Bank Connector API Documentation

## Overview
The Mock Bank Connector API provides a simulated banking integration for testing and development purposes. It includes bank account linking, webhook processing for transaction updates, and comprehensive audit logging.

## Features
- **Bank Account Linking**: Connect mock bank accounts with fake access tokens
- **Webhook Processing**: Receive and process transaction updates asynchronously
- **Transaction Upserts**: Automatically create or update transactions from webhook payloads
- **Audit Logging**: Complete audit trail of all bank integration activities
- **CLI Testing Tool**: Command-line interface for simulating webhook events
- **Async Processing**: Background processing of webhook events for scalability

## Banking Integration API

### 1. Link Mock Bank Account
```
POST /api/integrations/mockbank/link?userId={userId}
```

**Request Body:**
```json
{
  "bankName": "Mock Bank",
  "accountName": "Checking Account",
  "accountNumber": "1234567890",
  "routingNumber": "021000021",
  "currency": "USD"
}
```

**Response:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "123e4567-e89b-12d3-a456-426614174001",
  "bankName": "Mock Bank",
  "externalConnectionId": "mock_token_abc123def456",
  "accountNumberMasked": "****7890",
  "connectionStatus": "ACTIVE",
  "lastSyncAt": "2024-01-15T09:30:00",
  "createdAt": "2024-01-15T09:30:00",
  "updatedAt": "2024-01-15T09:30:00"
}
```

### 2. Get User Bank Connections
```
GET /api/integrations/mockbank/connections?userId={userId}
```

**Response:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "userId": "123e4567-e89b-12d3-a456-426614174001",
    "bankName": "Mock Bank",
    "externalConnectionId": "mock_token_abc123def456",
    "accountNumberMasked": "****7890",
    "connectionStatus": "ACTIVE",
    "lastSyncAt": "2024-01-15T09:30:00",
    "createdAt": "2024-01-15T09:30:00",
    "updatedAt": "2024-01-15T09:30:00"
  }
]
```

### 3. Get Bank Connection by ID
```
GET /api/integrations/mockbank/connections/{connectionId}?userId={userId}
```

### 4. Unlink Bank Account
```
DELETE /api/integrations/mockbank/connections/{connectionId}?userId={userId}
```

## Webhook API

### 1. Receive Mock Bank Webhook (Public)
```
POST /api/webhooks/mockbank
```

**Request Body:**
```json
{
  "eventType": "transactions.new",
  "accountId": "external_account_123",
  "transactions": [
    {
      "transactionId": "txn_abc123",
      "amount": -50.00,
      "description": "Coffee shop purchase",
      "merchant": "Starbucks",
      "postedAt": "2024-01-15T09:30:00",
      "currency": "USD",
      "category": "Food",
      "status": "cleared"
    }
  ]
}
```

**Response:**
```
"Webhook received and queued for processing"
```

### 2. Simulate Test Webhook
```
POST /api/webhooks/test/simulate
```

Same request body format as above. This endpoint is for testing purposes.

### 3. Get Webhook Events (Debug)
```
GET /api/webhooks/events
```

**Response:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "eventType": "transactions.new",
    "payload": "{\"eventType\":\"transactions.new\",\"accountId\":\"external_account_123\",\"transactions\":[...]}",
    "source": "mockbank",
    "status": "PROCESSED",
    "errorMessage": null,
    "processedAt": "2024-01-15T09:31:00",
    "createdAt": "2024-01-15T09:30:00"
  }
]
```

## Webhook Processing Flow

### 1. Webhook Reception
1. **Receive Payload**: Webhook endpoint receives transaction data
2. **Create Event**: Create `webhook_events` record with PENDING status
3. **Queue Processing**: Trigger asynchronous processing
4. **Return Response**: Immediately return success to sender

### 2. Async Processing
1. **Parse Payload**: Extract transaction data from JSON payload
2. **Find Account**: Map external account ID to internal account
3. **Process Transactions**: For each transaction:
   - Check for existing transaction by external ID
   - Create new transaction or update existing
   - Apply business rules and validation
   - Log audit trail
4. **Update Event**: Mark webhook event as PROCESSED or FAILED

### 3. Transaction Upsert Logic
```java
// Check if transaction exists
Optional<Transaction> existing = findByExternalId(transactionData.getTransactionId());

if (existing.isPresent()) {
    // Update existing transaction
    updateTransaction(existing.get(), transactionData);
} else {
    // Create new transaction
    createTransaction(account, transactionData);
}
```

## CLI Testing Tool

### Webhook Simulator
Run the CLI tool to simulate webhook events:

```bash
java -jar webhook-simulator.jar
```

**Features:**
- **Interactive Menu**: Choose from predefined webhook scenarios
- **Custom Transactions**: Create custom transaction data
- **Multiple Transactions**: Simulate batch transaction updates
- **Real-time Testing**: Send webhooks to running application

**Example Usage:**
```
=== Mock Bank Webhook Simulator ===
Options:
1. Simulate new transactions webhook
2. Simulate income transaction
3. Simulate expense transaction
4. Simulate multiple transactions
5. Exit

Choose an option (1-5): 2
Enter account ID (external): test_account_123
Enter amount: 2500.00
Enter description: Monthly salary
Webhook sent successfully!
Response: Webhook received and queued for processing
```

## Database Schema

### Bank Connections Table
```sql
CREATE TABLE bank_connections (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    external_connection_id VARCHAR(255),
    account_number_masked VARCHAR(20),
    connection_status VARCHAR(20) DEFAULT 'ACTIVE',
    last_sync_at TIMESTAMP,
    credentials_encrypted TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Webhook Events Table
```sql
CREATE TABLE webhook_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    processed_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Performance Optimizations

### Database Indexes
```sql
-- Bank connection performance
CREATE INDEX idx_bank_connection_user_id ON bank_connections(user_id);
CREATE INDEX idx_bank_connection_external_id ON bank_connections(external_connection_id);
CREATE INDEX idx_bank_connection_status ON bank_connections(connection_status);

-- Webhook processing performance
CREATE INDEX idx_webhook_event_status ON webhook_events(status);
CREATE INDEX idx_webhook_event_status_created ON webhook_events(status, created_at);
CREATE INDEX idx_webhook_event_pending ON webhook_events(status, created_at) WHERE status = 'PENDING';

-- Transaction lookup optimization
CREATE INDEX idx_transaction_external_id ON transactions(external_id);
```

### Async Processing
- **@Async Annotation**: Webhook processing runs in background threads
- **Event Sourcing**: All webhook events stored for replay/debugging
- **Retry Logic**: Failed events can be retried with exponential backoff
- **Dead Letter Queue**: Failed events after max retries moved to DLQ

## Security Considerations

### Access Control
- **User Isolation**: All bank connections scoped to individual users
- **Token Validation**: Access tokens validated for each request
- **Input Validation**: All webhook payloads validated before processing

### Data Protection
- **Masked Account Numbers**: Real account numbers never stored in plain text
- **Encrypted Credentials**: Bank credentials encrypted at rest
- **Audit Logging**: All actions logged for compliance

### Webhook Security
- **Public Endpoint**: Webhook endpoint is intentionally public for testing
- **Payload Validation**: Webhook payloads validated against schema
- **Error Handling**: Graceful error handling prevents information leakage

## Error Handling

### Common Errors
```json
// Bank account already linked
{
  "error": "Bank account already linked",
  "code": "DUPLICATE_CONNECTION"
}

// Invalid external account mapping
{
  "error": "Account not found for external ID: external_account_123",
  "code": "ACCOUNT_NOT_FOUND"
}

// Webhook processing failure
{
  "error": "Failed to process transaction txn_abc123: Invalid amount format",
  "code": "PROCESSING_ERROR"
}
```

### Retry Strategy
1. **Immediate Retry**: Retry failed webhooks immediately
2. **Exponential Backoff**: Increase delay between retries
3. **Max Retries**: Limit total retry attempts
4. **Dead Letter Queue**: Move permanently failed events to DLQ

## Monitoring and Debugging

### Webhook Event Status
- **PENDING**: Event received, waiting for processing
- **PROCESSED**: Event successfully processed
- **FAILED**: Event processing failed

### Debug Endpoints
```bash
# Get pending webhook events
GET /api/webhooks/events

# Get failed webhook events (last 24 hours)
GET /api/webhooks/events?status=FAILED

# Get webhook event by ID
GET /api/webhooks/events/{eventId}
```

### Audit Logs
All bank integration activities are logged:
- Bank connection creation/updates
- Transaction creation/updates from webhooks
- Webhook event processing results

## API Examples

### Link Bank Account
```bash
curl -X POST "http://localhost:8080/api/integrations/mockbank/link?userId=123e4567-e89b-12d3-a456-426614174001" \
  -H "Content-Type: application/json" \
  -d '{
    "bankName": "Mock Bank",
    "accountName": "Checking Account",
    "accountNumber": "1234567890",
    "routingNumber": "021000021",
    "currency": "USD"
  }'
```

### Simulate Webhook
```bash
curl -X POST "http://localhost:8080/api/webhooks/test/simulate" \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "transactions.new",
    "accountId": "test_account_123",
    "transactions": [
      {
        "transactionId": "txn_test_123",
        "amount": -25.50,
        "description": "Coffee purchase",
        "merchant": "Starbucks",
        "postedAt": "2024-01-15T09:30:00",
        "currency": "USD",
        "category": "Food",
        "status": "cleared"
      }
    ]
  }'
```

### Get Bank Connections
```bash
curl -X GET "http://localhost:8080/api/integrations/mockbank/connections?userId=123e4567-e89b-12d3-a456-426614174001"
```

## Testing Scenarios

### 1. Bank Account Linking
1. Link bank account with valid credentials
2. Attempt to link same account twice (should fail)
3. Link multiple accounts for same user
4. Unlink bank account

### 2. Webhook Processing
1. Send valid transaction webhook
2. Send duplicate transaction (should update existing)
3. Send invalid payload (should fail gracefully)
4. Send webhook for non-existent account

### 3. Transaction Creation
1. Create income transaction
2. Create expense transaction
3. Create transaction with category mapping
4. Create transaction without category

### 4. Error Scenarios
1. Invalid JSON payload
2. Missing required fields
3. Invalid transaction amounts
4. Network failures during processing

This Mock Bank Connector provides a comprehensive testing environment for bank integration features, with full audit logging, async processing, and realistic webhook simulation capabilities.
