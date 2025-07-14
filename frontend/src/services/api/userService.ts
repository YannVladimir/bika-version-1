import apiClient from './client';
import { User, CreateUserRequest } from './types';

class UserService {
  async getUsers(): Promise<User[]> {
    const response = await apiClient.get<User[]>('/v1/users');
    return response.data;
  }

  async getUsersByCompany(companyId: number): Promise<User[]> {
    const response = await apiClient.get<User[]>(`/v1/users/company/${companyId}`);
    return response.data;
  }

  async getUsersByDepartment(departmentId: number): Promise<User[]> {
    const response = await apiClient.get<User[]>(`/v1/users/department/${departmentId}`);
    return response.data;
  }

  async getUserById(id: number): Promise<User> {
    const response = await apiClient.get<User>(`/v1/users/${id}`);
    return response.data;
  }

  async getUserProfile(): Promise<User> {
    const response = await apiClient.get<User>('/v1/users/profile');
    return response.data;
  }

  async createUser(userData: CreateUserRequest): Promise<User> {
    const response = await apiClient.post<User>('/v1/users', userData);
    return response.data;
  }

  async updateUser(id: number, userData: Partial<User>): Promise<User> {
    const response = await apiClient.put<User>(`/v1/users/${id}`, userData);
    return response.data;
  }

  async updateUserProfile(userData: Partial<User>): Promise<User> {
    const response = await apiClient.put<User>('/v1/users/profile', userData);
    return response.data;
  }

  async activateUser(id: number): Promise<User> {
    const response = await apiClient.patch<User>(`/v1/users/${id}/activate`);
    return response.data;
  }

  async deactivateUser(id: number): Promise<User> {
    const response = await apiClient.patch<User>(`/v1/users/${id}/deactivate`);
    return response.data;
  }

  async deleteUser(id: number): Promise<void> {
    await apiClient.delete(`/v1/users/${id}`);
  }
}

const userService = new UserService();
export default userService; 