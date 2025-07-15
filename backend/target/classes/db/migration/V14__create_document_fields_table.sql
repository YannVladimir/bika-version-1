-- Create document_fields table
CREATE TABLE document_fields (
    id BIGSERIAL PRIMARY KEY,
    document_type_id BIGINT NOT NULL REFERENCES document_types(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    field_key VARCHAR(100) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    required BOOLEAN NOT NULL DEFAULT false,
    description TEXT,
    default_value TEXT,
    validation_rules TEXT,
    display_order INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- Create document_field_options table for select field options
CREATE TABLE document_field_options (
    field_id BIGINT NOT NULL REFERENCES document_fields(id) ON DELETE CASCADE,
    option_value VARCHAR(255) NOT NULL,
    PRIMARY KEY (field_id, option_value)
);

-- Remove metadata_schema column from document_types table (since we're using proper entities now)
ALTER TABLE document_types DROP COLUMN IF EXISTS metadata_schema;

-- Create indexes for better performance
CREATE INDEX idx_document_fields_document_type_id ON document_fields(document_type_id);
CREATE INDEX idx_document_fields_display_order ON document_fields(display_order);
CREATE INDEX idx_document_field_options_field_id ON document_field_options(field_id); 