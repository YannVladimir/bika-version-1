import apiClient from './client';

// Drive Types
export interface DriveFolder {
  id: number;
  name: string;
  path: string;
  description?: string;
  parentId?: number;
  companyId: number;
  departmentId?: number;
  userId: number;
  children: DriveFolder[];
  files: DriveFile[];
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
}

export interface DriveFile {
  id: number;
  name: string;
  originalFilename: string;
  filePath: string;
  fileSize: number;
  mimeType: string;
  fileExtension?: string;
  folderId?: number;
  folderName?: string;
  companyId: number;
  departmentId?: number;
  userId: number;
  isActive: boolean;
  isDeleted: boolean;
  downloadCount: number;
  lastAccessed?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
}

export interface UserStorageQuota {
  id: number;
  userId: number;
  companyId: number;
  maxStorageBytes: number;
  usedStorageBytes: number;
  usedStorageMB: number;
  maxStorageMB: number;
  usagePercentage: number;
  availableBytes: number;
  availableMB: number;
}

export interface CreateDriveFolderRequest {
  name: string;
  description?: string;
  parentId?: number;
}

export interface CreateDriveFileRequest {
  name: string;
  originalFilename: string;
  filePath: string;
  fileSize: number;
  mimeType: string;
  fileExtension?: string;
  folderId?: number;
}

class DriveService {
  private readonly basePath = '/v1/drive';

  // Storage Quota
  async getStorageQuota(): Promise<UserStorageQuota> {
    const response = await apiClient.get<UserStorageQuota>(`${this.basePath}/storage/quota`);
    return response.data;
  }

  // Folder Operations
  async getRootFolders(): Promise<DriveFolder[]> {
    const response = await apiClient.get<DriveFolder[]>(`${this.basePath}/folders/root`);
    return response.data;
  }

  async getFolderById(id: number): Promise<DriveFolder> {
    const response = await apiClient.get<DriveFolder>(`${this.basePath}/folders/${id}`);
    return response.data;
  }

  async createFolder(request: CreateDriveFolderRequest): Promise<DriveFolder> {
    const response = await apiClient.post<DriveFolder>(`${this.basePath}/folders`, request);
    return response.data;
  }

  async updateFolder(id: number, request: CreateDriveFolderRequest): Promise<DriveFolder> {
    const response = await apiClient.put<DriveFolder>(`${this.basePath}/folders/${id}`, request);
    return response.data;
  }

  async deleteFolder(id: number): Promise<void> {
    await apiClient.delete(`${this.basePath}/folders/${id}`);
  }

  // File Operations
  async getAllFiles(): Promise<DriveFile[]> {
    const response = await apiClient.get<DriveFile[]>(`${this.basePath}/files`);
    return response.data;
  }

  async getFilesByFolder(folderId?: number): Promise<DriveFile[]> {
    const url = folderId 
      ? `${this.basePath}/files/folder?folderId=${folderId}`
      : `${this.basePath}/files/folder`;
    const response = await apiClient.get<DriveFile[]>(url);
    return response.data;
  }

  async getFileById(id: number): Promise<DriveFile> {
    const response = await apiClient.get<DriveFile>(`${this.basePath}/files/${id}`);
    return response.data;
  }

  async uploadFile(request: CreateDriveFileRequest): Promise<DriveFile> {
    const response = await apiClient.post<DriveFile>(`${this.basePath}/files`, request);
    return response.data;
  }

  async updateFile(id: number, request: CreateDriveFileRequest): Promise<DriveFile> {
    const response = await apiClient.put<DriveFile>(`${this.basePath}/files/${id}`, request);
    return response.data;
  }

  async deleteFile(id: number): Promise<void> {
    await apiClient.delete(`${this.basePath}/files/${id}`);
  }

  async downloadFile(id: number): Promise<Blob> {
    const response = await apiClient.get(`${this.basePath}/files/${id}/download`, {
      responseType: 'blob'
    });
    return response.data;
  }

  async getFilesByType(mimeType: string): Promise<DriveFile[]> {
    const response = await apiClient.get<DriveFile[]>(`${this.basePath}/files/type/${encodeURIComponent(mimeType)}`);
    return response.data;
  }

  // Helper Methods
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  getFileIcon(mimeType: string): string {
    if (!mimeType) return 'InsertDriveFile';
    
    if (mimeType.startsWith('image/')) return 'Image';
    if (mimeType.startsWith('video/')) return 'VideoFile';
    if (mimeType.startsWith('audio/')) return 'AudioFile';
    if (mimeType === 'application/pdf') return 'PictureAsPdf';
    if (mimeType.includes('word') || mimeType.includes('document')) return 'Description';
    if (mimeType.includes('excel') || mimeType.includes('spreadsheet')) return 'TableChart';
    if (mimeType.includes('powerpoint') || mimeType.includes('presentation')) return 'Slideshow';
    if (mimeType.startsWith('text/')) return 'TextSnippet';
    if (mimeType.includes('zip') || mimeType.includes('rar') || mimeType.includes('archive')) return 'Archive';
    
    return 'InsertDriveFile';
  }

  getFileTypeColor(mimeType: string): string {
    if (!mimeType) return '#666';
    
    if (mimeType.startsWith('image/')) return '#4CAF50';
    if (mimeType.startsWith('video/')) return '#F44336';
    if (mimeType.startsWith('audio/')) return '#9C27B0';
    if (mimeType === 'application/pdf') return '#F44336';
    if (mimeType.includes('word') || mimeType.includes('document')) return '#2196F3';
    if (mimeType.includes('excel') || mimeType.includes('spreadsheet')) return '#4CAF50';
    if (mimeType.includes('powerpoint') || mimeType.includes('presentation')) return '#FF9800';
    if (mimeType.startsWith('text/')) return '#607D8B';
    if (mimeType.includes('zip') || mimeType.includes('rar') || mimeType.includes('archive')) return '#795548';
    
    return '#666';
  }

  createFileFromBrowser(file: File, folderId?: number): CreateDriveFileRequest {
    // Generate file extension
    const extension = file.name.split('.').pop()?.toLowerCase() || '';
    
    return {
      name: file.name,
      originalFilename: file.name,
      filePath: `/drive/uploads/${file.name}`, // This would be updated by actual file storage
      fileSize: file.size,
      mimeType: file.type || 'application/octet-stream',
      fileExtension: extension,
      folderId
    };
  }

  isFileTypeSupported(mimeType: string): boolean {
    // Support common file types
    const supportedTypes = [
      // Documents
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'application/vnd.ms-powerpoint',
      'application/vnd.openxmlformats-officedocument.presentationml.presentation',
      
      // Text files
      'text/plain',
      'text/csv',
      'application/json',
      'application/xml',
      
      // Images
      'image/jpeg',
      'image/png',
      'image/gif',
      'image/webp',
      'image/svg+xml',
      
      // Videos
      'video/mp4',
      'video/avi',
      'video/mov',
      'video/wmv',
      'video/webm',
      
      // Audio
      'audio/mp3',
      'audio/wav',
      'audio/ogg',
      'audio/flac',
      
      // Archives
      'application/zip',
      'application/x-rar-compressed',
      'application/x-7z-compressed'
    ];
    
    // Also support any image/*, video/*, audio/*, text/* types
    if (mimeType.startsWith('image/') || 
        mimeType.startsWith('video/') || 
        mimeType.startsWith('audio/') || 
        mimeType.startsWith('text/')) {
      return true;
    }
    
    return supportedTypes.includes(mimeType);
  }
}

const driveService = new DriveService();
export default driveService; 