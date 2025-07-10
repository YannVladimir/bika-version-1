-- Insert default company
INSERT INTO companies (name, code, email, description, is_active, created_at, updated_at, created_by, updated_by)
VALUES ('Bika System', 'BIKA001', 'info@bika.com', 'Default company for Bika System', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert default departments
INSERT INTO departments (name, code, description, company_id, is_active, created_at, updated_at, created_by, updated_by)
VALUES 
    ('Engineering', 'ENG', 'Software development and technical operations', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Product Management', 'PM', 'Product strategy and roadmap', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Design', 'DESIGN', 'UI/UX and visual design', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system');

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, role, company_id, department_id, is_active, created_at, updated_at, created_by, updated_by)
VALUES (
    'admin',
    'admin@bika.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'Admin',
    'User',
    'COMPANY_ADMIN',
    1,
    1,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system'
); 