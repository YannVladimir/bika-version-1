import apiClient from './client';
import { DocumentType, DocumentField, FieldType } from './types';

class DocumentTypeService {
  async getDocumentTypes(): Promise<DocumentType[]> {
    const response = await apiClient.get<DocumentType[]>('/v1/document-types');
    return response.data;
  }

  async getDocumentTypesByCompany(companyId: number): Promise<DocumentType[]> {
    const response = await apiClient.get<DocumentType[]>(`/v1/document-types/company/${companyId}`);
    return response.data;
  }

  async getDocumentTypeById(id: number): Promise<DocumentType> {
    const response = await apiClient.get<DocumentType>(`/v1/document-types/${id}`);
    return response.data;
  }

  async createDocumentType(documentTypeData: Partial<DocumentType>): Promise<DocumentType> {
    const response = await apiClient.post<DocumentType>('/v1/document-types', documentTypeData);
    return response.data;
  }

  async updateDocumentType(id: number, documentTypeData: Partial<DocumentType>): Promise<DocumentType> {
    const response = await apiClient.put<DocumentType>(`/v1/document-types/${id}`, documentTypeData);
    return response.data;
  }

  async deleteDocumentType(id: number): Promise<void> {
    await apiClient.delete(`/v1/document-types/${id}`);
  }

  // Helper method to generate field key from name
  generateFieldKey(name: string): string {
    return name
      .toLowerCase()
      .replace(/[^a-z0-9\s]/g, '') // Remove special characters
      .replace(/\s+/g, '_') // Replace spaces with underscores
      .trim();
  }

  // Helper method to create a new field with default values
  createNewField(name: string, type: FieldType): DocumentField {
    return {
      name,
      fieldKey: this.generateFieldKey(name),
      fieldType: type,
      required: false,
      active: true,
      options: type === 'SELECT' ? [] : undefined,
    };
  }
}

const documentTypeService = new DocumentTypeService();
export default documentTypeService; 