import apiClient from './client';
import { Company, CreateCompanyRequest } from './types';

class CompanyService {
  async getCompanies(): Promise<Company[]> {
    const response = await apiClient.get<Company[]>('/v1/companies');
    return response.data;
  }

  async getCompanyById(id: number): Promise<Company> {
    const response = await apiClient.get<Company>(`/v1/companies/${id}`);
    return response.data;
  }

  async createCompany(companyData: CreateCompanyRequest): Promise<Company> {
    const response = await apiClient.post<Company>('/v1/companies', companyData);
    return response.data;
  }

  async updateCompany(id: number, companyData: CreateCompanyRequest): Promise<Company> {
    const response = await apiClient.put<Company>(`/v1/companies/${id}`, companyData);
    return response.data;
  }

  async deleteCompany(id: number): Promise<void> {
    await apiClient.delete(`/v1/companies/${id}`);
  }
}

const companyService = new CompanyService();
export default companyService; 