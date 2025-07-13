-- Update user roles to match new UserRole enum
-- Remove ADMIN and DEPARTMENT_MANAGER, COMPANY_USER, GUEST roles
-- Keep SUPER_ADMIN, COMPANY_ADMIN, MANAGER, USER

-- First, convert the role column to text to avoid enum constraint issues
ALTER TABLE users ALTER COLUMN role TYPE text;

-- Update any existing ADMIN roles to COMPANY_ADMIN
UPDATE users SET role = 'COMPANY_ADMIN' WHERE role = 'ADMIN';

-- Update DEPARTMENT_MANAGER to MANAGER
UPDATE users SET role = 'MANAGER' WHERE role = 'DEPARTMENT_MANAGER';

-- Update COMPANY_USER to USER
UPDATE users SET role = 'USER' WHERE role = 'COMPANY_USER';

-- Update GUEST to USER (or you could delete these records if they shouldn't exist)
UPDATE users SET role = 'USER' WHERE role = 'GUEST';

-- For PostgreSQL, update the enum type
-- Drop the old enum type if it exists
DROP TYPE IF EXISTS user_role CASCADE;

-- Create the new enum type with only the valid roles
CREATE TYPE user_role AS ENUM ('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER');

-- Convert the column back to the new enum type
ALTER TABLE users ALTER COLUMN role TYPE user_role USING role::user_role; 