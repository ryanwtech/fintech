-- Create indexes for webhook processing performance

-- Bank connection indexes
CREATE INDEX IF NOT EXISTS idx_bank_connection_user_id ON bank_connections(user_id);
CREATE INDEX IF NOT EXISTS idx_bank_connection_external_id ON bank_connections(external_connection_id);
CREATE INDEX IF NOT EXISTS idx_bank_connection_status ON bank_connections(connection_status);

-- Webhook event indexes
CREATE INDEX IF NOT EXISTS idx_webhook_event_status ON webhook_events(status);
CREATE INDEX IF NOT EXISTS idx_webhook_event_created_at ON webhook_events(created_at);
CREATE INDEX IF NOT EXISTS idx_webhook_event_status_created ON webhook_events(status, created_at);
CREATE INDEX IF NOT EXISTS idx_webhook_event_source ON webhook_events(source);

-- Transaction external ID index for webhook processing
CREATE INDEX IF NOT EXISTS idx_transaction_external_id ON transactions(external_id);

-- Composite indexes for webhook processing
CREATE INDEX IF NOT EXISTS idx_webhook_event_pending ON webhook_events(status, created_at) WHERE status = 'PENDING';
CREATE INDEX IF NOT EXISTS idx_webhook_event_failed ON webhook_events(status, created_at) WHERE status = 'FAILED';
