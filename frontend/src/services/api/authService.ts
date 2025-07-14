import apiClient from './client';
import { LoginRequest, LoginResponse, User } from './types';

class AuthService {
  private readonly TOKEN_KEY = 'token';
  private readonly USER_KEY = 'user';

  // Remove the constructor that was clearing storage on initialization
  // constructor() {
  //   // Clear any existing tokens on initialization to prevent auth issues
  //   this.clearStorage();
  // }

  private clearStorage(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
    
    // Store the token and user data
    localStorage.setItem(this.TOKEN_KEY, response.data.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify({
      id: response.data.id,
      email: response.data.email,
      firstName: response.data.firstName,
      lastName: response.data.lastName,
      role: response.data.role,
      companyId: response.data.companyId,
      departmentId: response.data.departmentId,
    }));
    
    return response.data;
  }

  async logout(): Promise<void> {
    try {
      await apiClient.post('/auth/logout');
    } catch (error) {
      // Even if logout fails on server, clear local storage
      console.warn('Logout request failed, but clearing local storage anyway');
    } finally {
      this.clearStorage();
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUser(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  async fetchUserProfile(): Promise<User> {
    const response = await apiClient.get<User>('/v1/users/profile');
    
    // Update stored user data with fresh profile data
    const currentUser = this.getUser();
    if (currentUser) {
      const updatedUser = {
        ...currentUser,
        ...response.data
      };
      localStorage.setItem(this.USER_KEY, JSON.stringify(updatedUser));
    }
    
    return response.data;
  }
}

const authService = new AuthService();
export default authService; 