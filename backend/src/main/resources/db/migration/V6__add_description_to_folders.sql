-- Add missing description column to folders table
ALTER TABLE folders 
ADD COLUMN description TEXT; 