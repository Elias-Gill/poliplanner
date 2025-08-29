ALTER TABLE users
    ADD COLUMN recovery_token_hash VARCHAR(255),
    ADD COLUMN recovery_token_expiration TIMESTAMP,
    ADD COLUMN recovery_token_used BOOLEAN DEFAULT FALSE;
