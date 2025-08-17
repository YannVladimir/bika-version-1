-- Fix column types in password_reset_tokens table
-- Change created_by and updated_by from BIGINT to VARCHAR(255)

ALTER TABLE password_reset_tokens 
ALTER COLUMN created_by TYPE VARCHAR(255);

ALTER TABLE password_reset_tokens 
ALTER COLUMN updated_by TYPE VARCHAR(255); 