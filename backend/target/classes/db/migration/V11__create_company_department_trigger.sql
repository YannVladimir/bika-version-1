-- Create a function that will be called by the trigger
CREATE OR REPLACE FUNCTION create_administration_department()
RETURNS TRIGGER AS $$
BEGIN
    -- Insert the Administration department for the newly created company
    INSERT INTO departments (
        name, 
        code, 
        company_id, 
        is_active, 
        created_at, 
        created_by, 
        updated_at, 
        updated_by
    ) VALUES (
        'Administration',
        CONCAT(NEW.code, '_ADMIN'),
        NEW.id,
        true,
        NOW(),
        COALESCE(NEW.created_by, 'SYSTEM'),
        NOW(),
        COALESCE(NEW.created_by, 'SYSTEM')
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger that fires after a company is inserted
CREATE TRIGGER trigger_create_administration_department
    AFTER INSERT ON companies
    FOR EACH ROW
    EXECUTE FUNCTION create_administration_department();

-- Add comments for documentation
COMMENT ON FUNCTION create_administration_department() IS 'Automatically creates an Administration department when a new company is added';
COMMENT ON TRIGGER trigger_create_administration_department ON companies IS 'Trigger to automatically create Administration department for new companies'; 