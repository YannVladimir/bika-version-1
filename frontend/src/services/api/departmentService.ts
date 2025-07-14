import apiClient from './client';
import { Department } from './types';

class DepartmentService {
  async getDepartments(): Promise<Department[]> {
    const response = await apiClient.get<Department[]>('/v1/departments');
    return response.data;
  }

  async getDepartmentsByCompany(companyId: number): Promise<Department[]> {
    const response = await apiClient.get<Department[]>(`/v1/departments/company/${companyId}`);
    return response.data;
  }

  async getDepartmentById(id: number): Promise<Department> {
    const response = await apiClient.get<Department>(`/v1/departments/${id}`);
    return response.data;
  }

  async createDepartment(departmentData: Partial<Department>): Promise<Department> {
    const response = await apiClient.post<Department>('/v1/departments', departmentData);
    return response.data;
  }

  async updateDepartment(id: number, departmentData: Partial<Department>): Promise<Department> {
    const response = await apiClient.put<Department>(`/v1/departments/${id}`, departmentData);
    return response.data;
  }

  async deleteDepartment(id: number): Promise<void> {
    await apiClient.delete(`/v1/departments/${id}`);
  }
}

const departmentService = new DepartmentService();
export default departmentService; 