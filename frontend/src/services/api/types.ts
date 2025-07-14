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
  role: string;
  id: number;
  firstName: string;
  lastName: string;
  companyId: number;
  departmentId?: number;
}

export interface RegisterResponse {
  token: string;
  email: string;
  role: string;
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
export interface Document {
  id: number;
  name: string;
  type: string;
  size: number;
  createdAt: string;
  updatedAt: string;
  folderId?: number;
  documentTypeId: number;
}

export interface DocumentType {
  id: number;
  name: string;
  description?: string;
  retentionPeriod: number;
  isActive: boolean;
} 