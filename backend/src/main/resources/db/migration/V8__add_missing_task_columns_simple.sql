-- Simple migration to add missing completed_date column
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS completed_date TIMESTAMP WITH TIME ZONE;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS start_date TIMESTAMP WITH TIME ZONE;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS company_id BIGINT;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS department_id BIGINT;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS creator_id BIGINT;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);

-- Set default values
UPDATE tasks SET is_active = true WHERE is_active IS NULL;
UPDATE tasks SET updated_by = 'system' WHERE updated_by IS NULL;

-- Fix created_by column type if it's still BIGINT
DO $$
BEGIN
    -- Check if created_by is still BIGINT and convert it
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'tasks' AND column_name = 'created_by' 
               AND data_type = 'bigint') THEN
        -- First populate creator_id with the current created_by value
        UPDATE tasks SET creator_id = created_by WHERE creator_id IS NULL;
        
        -- Drop the foreign key constraint if it exists
        IF EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'tasks_created_by_fkey') THEN
            ALTER TABLE tasks DROP CONSTRAINT tasks_created_by_fkey;
        END IF;
        
        -- Convert created_by to VARCHAR
        ALTER TABLE tasks ALTER COLUMN created_by TYPE VARCHAR(255) USING 'system';
    END IF;
END
$$;

-- Add foreign key constraints if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'tasks_company_id_fkey') THEN
        ALTER TABLE tasks ADD CONSTRAINT tasks_company_id_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'tasks_department_id_fkey') THEN
        ALTER TABLE tasks ADD CONSTRAINT tasks_department_id_fkey FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'tasks_creator_id_fkey') THEN
        ALTER TABLE tasks ADD CONSTRAINT tasks_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END
$$; 