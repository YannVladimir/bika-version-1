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

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  companyId: number;
  departmentId?: number;
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
  description: string;
  retentionPeriod: number;
  isActive: boolean;
} 