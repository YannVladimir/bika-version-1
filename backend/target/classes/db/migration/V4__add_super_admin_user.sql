-- Add super admin user with properly encoded password
-- Password: superadmin123 (BCrypt encoded)
INSERT INTO users (
    username, 
    email, 
    password, 
    first_name, 
    last_name, 
    role, 
    company_id, 
    department_id,
    is_active,
    created_at,
    updated_at,
    created_by,
    updated_by
) VALUES (
    'superadmin',
    'superadmin@bika.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- superadmin123
    'Super',
    'Administrator',
    'SUPER_ADMIN',
    1,
    1,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
) ON CONFLICT (username) DO UPDATE SET
    email = EXCLUDED.email,
    password = EXCLUDED.password,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active,
    updated_at = CURRENT_TIMESTAMP,
    updated_by = 'system'; 