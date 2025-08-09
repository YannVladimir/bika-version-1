-- Add missing columns to physical storage tables if they don't exist

-- Add columns to physical_storage_rooms if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'physical_storage_rooms' 
        AND column_name = 'created_by'
    ) THEN
        ALTER TABLE physical_storage_rooms ADD COLUMN created_by VARCHAR(255) DEFAULT 'system' NOT NULL;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'physical_storage_rooms' 
        AND column_name = 'updated_by'
    ) THEN
        ALTER TABLE physical_storage_rooms ADD COLUMN updated_by VARCHAR(255) DEFAULT 'system' NOT NULL;
    END IF;
END $$;

-- Add columns to physical_storage_colors if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'physical_storage_colors' 
        AND column_name = 'created_by'
    ) THEN
        ALTER TABLE physical_storage_colors ADD COLUMN created_by VARCHAR(255) DEFAULT 'system' NOT NULL;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'physical_storage_colors' 
        AND column_name = 'updated_by'
    ) THEN
        ALTER TABLE physical_storage_colors ADD COLUMN updated_by VARCHAR(255) DEFAULT 'system' NOT NULL;
    END IF;
END $$; 