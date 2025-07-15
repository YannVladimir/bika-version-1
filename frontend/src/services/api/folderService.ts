import apiClient from './client';
import { Folder, CreateFolderRequest, ApiResponse } from './types';

class FolderService {
  private readonly basePath = '/v1/folders';

  // Get root folders by company
  async getRootFoldersByCompany(companyId: number): Promise<Folder[]> {
    const response = await apiClient.get<Folder[]>(`${this.basePath}/company/${companyId}/root`);
    return response.data;
  }

  // Get folder by ID with contents
  async getFolderContents(id: number): Promise<Folder> {
    const response = await apiClient.get<Folder>(`${this.basePath}/${id}/contents`);
    return response.data;
  }

  // Get folder by ID
  async getFolderById(id: number): Promise<Folder> {
    const response = await apiClient.get<Folder>(`${this.basePath}/${id}`);
    return response.data;
  }

  // Create a new folder
  async createFolder(folder: CreateFolderRequest): Promise<Folder> {
    const response = await apiClient.post<Folder>(this.basePath, folder);
    return response.data;
  }

  // Delete folder
  async deleteFolder(id: number): Promise<void> {
    await apiClient.delete(`${this.basePath}/${id}`);
  }

  // Helper method to generate folder key from name
  generateFolderKey(name: string): string {
    return name
      .toLowerCase()
      .replace(/[^a-z0-9]/g, '_')
      .replace(/_+/g, '_')
      .replace(/^_|_$/g, '');
  }

  // Helper method to create new folder with defaults
  createNewFolder(name: string, companyId: number, parentId?: number, departmentId?: number): CreateFolderRequest {
    return {
      name,
      companyId,
      parentId,
      departmentId,
      description: '',
    };
  }
}

const folderService = new FolderService();
export default folderService; 