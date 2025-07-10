import apiClient from './client';
import { Company, CreateCompanyRequest, PaginatedResponse } from './types';

class CompanyService {
  async getCompanies(page = 0, size = 10, search?: string): Promise<PaginatedResponse<Company>> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (search) {
      params.append('search', search);
    }

    const response = await apiClient.get<PaginatedResponse<Company>>(`/companies?${params}`);
    return response.data;
  }

  async getCompanyById(id: number): Promise<Company> {
    const response = await apiClient.get<Company>(`/companies/${id}`);
    return response.data;
  }

  async createCompany(companyData: CreateCompanyRequest): Promise<Company> {
    const response = await apiClient.post<Company>('/companies', companyData);
    return response.data;
  }

  async updateCompany(id: number, companyData: Partial<CreateCompanyRequest>): Promise<Company> {
    const response = await apiClient.put<Company>(`/companies/${id}`, companyData);
    return response.data;
  }

  async deleteCompany(id: number): Promise<void> {
    await apiClient.delete(`/companies/${id}`);
  }

  async activateCompany(id: number): Promise<Company> {
    const response = await apiClient.patch<Company>(`/companies/${id}/activate`);
    return response.data;
  }

  async deactivateCompany(id: number): Promise<Company> {
    const response = await apiClient.patch<Company>(`/companies/${id}/deactivate`);
    return response.data;
  }
}

const companyService = new CompanyService();
export default companyService; 