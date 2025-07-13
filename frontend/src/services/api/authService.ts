import apiClient from './client';
import { LoginRequest, LoginResponse, RegisterResponse, User } from './types';

class AuthService {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
    
    // Store token and user data
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      
      // Create user object from login response
      const user: User = {
        id: response.data.id,
        email: response.data.email,
        firstName: response.data.firstName,
        lastName: response.data.lastName,
        role: response.data.role,
        companyId: response.data.companyId,
        departmentId: response.data.departmentId,
      };
      localStorage.setItem('user', JSON.stringify(user));
    }
    
    return response.data;
  }

  async register(userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    companyId: number;
  }): Promise<RegisterResponse> {
    const response = await apiClient.post<RegisterResponse>('/auth/register', userData);
    
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      
      // Create user object from register response
      const user: User = {
        id: response.data.id,
        email: response.data.email,
        firstName: response.data.firstName,
        lastName: response.data.lastName,
        role: response.data.role,
        companyId: response.data.companyId,
        departmentId: response.data.departmentId,
      };
      localStorage.setItem('user', JSON.stringify(user));
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

  // Method to fetch fresh user profile from API
  async fetchUserProfile(): Promise<User> {
    const response = await apiClient.get<User>('/users/profile');
    const user = response.data;
    localStorage.setItem('user', JSON.stringify(user));
    return user;
  }
}

const authService = new AuthService();
export default authService; 