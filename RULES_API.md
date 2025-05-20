# Rules API Documentation

## Overview
The Rules API provides intelligent transaction categorization using pattern matching. Rules automatically assign categories to transactions based on merchant names and descriptions using regular expressions.

## Features
- **Pattern Matching**: Use regex patterns to match merchant names and descriptions
- **Priority System**: Rules are applied in priority order (lower numbers = higher priority)
- **Logic Operators**: Support for AND/OR logic between patterns
- **Auto-categorization**: Automatically assign categories during transaction creation/import
- **Audit Logging**: Complete audit trail of rule changes
- **Comprehensive Testing**: Full test coverage with Testcontainers

## Endpoints

### 1. Get User Rules
```
GET /api/rules?userId={userId}
```

**Response:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "userId": "123e4567-e89b-12d3-a456-426614174001",
    "name": "Starbucks Rule",
    "description": "Auto-categorize Starbucks transactions",
    "conditions": "{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}",
    "actions": "{\"targetCategoryId\": \"123e4567-e89b-12d3-a456-426614174002\"}",
    "priority": 1,
    "enabled": true,
    "createdAt": "2024-01-15T09:30:00",
    "updatedAt": "2024-01-15T09:30:00"
  }
]
```

### 2. Get Rule by ID
```
GET /api/rules/{ruleId}?userId={userId}
```

### 3. Create Rule
```
POST /api/rules?userId={userId}
```

**Request Body:**
```json
{
  "name": "Coffee Rule",
  "description": "Auto-categorize coffee transactions",
  "conditions": "{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}",
  "actions": "{\"targetCategoryId\": \"123e4567-e89b-12d3-a456-426614174002\"}",
  "priority": 1,
  "enabled": true
}
```

### 4. Update Rule
```
PATCH /api/rules/{ruleId}?userId={userId}
```

**Request Body:**
```json
{
  "name": "Updated Coffee Rule",
  "description": "Updated description",
  "priority": 5,
  "enabled": false
}
```

### 5. Delete Rule
```
DELETE /api/rules/{ruleId}?userId={userId}
```

## Rule Configuration

### Conditions Format
Rules use JSON format for conditions with the following structure:

```json
{
  "merchantPattern": ".*starbucks.*",
  "descriptionPattern": ".*coffee.*",
  "logic": "OR"
}
```

**Fields:**
- `merchantPattern`: Regex pattern to match against merchant name (optional)
- `descriptionPattern`: Regex pattern to match against description (optional)
- `logic`: Logic operator - "AND" or "OR" (default: "OR")

### Actions Format
Rules use JSON format for actions:

```json
{
  "targetCategoryId": "123e4567-e89b-12d3-a456-426614174002"
}
```

**Fields:**
- `targetCategoryId`: UUID of the category to assign when rule matches

### Priority System
- Lower numbers = higher priority
- Rules are evaluated in priority order
- First matching rule wins
- Disabled rules are skipped

## Pattern Examples

### Basic Patterns
```json
// Match any Starbucks merchant
{"merchantPattern": ".*starbucks.*"}

// Match coffee in description
{"descriptionPattern": ".*coffee.*"}

// Match exact merchant name
{"merchantPattern": "^Starbucks$"}

// Match multiple merchants
{"merchantPattern": ".*(starbucks|dunkin|tim hortons).*"}
```

### Advanced Patterns
```json
// Match Starbucks with case insensitive
{"merchantPattern": "(?i).*starbucks.*"}

// Match coffee or tea
{"descriptionPattern": ".*(coffee|tea).*"}

// Match specific amount patterns
{"descriptionPattern": ".*\\$[0-9]+\\.[0-9]{2}.*"}

// Match phone numbers
{"merchantPattern": ".*\\([0-9]{3}\\) [0-9]{3}-[0-9]{4}.*"}
```

### Logic Examples
```json
// OR logic - match either pattern
{
  "merchantPattern": ".*starbucks.*",
  "descriptionPattern": ".*coffee.*",
  "logic": "OR"
}

