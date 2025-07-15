-- V17: Create Drive Tables
-- This migration creates tables for personal drive functionality
-- separate from the document archival system

-- Create drive_folders table for personal drive folder structure
CREATE TABLE drive_folders (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    path TEXT NOT NULL,
    description TEXT,
    parent_id BIGINT REFERENCES drive_folders(id) ON DELETE CASCADE,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- Create drive_files table for personal drive files
CREATE TABLE drive_files (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_extension VARCHAR(10),
    folder_id BIGINT REFERENCES drive_folders(id) ON DELETE SET NULL,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    download_count BIGINT DEFAULT 0,
    last_accessed TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- Create user_storage_quota table to track storage usage per user
CREATE TABLE user_storage_quota (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    max_storage_bytes BIGINT NOT NULL DEFAULT 2147483648, -- 2GB default
    used_storage_bytes BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    UNIQUE(user_id, company_id)
);

-- Create indexes for better performance
CREATE INDEX idx_drive_folders_parent_id ON drive_folders(parent_id);
CREATE INDEX idx_drive_folders_user_id ON drive_folders(user_id);
CREATE INDEX idx_drive_folders_company_id ON drive_folders(company_id);
CREATE INDEX idx_drive_folders_path ON drive_folders(path);

CREATE INDEX idx_drive_files_folder_id ON drive_files(folder_id);
CREATE INDEX idx_drive_files_user_id ON drive_files(user_id);
CREATE INDEX idx_drive_files_company_id ON drive_files(company_id);
CREATE INDEX idx_drive_files_mime_type ON drive_files(mime_type);
CREATE INDEX idx_drive_files_is_deleted ON drive_files(is_deleted);

CREATE INDEX idx_user_storage_quota_user_id ON user_storage_quota(user_id);
CREATE INDEX idx_user_storage_quota_company_id ON user_storage_quota(company_id);

-- Add trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_drive_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_drive_folders_updated_at BEFORE UPDATE ON drive_folders FOR EACH ROW EXECUTE PROCEDURE update_drive_updated_at_column();
CREATE TRIGGER update_drive_files_updated_at BEFORE UPDATE ON drive_files FOR EACH ROW EXECUTE PROCEDURE update_drive_updated_at_column();
CREATE TRIGGER update_user_storage_quota_updated_at BEFORE UPDATE ON user_storage_quota FOR EACH ROW EXECUTE PROCEDURE update_drive_updated_at_column(); 