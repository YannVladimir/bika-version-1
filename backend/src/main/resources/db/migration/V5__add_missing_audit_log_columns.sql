-- Add missing columns to audit_logs table to match BaseEntity
ALTER TABLE audit_logs 
ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'system',
ADD COLUMN updated_by VARCHAR(255) NOT NULL DEFAULT 'system'; 