// Common API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Error types
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
  path: string;
}

// Auth types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  role: UserRole;
  id: number;
  firstName: string;
  lastName: string;
  companyId: number;
  departmentId?: number;
}

export interface RegisterResponse {
  token: string;
  email: string;
  role: UserRole;
  id: number;
  firstName: string;
  lastName: string;
  companyId: number;
  departmentId?: number;
}

// User types - Enhanced to match backend
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  companyId: number;
  departmentId?: number;
  active: boolean;
  lastLogin?: string;
  createdAt: string;
  updatedAt: string;
}

export enum UserRole {
  SUPER_ADMIN = 'SUPER_ADMIN',
  COMPANY_ADMIN = 'COMPANY_ADMIN',
  MANAGER = 'MANAGER',
  USER = 'USER'
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  companyId: number;
  departmentId?: number;
  role: UserRole;
}

// Department types
export interface Department {
  id: number;
  name: string;
  code: string;
  description?: string;
  companyId: number;
  parentId?: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

// Company types
export interface Company {
  id: number;
  name: string;
  code: string;
  email: string;
  phone?: string;
  address?: string;
  description?: string;
  isActive: boolean;
}

export interface CreateCompanyRequest {
  name: string;
  code: string;
  email: string;
  phone?: string;
  address?: string;
  description?: string;
  isActive: boolean;
}

// Document types

// Field types for document type fields
export type FieldType = 'TEXT' | 'TEXTAREA' | 'NUMBER' | 'DATE' | 'SELECT' | 'CHECKBOX' | 'EMAIL' | 'PHONE' | 'URL';

// Document field interface
export interface DocumentField {
  id?: number;
  name: string;
  fieldKey: string;
  fieldType: FieldType;
  required: boolean;
  description?: string;
  defaultValue?: string;
  validationRules?: string;
  options?: string[];
  displayOrder?: number;
  active: boolean;
}

// Enhanced Document interface to match backend DTO
export interface Document {
  id: number;
  name: string;
  code: string;
  companyId: number;
  departmentId?: number;
  folderId?: number;
  documentTypeId?: number;
  documentTypeName?: string;
  filePath?: string;
  fileSize?: number;
  mimeType?: string;
  metadata?: { [key: string]: any }; // Dynamic field values
  status?: 'DRAFT' | 'ACTIVE' | 'ARCHIVED' | 'DELETED';
  physicalLocation?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
}

// Folder interface to match backend DTO
export interface Folder {
  id: number;
  name: string;
  path: string;
  description?: string;
  parentId?: number;
  companyId: number;
  departmentId?: number;
  children?: Folder[];
  documents?: Document[];
  isActive?: boolean;
}

// Create document request to match backend
export interface CreateDocumentRequest {
  name: string;
  code: string;
  companyId: number;
  departmentId?: number;
  folderId?: number;
  documentTypeId: number;
  filePath?: string;
  fileSize?: number;
  mimeType?: string;
  fieldValues?: { [key: string]: any }; // Dynamic field values
  status?: 'DRAFT' | 'ACTIVE' | 'ARCHIVED' | 'DELETED';
  physicalLocation?: string;
}

// Create folder request
export interface CreateFolderRequest {
  name: string;
  description?: string;
  parentId?: number;
  companyId: number;
  departmentId?: number;
}

// Physical storage structure for frontend
export interface PhysicalStorage {
  room: string;
  cupboard: string;
  drawer: string;
  fileNumber: string;
  fileColor: string;
  documentNumber: string;
  fileSection: string;
  sectionColor: string;
  fileName?: string;
  uploadTime?: string;
}

// Physical storage lookup data
export interface PhysicalStorageLookup {
  rooms: string[];
  fileColors: string[];
  sectionColors: string[];
}

export interface DocumentType {
  id?: number;
  name: string;
  code: string;
  description?: string;
  companyId: number;
  fields: DocumentField[];
  isActive: boolean;
} 