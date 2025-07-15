-- Add APPROVED status to the document_status enum type
ALTER TYPE document_status ADD VALUE 'APPROVED' BEFORE 'ACTIVE'; 