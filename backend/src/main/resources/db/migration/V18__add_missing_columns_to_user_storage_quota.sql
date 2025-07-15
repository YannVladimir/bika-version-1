-- V18: Add missing columns to user_storage_quota table
-- This migration adds the missing created_by and updated_by columns
-- that are required by the BaseEntity class

ALTER TABLE user_storage_quota 
ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'system',
ADD COLUMN updated_by VARCHAR(255) NOT NULL DEFAULT 'system';

-- Update the default constraint to remove the DEFAULT after adding the columns
ALTER TABLE user_storage_quota 
ALTER COLUMN created_by DROP DEFAULT,
ALTER COLUMN updated_by DROP DEFAULT; 