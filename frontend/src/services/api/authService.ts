import apiClient from './client';
import { LoginRequest, LoginResponse, User } from './types';

class AuthService {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
    
    // Store token
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      // For now, create a mock user until we have a user info endpoint
      const mockUser: User = {
        id: 1,
        email: credentials.email,
        firstName: 'Super',
        lastName: 'Admin',
        role: 'SUPER_ADMIN', // Changed to SUPER_ADMIN for testing
        companyId: 1,
      };
      localStorage.setItem('user', JSON.stringify(mockUser));
    }
    
    return response.data;
  }

  async register(userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    companyId: number;
  }): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>('/auth/register', userData);
    
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      const mockUser: User = {
        id: 1,
        email: userData.email,
        firstName: userData.firstName,
        lastName: userData.lastName,
        role: 'USER',
        companyId: userData.companyId,
      };
      localStorage.setItem('user', JSON.stringify(mockUser));
    }
    
    return response.data;
  }

  async logout(): Promise<void> {
    try {
      await apiClient.post('/auth/logout');
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  }

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

const authService = new AuthService();
export default authService; 