-- Fix user_role enum type issue
-- This migration ensures the role column properly accepts the enum values

-- Step 1: Convert role column to text temporarily
ALTER TABLE users ALTER COLUMN role TYPE text;

-- Step 2: Ensure all role values are valid
UPDATE users SET role = 'USER' WHERE role NOT IN ('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER');

-- Step 3: Convert back to enum type with explicit casting
ALTER TABLE users ALTER COLUMN role TYPE user_role USING role::user_role; 