-- First remove the default value that depends on the enum type
ALTER TABLE documents ALTER COLUMN status DROP DEFAULT;

-- Change status column from document_status enum to varchar
ALTER TABLE documents ALTER COLUMN status TYPE VARCHAR(50) USING status::text;

-- Drop the document_status enum type since we're no longer using it
DROP TYPE document_status; 