import apiClient from './client';
import { Document, CreateDocumentRequest, ApiResponse } from './types';

class DocumentService {
  private readonly basePath = '/v1/documents';

  // Get documents by folder
  async getDocumentsByFolder(folderId: number): Promise<Document[]> {
    const response = await apiClient.get<Document[]>(`${this.basePath}/folder/${folderId}`);
    return response.data;
  }

  // Get documents by company
  async getDocumentsByCompany(companyId: number): Promise<Document[]> {
    const response = await apiClient.get<Document[]>(`${this.basePath}/company/${companyId}`);
    return response.data;
  }

  // Get document by ID
  async getDocumentById(id: number): Promise<Document> {
    const response = await apiClient.get<Document>(`${this.basePath}/${id}`);
    return response.data;
  }

  // Create a new document
  async createDocument(document: CreateDocumentRequest): Promise<Document> {
    const response = await apiClient.post<Document>(this.basePath, document);
    return response.data;
  }

  // Delete document
  async deleteDocument(id: number): Promise<void> {
    await apiClient.delete(`${this.basePath}/${id}`);
  }

  // Download document file
  async downloadDocument(id: number): Promise<Blob> {
    const response = await apiClient.get(`${this.basePath}/${id}/download`, {
      responseType: 'blob'
    });
    return response.data;
  }

  // Helper method to create new document with defaults
  createNewDocument(
    name: string, 
    documentTypeId: number, 
    companyId: number, 
    folderId?: number, 
    departmentId?: number
  ): CreateDocumentRequest {
    // Generate a unique code based on name and timestamp
    const timestamp = Date.now();
    const code = `${name.replace(/[^a-zA-Z0-9]/g, '').toUpperCase()}_${timestamp}`;
    
    return {
      name,
      code,
      documentTypeId,
      companyId,
      folderId,
      departmentId,
      fieldValues: {},
      status: 'DRAFT',
    };
  }

  // Helper method to prepare document for upload with file info
  prepareDocumentForUpload(
    document: CreateDocumentRequest,
    file: File
  ): CreateDocumentRequest {
    return {
      ...document,
      filePath: `/uploads/${file.name}`,
      fileSize: file.size,
      mimeType: file.type,
      physicalLocation: JSON.stringify({ fileName: file.name, uploadTime: new Date().toISOString() }),
    };
  }
}

const documentService = new DocumentService();
export default documentService; 