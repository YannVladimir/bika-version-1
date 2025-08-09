-- Create physical storage lookup tables for standardization

-- Create rooms lookup table
CREATE TABLE IF NOT EXISTS physical_storage_rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- Create colors lookup table (for file and section colors)
CREATE TABLE IF NOT EXISTS physical_storage_colors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    hex_value VARCHAR(7), -- For storing hex color codes like #FF0000
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

-- Insert default rooms
INSERT INTO physical_storage_rooms (name, description, created_by, updated_by) VALUES
('Archive Room 1', 'Main archival storage room', 'system', 'system'),
('Archive Room 2', 'Secondary archival storage room', 'system', 'system'),
('Main Office', 'Main office storage area', 'system', 'system'),
('Storage Room A', 'General storage room A', 'system', 'system'),
('Storage Room B', 'General storage room B', 'system', 'system'),
('Basement Archive', 'Basement level document storage', 'system', 'system'),
('Second Floor Archive', 'Second floor document storage', 'system', 'system')
ON CONFLICT (name) DO NOTHING;

-- Insert default colors
INSERT INTO physical_storage_colors (name, hex_value, created_by, updated_by) VALUES
('Red', '#FF0000', 'system', 'system'),
('Blue', '#0000FF', 'system', 'system'),
('Green', '#008000', 'system', 'system'),
('Yellow', '#FFFF00', 'system', 'system'),
('Black', '#000000', 'system', 'system'),
('White', '#FFFFFF', 'system', 'system'),
('Orange', '#FFA500', 'system', 'system'),
('Purple', '#800080', 'system', 'system'),
('Brown', '#A52A2A', 'system', 'system'),
('Gray', '#808080', 'system', 'system')
ON CONFLICT (name) DO NOTHING;

-- Create index for better performance
CREATE INDEX IF NOT EXISTS idx_physical_storage_rooms_active ON physical_storage_rooms(is_active);
CREATE INDEX IF NOT EXISTS idx_physical_storage_colors_active ON physical_storage_colors(is_active); 