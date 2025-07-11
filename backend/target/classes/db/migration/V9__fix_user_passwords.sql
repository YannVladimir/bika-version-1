-- Fix user passwords with correct BCrypt hashes
-- Update admin user password to admin123
UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE email = 'admin@bika.com';

-- Update superadmin user password to superadmin123
UPDATE users 
SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE email = 'superadmin@bika.com'; 