// AND logic - match both patterns
{
  "merchantPattern": ".*starbucks.*",
  "descriptionPattern": ".*coffee.*",
  "logic": "AND"
}
```

## Integration with Transactions

### Automatic Categorization
Rules are automatically applied when:
- Creating new transactions via API
- Importing transactions from CSV
- Only if no explicit category is provided

### Rule Application Process
1. Get all enabled rules for the user, ordered by priority
2. For each rule, evaluate conditions against merchant and description
3. If rule matches, assign the target category and stop processing
4. If no rules match, leave category as null

### Example Flow
```
Transaction: merchant="Starbucks Coffee", description="Morning coffee"
Rule 1: merchantPattern=".*starbucks.*" → MATCH → Assign Coffee category
Result: Transaction created with Coffee category
```

## Testing

### Unit Tests
- Rule creation, update, deletion
- Pattern matching logic
- Priority ordering
- Validation of conditions and actions

### Integration Tests
- Transaction creation with rule matching
- CSV import with rule application
- Multiple rule scenarios
- Edge cases and error handling

### Test Examples
```java
@Test
void testRuleMatching_MerchantPattern() {
    // Create rule
    createTestRule("Starbucks Rule", 
                  "{\"merchantPattern\": \".*starbucks.*\"}", 
                  "{\"targetCategoryId\": \"" + coffeeCategoryId + "\"}");
    
    // Test matching
    var match = ruleService.applyRulesToTransaction(userId, "Starbucks Coffee", "Morning coffee");
    
    assertThat(match.isMatch()).isTrue();
    assertThat(match.getTargetCategoryId()).isEqualTo(coffeeCategoryId);
}
```

## Best Practices

### Rule Design
1. **Start Simple**: Begin with basic patterns and refine
2. **Use Specific Patterns**: Avoid overly broad patterns that might match unintended transactions
3. **Test Thoroughly**: Test rules with various transaction examples
4. **Use Priority Wisely**: Order rules from most specific to most general
5. **Regular Review**: Periodically review and update rules

### Performance Considerations
1. **Limit Rule Count**: Too many rules can impact performance
2. **Efficient Patterns**: Use efficient regex patterns
3. **Priority Ordering**: Place most common rules first
4. **Disable Unused Rules**: Disable rules that are no longer needed

### Common Patterns
```json
// Food delivery services
{"merchantPattern": ".*(uber eats|doordash|grubhub).*"}

// Gas stations
{"merchantPattern": ".*(shell|exxon|chevron|bp).*"}

// Grocery stores
{"merchantPattern": ".*(walmart|target|kroger|safeway).*"}

// Subscription services
{"descriptionPattern": ".*(subscription|monthly|recurring).*"}

// ATM withdrawals
{"descriptionPattern": ".*(atm|withdrawal|cash).*"}
```

## Error Handling

### Validation Errors
- Invalid JSON format in conditions/actions
- Invalid regex patterns
- Missing required fields
- Invalid category IDs

### Runtime Errors
- Malformed regex patterns are logged but don't fail transactions
- Missing categories are logged but don't fail transactions
- Database errors are handled gracefully

## Security Considerations

- User isolation: Rules are scoped to individual users
- Input validation: All inputs are validated before processing
- SQL injection prevention: Uses parameterized queries
- Audit logging: All rule changes are logged

## Monitoring and Debugging

### Audit Logs
All rule operations are logged with:
- Rule ID and name
- Old and new values for updates
- Timestamp and user information

### Debug Information
- Rule evaluation results
- Pattern matching details
- Performance metrics

## Migration and Maintenance

### Rule Updates
- Rules can be updated without affecting existing transactions
- Historical transactions retain their original categories
- New transactions will use updated rules

### Rule Cleanup
- Disable unused rules instead of deleting
- Archive old rules for historical reference
- Regular cleanup of test rules

## API Examples

### Create a Coffee Rule
```bash
curl -X POST "http://localhost:8080/api/rules?userId=123e4567-e89b-12d3-a456-426614174001" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Coffee Rule",
    "description": "Auto-categorize coffee transactions",
    "conditions": "{\"merchantPattern\": \".*starbucks.*\", \"descriptionPattern\": \".*coffee.*\", \"logic\": \"OR\"}",
    "actions": "{\"targetCategoryId\": \"123e4567-e89b-12d3-a456-426614174002\"}",
    "priority": 1,
    "enabled": true
  }'
```

### Update Rule Priority
```bash
curl -X PATCH "http://localhost:8080/api/rules/123e4567-e89b-12d3-a456-426614174000?userId=123e4567-e89b-12d3-a456-426614174001" \
  -H "Content-Type: application/json" \
  -d '{
    "priority": 5,
    "enabled": false
  }'
```

### Get All Rules
```bash
curl -X GET "http://localhost:8080/api/rules?userId=123e4567-e89b-12d3-a456-426614174001"
```

This Rules API provides a powerful and flexible way to automatically categorize transactions, reducing manual effort and improving data consistency in your fintech application.
