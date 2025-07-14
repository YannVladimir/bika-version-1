-- Convert user role from PostgreSQL enum to VARCHAR to avoid enum type issues
-- This will allow Hibernate to work properly with string enums

-- Step 1: Convert role column to VARCHAR
ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(50);

-- Step 2: Add check constraint to ensure valid role values
ALTER TABLE users ADD CONSTRAINT check_user_role 
    CHECK (role IN ('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER'));

-- Step 3: Drop the enum type since we're no longer using it for users table
-- Note: Keep it for now in case other tables use it, we can clean up later if needed
-- DROP TYPE IF EXISTS user_role; 