-- V{numero}_add_reset_password_otp_fields.sql
ALTER TABLE users
    ADD COLUMN reset_password_code VARCHAR(6),
ADD COLUMN reset_password_code_expires_at TIMESTAMP;

CREATE INDEX idx_users_reset_code ON users(reset_password_code);