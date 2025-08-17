-- Add test users with different roles for testing
-- Passwords are BCrypt encoded versions of: [username]123

-- Manager user (can create document types and documents)
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
    'manager',
    'manager@bika.com',
    '$2a$10$N.wmP6JhAQI8Nt4l8.mOKekHaLw2kU8jNTZWOCW4gKDZhIWQUuPl6', -- manager123
    'Manager',
    'User',
    'MANAGER',
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
    updated_at = CURRENT_TIMESTAMP;

-- Regular user (can create documents only)
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
    'testuser',
    'testuser@bika.com',
    '$2a$10$N.wmP6JhAQI8Nt4l8.mOKekHaLw2kU8jNTZWOCW4gKDZhIWQUuPl6', -- testuser123
    'Test',
    'User',
    'USER',
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
    updated_at = CURRENT_TIMESTAMP; 