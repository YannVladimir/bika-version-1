-- Fix created_by and updated_by columns to be consistent with other tables
-- Convert from BIGINT references to VARCHAR(255) to match BaseEntity

-- Create ProjectStatus enum
CREATE TYPE project_status AS ENUM ('PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED');

-- Update task_status enum to match TaskStatus entity
ALTER TYPE task_status ADD VALUE 'COMPLETED';
ALTER TYPE task_status ADD VALUE 'CANCELLED';

-- Add missing columns to projects table to match Project entity
ALTER TABLE projects ADD COLUMN code VARCHAR(255);
ALTER TABLE projects ADD COLUMN start_date TIMESTAMP WITH TIME ZONE;
ALTER TABLE projects ADD COLUMN end_date TIMESTAMP WITH TIME ZONE;
ALTER TABLE projects ADD COLUMN status project_status;
ALTER TABLE projects ADD COLUMN is_active BOOLEAN DEFAULT true;
ALTER TABLE projects ADD COLUMN updated_by VARCHAR(255);

-- Make code unique and not null (after adding the column)
UPDATE projects SET code = 'PROJ-' || id WHERE code IS NULL;
ALTER TABLE projects ALTER COLUMN code SET NOT NULL;
ALTER TABLE projects ADD CONSTRAINT projects_code_unique UNIQUE (code);

-- Set default status
UPDATE projects SET status = 'PLANNING' WHERE status IS NULL;
ALTER TABLE projects ALTER COLUMN status SET NOT NULL;

-- Change created_by from BIGINT to VARCHAR(255) in projects table
ALTER TABLE projects DROP CONSTRAINT projects_created_by_fkey;
ALTER TABLE projects ALTER COLUMN created_by TYPE VARCHAR(255) USING 'system';
ALTER TABLE projects ALTER COLUMN created_by SET NOT NULL;

-- Add missing columns to tasks table to match Task entity
ALTER TABLE tasks ADD COLUMN completed_date TIMESTAMP WITH TIME ZONE;
ALTER TABLE tasks ADD COLUMN start_date TIMESTAMP WITH TIME ZONE;
ALTER TABLE tasks ADD COLUMN is_active BOOLEAN DEFAULT true;
ALTER TABLE tasks ADD COLUMN company_id BIGINT REFERENCES companies(id) ON DELETE CASCADE;
ALTER TABLE tasks ADD COLUMN department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL;
ALTER TABLE tasks ADD COLUMN creator_id BIGINT REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE tasks ADD COLUMN updated_by VARCHAR(255);

-- Set default values for new columns in tasks (while created_by is still BIGINT)
UPDATE tasks SET is_active = true WHERE is_active IS NULL;
UPDATE tasks SET company_id = (SELECT company_id FROM projects WHERE projects.id = tasks.project_id LIMIT 1);
UPDATE tasks SET creator_id = created_by WHERE creator_id IS NULL;

-- Change created_by from BIGINT to VARCHAR(255) in tasks table
ALTER TABLE tasks DROP CONSTRAINT tasks_created_by_fkey;
ALTER TABLE tasks ALTER COLUMN created_by TYPE VARCHAR(255) USING 'system';
ALTER TABLE tasks ALTER COLUMN created_by SET NOT NULL;

-- Set default values for the new updated_by columns
UPDATE projects SET updated_by = 'system' WHERE updated_by IS NULL;
UPDATE tasks SET updated_by = 'system' WHERE updated_by IS NULL;

-- Make updated_by columns NOT NULL
ALTER TABLE projects ALTER COLUMN updated_by SET NOT NULL;
ALTER TABLE tasks ALTER COLUMN updated_by SET NOT NULL;

-- Add indexes for new foreign keys
CREATE INDEX idx_tasks_company_id ON tasks(company_id);
CREATE INDEX idx_tasks_department_id ON tasks(department_id);
CREATE INDEX idx_tasks_creator_id ON tasks(creator_id); 