-- Test data for integration tests

-- Insert test users
INSERT INTO users (id, username, email, password, first_name, last_name, role, is_active, created_at, updated_at)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhUz0LJxK8LQz8BQz8BQz8BQz', 'Test', 'User', 'USER', true, NOW(), NOW()),
    ('22222222-2222-2222-2222-222222222222', 'adminuser', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhUz0LJxK8LQz8BQz8BQz8BQz', 'Admin', 'User', 'ADMIN', true, NOW(), NOW());

-- Insert test accounts
INSERT INTO accounts (id, user_id, name, account_type, balance, currency, is_active, created_at, updated_at)
VALUES 
    ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'Test Checking', 'CHECKING', 1000.00, 'USD', true, NOW(), NOW()),
    ('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'Test Savings', 'SAVINGS', 5000.00, 'USD', true, NOW(), NOW()),
    ('55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', 'Admin Account', 'CHECKING', 2000.00, 'USD', true, NOW(), NOW());

-- Insert test categories
INSERT INTO categories (id, user_id, name, description, color, icon, is_income, is_active, created_at, updated_at)
VALUES 
    ('66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', 'Food', 'Food and dining expenses', '#FF6B6B', '🍽️', false, true, NOW(), NOW()),
    ('77777777-7777-7777-7777-777777777777', '11111111-1111-1111-1111-111111111111', 'Transportation', 'Transportation expenses', '#4ECDC4', '🚗', false, true, NOW(), NOW()),
    ('88888888-8888-8888-8888-888888888888', '11111111-1111-1111-1111-111111111111', 'Salary', 'Monthly salary income', '#45B7D1', '💰', true, true, NOW(), NOW()),
    ('99999999-9999-9999-9999-999999999999', NULL, 'Global Category', 'Global category for all users', '#96CEB4', '🌍', false, true, NOW(), NOW());

-- Insert test transactions
INSERT INTO transactions (id, account_id, category_id, amount, description, merchant, posted_at, transaction_type, status, external_id, created_at, updated_at)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '33333333-3333-3333-3333-333333333333', '66666666-6666-6666-6666-666666666666', -25.50, 'Coffee shop', 'Starbucks', NOW() - INTERVAL '1 day', 'DEBIT', 'CLEARED', 'ext_txn_001', NOW(), NOW()),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333', '77777777-7777-7777-7777-777777777777', -45.00, 'Gas station', 'Shell', NOW() - INTERVAL '2 days', 'DEBIT', 'CLEARED', 'ext_txn_002', NOW(), NOW()),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', '88888888-8888-8888-8888-888888888888', 3000.00, 'Monthly salary', 'Employer Corp', NOW() - INTERVAL '3 days', 'CREDIT', 'CLEARED', 'ext_txn_003', NOW(), NOW()),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '44444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666', -85.50, 'Grocery shopping', 'SuperMart', NOW() - INTERVAL '4 days', 'DEBIT', 'CLEARED', 'ext_txn_004', NOW(), NOW());

-- Insert test rules
INSERT INTO rules (id, user_id, name, description, conditions, actions, priority, enabled, created_at, updated_at)
VALUES 
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '11111111-1111-1111-1111-111111111111', 'Starbucks Rule', 'Auto-categorize Starbucks transactions', 'merchant.contains("starbucks")', 'categoryId=66666666-6666-6666-6666-666666666666', 1, true, NOW(), NOW()),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', '11111111-1111-1111-1111-111111111111', 'Gas Station Rule', 'Auto-categorize gas station transactions', 'merchant.contains("shell") OR merchant.contains("exxon")', 'categoryId=77777777-7777-7777-7777-777777777777', 2, true, NOW(), NOW());

-- Insert test budgets
INSERT INTO budgets (id, user_id, name, description, start_date, end_date, total_amount, is_active, created_at, updated_at)
VALUES 
    ('gggggggg-gggg-gggg-gggg-gggggggggggg', '11111111-1111-1111-1111-111111111111', 'January 2024 Budget', 'Monthly budget for January 2024', '2024-01-01', '2024-01-31', 2000.00, true, NOW(), NOW());

-- Insert test budget items
INSERT INTO budget_items (id, budget_id, category_id, planned_amount, actual_amount, created_at, updated_at)
VALUES 
    ('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', 'gggggggg-gggg-gggg-gggg-gggggggggggg', '66666666-6666-6666-6666-666666666666', 500.00, 111.00, NOW(), NOW()),
    ('iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii', 'gggggggg-gggg-gggg-gggg-gggggggggggg', '77777777-7777-7777-7777-777777777777', 200.00, 45.00, NOW(), NOW());

-- Insert test bank connections
INSERT INTO bank_connections (id, user_id, bank_name, external_connection_id, account_number_masked, connection_status, last_sync_at, created_at, updated_at)
VALUES 
    ('jjjjjjjj-jjjj-jjjj-jjjj-jjjjjjjjjjjj', '11111111-1111-1111-1111-111111111111', 'Test Bank', 'mock_token_12345', '****1234', 'ACTIVE', NOW(), NOW(), NOW());
