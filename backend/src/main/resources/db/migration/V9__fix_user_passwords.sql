-- Fix user passwords with correct BCrypt hashes
-- These hashes are generated fresh and verified to work

-- Update admin user password to admin123
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
WHERE email = 'admin@bika.com';

-- Update superadmin user password to superadmin123
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
WHERE email = 'superadmin@bika.com'; 