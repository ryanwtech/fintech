-- Create indexes for budget and reports performance

-- Budget indexes
CREATE INDEX IF NOT EXISTS idx_budget_user_id ON budgets(user_id);
CREATE INDEX IF NOT EXISTS idx_budget_start_date ON budgets(start_date);
CREATE INDEX IF NOT EXISTS idx_budget_user_start_date ON budgets(user_id, start_date);
CREATE INDEX IF NOT EXISTS idx_budget_active ON budgets(is_active);

-- Budget item indexes
CREATE INDEX IF NOT EXISTS idx_budget_item_budget_id ON budget_items(budget_id);
CREATE INDEX IF NOT EXISTS idx_budget_item_category_id ON budget_items(category_id);
CREATE INDEX IF NOT EXISTS idx_budget_item_budget_category ON budget_items(budget_id, category_id);

-- Transaction indexes for reports
CREATE INDEX IF NOT EXISTS idx_transaction_posted_at ON transactions(posted_at);
CREATE INDEX IF NOT EXISTS idx_transaction_category_id ON transactions(category_id);
CREATE INDEX IF NOT EXISTS idx_transaction_amount ON transactions(amount);
CREATE INDEX IF NOT EXISTS idx_transaction_posted_at_amount ON transactions(posted_at, amount);
CREATE INDEX IF NOT EXISTS idx_transaction_category_posted_at ON transactions(category_id, posted_at);

-- Account indexes for user-based queries
CREATE INDEX IF NOT EXISTS idx_account_user_id ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_account_user_active ON accounts(user_id, is_active);

-- Category indexes
CREATE INDEX IF NOT EXISTS idx_category_user_id ON categories(user_id);
CREATE INDEX IF NOT EXISTS idx_category_user_active ON categories(user_id, is_active);

-- Composite indexes for complex queries
CREATE INDEX IF NOT EXISTS idx_transaction_user_posted_amount ON transactions(account_id, posted_at, amount);
CREATE INDEX IF NOT EXISTS idx_transaction_user_category_posted ON transactions(account_id, category_id, posted_at);

-- Audit log indexes are created in V4 migration after the table is created
