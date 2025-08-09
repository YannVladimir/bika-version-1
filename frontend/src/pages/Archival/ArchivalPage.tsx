import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Button,
  Paper,
  Grid,
  Breadcrumbs,
  Link,
  Modal,
  TextField,
  MenuItem,
  IconButton,
  Alert,
  CircularProgress,
  FormHelperText,
} from "@mui/material";
import {
  Add as AddIcon,
  Close as CloseIcon,
  Folder as FolderIcon,
  InsertDriveFile as FileIcon,
  CreateNewFolder as NewFolderIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { Document, Folder, DocumentType, CreateDocumentRequest } from "../../services/api/types";
import documentService from "../../services/api/documentService";
import { 
  folderService, 
  documentTypeService,
  CreateFolderRequest 
} from "../../services/api";

// Helper function to find folder by path
const findFolderByPath = (folders: Folder[], path: string[]): Folder | undefined => {
  if (path.length === 0) return undefined;
  
  for (const folder of folders) {
    if (folder.name === path[0]) {
      if (path.length === 1) return folder;
      if (folder.children) {
        return findFolderByPath(folder.children, path.slice(1));
      }
    }
  }
  return undefined;
};

const ArchivalPage: React.FC = () => {
  const { user } = useAuth();
  
  // State for folders and navigation
  const [folders, setFolders] = useState<Folder[]>([]);
  const [currentPath, setCurrentPath] = useState<string[]>([]);
  const [currentFolder, setCurrentFolder] = useState<Folder | null>(null);
  
  // State for document types
  const [documentTypes, setDocumentTypes] = useState<DocumentType[]>([]);
  
  // Modal states
  const [openUpload, setOpenUpload] = useState(false);
  const [openNewFolder, setOpenNewFolder] = useState(false);
  const [viewDoc, setViewDoc] = useState<Document | null>(null);
  
  // Form states
  const [selectedType, setSelectedType] = useState<number | "">("");
  const [attachment, setAttachment] = useState<File | null>(null);
  const [docFields, setDocFields] = useState<{ [key: string]: any }>({});
  const [newFolderName, setNewFolderName] = useState("");
  const [newFolderDescription, setNewFolderDescription] = useState("");
  const [documentName, setDocumentName] = useState("");
  const [documentCode, setDocumentCode] = useState("");
  
  // Physical storage metadata states
  const [physicalStorage, setPhysicalStorage] = useState({
    room: "",
    cupboard: "",
    drawer: "",
    fileNumber: "",
    fileColor: "",
    documentNumber: "",
    fileSection: "",
    sectionColor: ""
  });
  
  // Loading and error states
  const [loading, setLoading] = useState(true);
  const [uploadLoading, setUploadLoading] = useState(false);
  const [folderLoading, setFolderLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load initial data
  useEffect(() => {
    const loadInitialData = async () => {
      if (!user?.companyId) return;
      
      try {
        setLoading(true);
        setError(null);
        
        // Load root folders and document types in parallel
        const [foldersData, documentTypesData] = await Promise.all([
          folderService.getRootFoldersByCompany(user.companyId),
          documentTypeService.getDocumentTypesByCompany(user.companyId)
        ]);
        
        setFolders(foldersData);
        setDocumentTypes(documentTypesData);
      } catch (err) {
        console.error('Error loading data:', err);
        setError('Failed to load folders and document types');
      } finally {
        setLoading(false);
      }
    };

    loadInitialData();
  }, [user?.companyId]);

  // Update current folder when path changes
  useEffect(() => {
    if (currentPath.length === 0) {
      setCurrentFolder(null);
    } else {
      const folder = findFolderByPath(folders, currentPath);
      setCurrentFolder(folder || null);
    }
  }, [currentPath, folders]);

  // Helper function to reset upload form
  const resetUploadForm = () => {
    setSelectedType("");
    setDocFields({});
    setAttachment(null);
    setDocumentName("");
    setDocumentCode("");
    setPhysicalStorage({
      room: "",
      cupboard: "",
      drawer: "",
      fileNumber: "",
      fileColor: "",
      documentNumber: "",
      fileSection: "",
      sectionColor: ""
    });
  };

  // Folder navigation
  const handleFolderClick = async (folder: Folder) => {
    try {
      setLoading(true);
      // Get folder contents including documents
      const folderWithContents = await folderService.getFolderContents(folder.id);
      
      // Update the folder in our state with the new contents
      const updateFolderInTree = (folders: Folder[], targetId: number, newData: Folder): Folder[] => {
        return folders.map(f => {
          if (f.id === targetId) {
            return { ...f, ...newData };
          }
          if (f.children) {
            return { ...f, children: updateFolderInTree(f.children, targetId, newData) };
          }
          return f;
        });
      };
      
      setFolders(prev => updateFolderInTree(prev, folder.id, folderWithContents));
      setCurrentPath([...currentPath, folder.name]);
    } catch (err) {
      console.error('Error loading folder contents:', err);
      setError('Failed to load folder contents');
    } finally {
      setLoading(false);
    }
  };

  const handleBack = (idx?: number) => {
    if (typeof idx === "number") {
      setCurrentPath(currentPath.slice(0, idx + 1));
    } else {
      setCurrentPath([]);
    }
  };

  // Create new folder
  const handleCreateFolder = async () => {
    if (!newFolderName.trim() || !user?.companyId) return;
    
    try {
      setFolderLoading(true);
      setError(null);
      
      const folderRequest: CreateFolderRequest = {
        name: newFolderName,
        description: newFolderDescription,
        companyId: user.companyId,
        departmentId: user.departmentId,
        parentId: currentFolder?.id,
      };
      
      const newFolder = await folderService.createFolder(folderRequest);
      
      // Add the new folder to the appropriate location in our state
      if (currentPath.length === 0) {
        // Adding to root
        setFolders(prev => [...prev, newFolder]);
      } else {
        // Adding to current folder
        const updateFolderChildren = (folders: Folder[], path: string[], newChild: Folder): Folder[] => {
          if (path.length === 0) return folders;
          
          return folders.map(f => {
            if (f.name === path[0]) {
              if (path.length === 1) {
                return { ...f, children: [...(f.children || []), newChild] };
              }
              if (f.children) {
                return { ...f, children: updateFolderChildren(f.children, path.slice(1), newChild) };
              }
            }
            return f;
          });
        };
        
        setFolders(prev => updateFolderChildren(prev, currentPath, newFolder));
      }
      
      setNewFolderName("");
      setNewFolderDescription("");
      setOpenNewFolder(false);
    } catch (err) {
      console.error('Error creating folder:', err);
      setError('Failed to create folder');
    } finally {
      setFolderLoading(false);
    }
  };

  // Document upload
  const handleTypeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const typeId = event.target.value === "" ? "" : Number(event.target.value);
    setSelectedType(typeId);
    setDocFields({});
    
    // Set default document name and code from the document type
    if (typeof typeId === 'number') {
      const docType = documentTypes.find(t => t.id === typeId);
      if (docType) {
        const defaultName = `New ${docType.name}`;
        const timestamp = Date.now();
        const defaultCode = `${docType.code}_${timestamp}`;
        
        setDocumentName(defaultName);
        setDocumentCode(defaultCode);
      }
    } else {
      setDocumentName("");
      setDocumentCode("");
    }
  };

  const handleFieldChange = (fieldKey: string, value: any) => {
    setDocFields(prev => ({ ...prev, [fieldKey]: value }));
  };

  const handlePhysicalStorageChange = (field: string, value: string) => {
    setPhysicalStorage(prev => ({ ...prev, [field]: value }));
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setAttachment(event.target.files[0]);
    }
  };

  const handleUpload = async (event: React.FormEvent) => {
    event.preventDefault();
    
    // Validate required fields including physical storage
    const requiredPhysicalFields = ['room', 'cupboard', 'drawer', 'fileNumber', 'fileColor', 'documentNumber', 'fileSection', 'sectionColor'];
    const missingPhysicalFields = requiredPhysicalFields.filter(field => !physicalStorage[field as keyof typeof physicalStorage].trim());
    
    if (!selectedType || !user?.companyId || !documentName.trim() || !documentCode.trim() || missingPhysicalFields.length > 0) {
      if (missingPhysicalFields.length > 0) {
        setError(`Please fill in all physical storage fields: ${missingPhysicalFields.join(', ')}`);
      }
      return;
    }
    
    try {
      setUploadLoading(true);
      setError(null);
      
      // Create document request with user-provided code and physical storage
      let documentRequest: CreateDocumentRequest = {
        name: documentName,
        code: documentCode,
        documentTypeId: selectedType as number,
        companyId: user.companyId,
        departmentId: user.departmentId,
        folderId: currentFolder?.id,
        fieldValues: docFields,
        status: 'ACTIVE',
        physicalLocation: JSON.stringify(physicalStorage),
      };
      
      // Add file information if attachment is provided
      if (attachment) {
        documentRequest = documentService.prepareDocumentForUpload(documentRequest, attachment);
        // Preserve physical storage data when adding file info
        documentRequest.physicalLocation = JSON.stringify(physicalStorage);
      }
      
      const newDocument = await documentService.createDocument(documentRequest);
      
      // Add the document to the current folder's documents
      if (currentFolder) {
        setFolders(prev => {
          const updateFolderDocuments = (folders: Folder[], path: string[], newDoc: Document): Folder[] => {
            if (path.length === 0) return folders;
            
            return folders.map(f => {
              if (f.name === path[0]) {
                if (path.length === 1) {
                  return { ...f, documents: [...(f.documents || []), newDoc] };
                }
                if (f.children) {
                  return { ...f, children: updateFolderDocuments(f.children, path.slice(1), newDoc) };
                }
              }
              return f;
            });
          };
          
          return updateFolderDocuments(prev, currentPath, newDocument);
        });
      }
      
      // Reset form
      setOpenUpload(false);
      resetUploadForm();
    } catch (err) {
      console.error('Error uploading document:', err);
      setError('Failed to upload document');
    } finally {
      setUploadLoading(false);
    }
  };

  // View document
  const handleViewDoc = (doc: Document) => setViewDoc(doc);
  const handleCloseViewDoc = () => setViewDoc(null);

  // Get current folder data for display
  const getCurrentFolderData = () => {
    if (currentPath.length === 0) {
      return { folders, documents: [] };
    }
    
    const folder = findFolderByPath(folders, currentPath);
    return {
      folders: folder?.children || [],
      documents: folder?.documents || []
    };
  };

  const { folders: currentFolders, documents: currentDocuments } = getCurrentFolderData();
  const selectedDocumentType = documentTypes.find(dt => dt.id === selectedType);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box width="100%" boxSizing="border-box" p={3}>
      <Typography variant="h4" fontWeight={600} mb={4}>
        Document Archival
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Breadcrumbs */}
      <Breadcrumbs sx={{ mb: 2 }}>
        <Link
          component="button"
          variant="body1"
          onClick={() => handleBack()}
          sx={{ color: tokens.primary.main }}
        >
          Archival
        </Link>
        {currentPath.map((name, idx) => (
          <Link
            key={`${name}-${idx}`}
            component="button"
            variant="body1"
            onClick={() => handleBack(idx)}
            sx={{ color: tokens.primary.main }}
          >
            {name}
          </Link>
        ))}
      </Breadcrumbs>

      {/* Action Buttons */}
      <Box sx={{ display: "flex", gap: 2, justifyContent: "flex-end", mb: 2 }}>
        <Button
          variant="outlined"
          startIcon={<NewFolderIcon />}
          onClick={() => setOpenNewFolder(true)}
          disabled={loading}
        >
          New Folder
        </Button>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenUpload(true)}
          disabled={loading || documentTypes.length === 0}
          sx={{
            bgcolor: tokens.primary.main,
            "&:hover": { bgcolor: tokens.primary.dark },
          }}
        >
          Upload Document
        </Button>
      </Box>

      {/* Folders and Documents Grid */}
      <Paper
        elevation={0}
        sx={{ p: 2, backgroundColor: tokens.grey[100], borderRadius: 2 }}
      >
        <Grid container spacing={2}>
          {currentFolders.map((folder) => (
            <Grid item xs={12} sm={6} md={4} key={folder.id}>
              <Paper
                elevation={0}
                sx={{
                  p: 2,
                  cursor: "pointer",
                  "&:hover": { backgroundColor: tokens.grey[200] },
                }}
                onClick={() => handleFolderClick(folder)}
              >
                <Box display="flex" alignItems="center" gap={2}>
                  <FolderIcon sx={{ color: tokens.secondary.main }} />
                  <Box>
                    <Typography>{folder.name}</Typography>
                    {folder.description && (
                      <Typography variant="body2" color="text.secondary">
                        {folder.description}
                      </Typography>
                    )}
                  </Box>
                </Box>
              </Paper>
            </Grid>
          ))}
          
          {currentDocuments.map((doc) => (
            <Grid item xs={12} sm={6} md={4} key={doc.id}>
              <Paper
                elevation={0}
                sx={{
                  p: 2,
                  cursor: "pointer",
                  "&:hover": { backgroundColor: tokens.grey[200] },
                  position: 'relative',
                }}
                onClick={() => handleViewDoc(doc)}
              >
                <Box display="flex" alignItems="center" gap={2}>
                  <FileIcon sx={{ color: tokens.primary.main }} />
                  <Box flex={1}>
                    <Typography>{doc.name}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      {doc.documentTypeName}
                    </Typography>
                    {doc.fileSize && (
                      <Typography variant="caption" color="text.secondary">
                        {(doc.fileSize / 1024 / 1024).toFixed(2)} MB
                      </Typography>
                    )}
                    {/* Physical Storage Preview */}
                    {doc.physicalLocation && (() => {
                      try {
                        const physicalData = JSON.parse(doc.physicalLocation);
                        return (
                          <Box sx={{ mt: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography variant="caption" color="primary.main" sx={{ fontWeight: 'bold' }}>
                              📍 {physicalData.room || 'Unknown Room'}
                            </Typography>
                            {physicalData.fileColor && (
                              <Box sx={{ 
                                width: 12, 
                                height: 12, 
                                bgcolor: physicalData.fileColor?.toLowerCase() || 'grey', 
                                borderRadius: '50%',
                                border: '1px solid #ccc'
                              }} />
                            )}
                          </Box>
                        );
                      } catch (e) {
                        return null;
                      }
                    })()}
                  </Box>
                </Box>
              </Paper>
            </Grid>
          ))}
        </Grid>

        {currentFolders.length === 0 && currentDocuments.length === 0 && (
          <Box textAlign="center" py={4}>
            <Typography color="text.secondary">
              No folders or documents found. Create a folder or upload a document to get started.
            </Typography>
          </Box>
        )}
      </Paper>

      {/* New Folder Dialog */}
      <Modal
        open={openNewFolder}
        onClose={() => setOpenNewFolder(false)}
        aria-labelledby="new-folder-modal"
      >
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 400,
            bgcolor: "background.paper",
            boxShadow: 24,
            p: 4,
            borderRadius: 2,
          }}
        >
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 2,
            }}
          >
            <Typography variant="h6" component="h2">
              Create New Folder
            </Typography>
            <IconButton onClick={() => setOpenNewFolder(false)} size="small">
              <CloseIcon />
            </IconButton>
          </Box>
          
          <TextField
            fullWidth
            label="Folder Name"
            value={newFolderName}
            onChange={(e) => setNewFolderName(e.target.value)}
            margin="normal"
            required
            error={!newFolderName.trim() && newFolderName.length > 0}
            helperText={!newFolderName.trim() && newFolderName.length > 0 ? "Folder name is required" : ""}
          />
          
          <TextField
            fullWidth
            label="Description (Optional)"
            value={newFolderDescription}
            onChange={(e) => setNewFolderDescription(e.target.value)}
            margin="normal"
            multiline
            rows={2}
          />
          
          <Box sx={{ display: "flex", gap: 2, mt: 3 }}>
            <Button
              variant="outlined"
              onClick={() => setOpenNewFolder(false)}
              fullWidth
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              onClick={handleCreateFolder}
              disabled={!newFolderName.trim() || folderLoading}
              fullWidth
              sx={{
                bgcolor: tokens.primary.main,
                "&:hover": { bgcolor: tokens.primary.dark },
              }}
            >
              {folderLoading ? <CircularProgress size={20} /> : "Create"}
            </Button>
          </Box>
        </Box>
      </Modal>

      {/* Upload Document Modal */}
      <Modal
        open={openUpload}
        onClose={() => {
          setOpenUpload(false);
          resetUploadForm();
        }}
        aria-labelledby="upload-document-modal"
      >
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 500,
            bgcolor: "background.paper",
            boxShadow: 24,
            p: 4,
            borderRadius: 2,
            maxHeight: '90vh',
            overflow: 'auto',
          }}
        >
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 2,
            }}
          >
            <Typography variant="h6" component="h2">
              Upload Document
            </Typography>
            <IconButton 
              onClick={() => {
                setOpenUpload(false);
                resetUploadForm();
              }} 
              size="small"
            >
              <CloseIcon />
            </IconButton>
          </Box>
          
          <form onSubmit={handleUpload}>
            <TextField
              fullWidth
              label="Document Name"
              value={documentName}
              onChange={(e) => setDocumentName(e.target.value)}
              margin="normal"
              required
              error={!documentName.trim() && documentName.length > 0}
              helperText={!documentName.trim() && documentName.length > 0 ? "Document name is required" : ""}
            />
            
            <TextField
              fullWidth
              label="Document Code"
              value={documentCode}
              onChange={(e) => setDocumentCode(e.target.value)}
              margin="normal"
              required
              error={!documentCode.trim() && documentCode.length > 0}
              helperText={!documentCode.trim() && documentCode.length > 0 ? "Document code is required" : ""}
            />

            <TextField
              select
              fullWidth
              label="Document Type"
              value={selectedType}
              onChange={handleTypeChange}
              margin="normal"
              required
              error={!selectedType}
              helperText={!selectedType ? "Please select a document type" : ""}
            >
              {documentTypes.map((type) => (
                <MenuItem key={type.id} value={type.id}>
                  {type.name}
                </MenuItem>
              ))}
            </TextField>
            
            {/* Dynamic fields for document type */}
            {selectedDocumentType && selectedDocumentType.fields.map((field) => (
              <TextField
                key={field.fieldKey}
                label={field.name + (field.required ? ' *' : '')}
                fullWidth
                margin="normal"
                type={field.fieldType === 'NUMBER' ? 'number' : 
                      field.fieldType === 'DATE' ? 'date' : 
                      field.fieldType === 'EMAIL' ? 'email' : 'text'}
                multiline={field.fieldType === 'TEXTAREA'}
                rows={field.fieldType === 'TEXTAREA' ? 3 : 1}
                select={field.fieldType === 'SELECT'}
                value={docFields[field.fieldKey] || field.defaultValue || ''}
                onChange={(e) => handleFieldChange(field.fieldKey, e.target.value)}
                required={field.required}
                helperText={field.description}
                InputLabelProps={field.fieldType === 'DATE' ? { shrink: true } : undefined}
              >
                {field.fieldType === 'SELECT' && field.options?.map((option) => (
                  <MenuItem key={option} value={option}>
                    {option}
                  </MenuItem>
                ))}
              </TextField>
            ))}
            
            {/* Physical Storage Fields */}
            <Typography variant="h6" sx={{ mt: 3, mb: 1 }}>Physical Storage</Typography>
            <Grid container spacing={2} sx={{ mb: 2 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Room"
                  value={physicalStorage.room}
                  onChange={(e) => handlePhysicalStorageChange('room', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.room.trim()}
                  helperText={!physicalStorage.room.trim() ? "Room is required" : ""}
                  placeholder="Enter room name (e.g., Room 101, Main Office, etc.)"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Cupboard / Cabinet"
                  value={physicalStorage.cupboard}
                  onChange={(e) => handlePhysicalStorageChange('cupboard', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.cupboard.trim()}
                  helperText={!physicalStorage.cupboard.trim() ? "Cupboard is required" : ""}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Drawer"
                  value={physicalStorage.drawer}
                  onChange={(e) => handlePhysicalStorageChange('drawer', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.drawer.trim()}
                  helperText={!physicalStorage.drawer.trim() ? "Drawer is required" : ""}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="File Number"
                  value={physicalStorage.fileNumber}
                  onChange={(e) => handlePhysicalStorageChange('fileNumber', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.fileNumber.trim()}
                  helperText={!physicalStorage.fileNumber.trim() ? "File number is required" : ""}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="File Color"
                  value={physicalStorage.fileColor}
                  onChange={(e) => handlePhysicalStorageChange('fileColor', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.fileColor.trim()}
                  helperText={!physicalStorage.fileColor.trim() ? "File color is required" : ""}
                  placeholder="Enter file color (e.g., Blue, Red, Green, etc.)"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Document Number"
                  value={physicalStorage.documentNumber}
                  onChange={(e) => handlePhysicalStorageChange('documentNumber', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.documentNumber.trim()}
                  helperText={!physicalStorage.documentNumber.trim() ? "Document number is required" : ""}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="File Section"
                  value={physicalStorage.fileSection}
                  onChange={(e) => handlePhysicalStorageChange('fileSection', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.fileSection.trim()}
                  helperText={!physicalStorage.fileSection.trim() ? "File section is required" : ""}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Section Color"
                  value={physicalStorage.sectionColor}
                  onChange={(e) => handlePhysicalStorageChange('sectionColor', e.target.value)}
                  margin="normal"
                  required
                  error={!physicalStorage.sectionColor.trim()}
                  helperText={!physicalStorage.sectionColor.trim() ? "Section color is required" : ""}
                  placeholder="Enter section color (e.g., Yellow, Orange, Purple, etc.)"
                />
              </Grid>
            </Grid>

            <input
              accept="*/*"
              style={{ display: "none" }}
              id="attachment-input"
              type="file"
              onChange={handleFileChange}
            />
            <label htmlFor="attachment-input">
              <Button
                variant="outlined"
                component="span"
                fullWidth
                sx={{ mt: 2 }}
              >
                {attachment ? `Selected: ${attachment.name}` : "Select File (Optional)"}
              </Button>
            </label>
            
            {attachment && (
              <Box sx={{ mt: 1 }}>
                <Typography variant="body2" color="text.secondary">
                  File: {attachment.name} ({(attachment.size / 1024 / 1024).toFixed(2)} MB)
                </Typography>
              </Box>
            )}
            
            <Box sx={{ display: "flex", gap: 2, mt: 3 }}>
              <Button
                variant="outlined"
                onClick={() => {
                  setOpenUpload(false);
                  resetUploadForm();
                }}
                fullWidth
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                disabled={!selectedType || !documentName.trim() || !documentCode.trim() || uploadLoading}
                fullWidth
                sx={{
                  bgcolor: tokens.primary.main,
                  "&:hover": { bgcolor: tokens.primary.dark },
                }}
              >
                {uploadLoading ? <CircularProgress size={20} /> : "Upload"}
              </Button>
            </Box>
          </form>
        </Box>
      </Modal>

      {/* View Document Modal */}
      <Modal
        open={!!viewDoc}
        onClose={handleCloseViewDoc}
        aria-labelledby="view-document-modal"
      >
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: 500,
            bgcolor: "background.paper",
            boxShadow: 24,
            p: 4,
            borderRadius: 2,
            maxHeight: '90vh',
            overflow: 'auto',
          }}
        >
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 2,
            }}
          >
            <Typography variant="h6" component="h2">
              {viewDoc?.name}
            </Typography>
            <IconButton onClick={handleCloseViewDoc} size="small">
              <CloseIcon />
            </IconButton>
          </Box>
          
          {viewDoc && (
            <Box>
              <Typography variant="subtitle1" sx={{ mb: 2 }}>
                <strong>Document Type:</strong> {viewDoc.documentTypeName}
              </Typography>
              
              {viewDoc.metadata && Object.entries(viewDoc.metadata).map(([key, value]) => (
                <Typography key={key} variant="body2" sx={{ mb: 1 }}>
                  <strong>{key}:</strong> {value?.toString()}
                </Typography>
              ))}
              
              {/* Physical Storage Information */}
              {viewDoc.physicalLocation && (
                <Box sx={{ mt: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
                  <Typography variant="h6" sx={{ mb: 2, color: 'primary.main' }}>
                    📍 Physical Storage Location
                  </Typography>
                  {(() => {
                    try {
                      const physicalData = JSON.parse(viewDoc.physicalLocation);
                      return (
                        <Grid container spacing={2}>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>Room:</strong> {physicalData.room || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>Cupboard:</strong> {physicalData.cupboard || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>Drawer:</strong> {physicalData.drawer || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>File Number:</strong> {physicalData.fileNumber || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>File Color:</strong> 
                              <Box component="span" sx={{ 
                                ml: 1, 
                                display: 'inline-block', 
                                width: 20, 
                                height: 20, 
                                bgcolor: physicalData.fileColor?.toLowerCase() || 'grey', 
                                borderRadius: '50%',
                                border: '1px solid #ccc',
                                verticalAlign: 'middle'
                              }} />
                              {' '}{physicalData.fileColor || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>Document Number:</strong> {physicalData.documentNumber || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>File Section:</strong> {physicalData.fileSection || 'N/A'}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                              <strong>Section Color:</strong>
                              <Box component="span" sx={{ 
                                ml: 1, 
                                display: 'inline-block', 
                                width: 20, 
                                height: 20, 
                                bgcolor: physicalData.sectionColor?.toLowerCase() || 'grey', 
                                borderRadius: '50%',
                                border: '1px solid #ccc',
                                verticalAlign: 'middle'
                              }} />
                              {' '}{physicalData.sectionColor || 'N/A'}
                            </Typography>
                          </Grid>
                        </Grid>
                      );
                    } catch (e) {
                      return (
                        <Typography variant="body2" color="error">
                          Error parsing physical storage data
                        </Typography>
                      );
                    }
                  })()}
                </Box>
              )}
              
              <Box sx={{ mt: 2, pt: 2, borderTop: 1, borderColor: 'divider' }}>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  <strong>File:</strong> {viewDoc.filePath || 'No file attached'}
                </Typography>
                {viewDoc.fileSize && (
                  <Typography variant="body2" sx={{ mb: 1 }}>
                    <strong>Size:</strong> {(viewDoc.fileSize / 1024 / 1024).toFixed(2)} MB
                  </Typography>
                )}
                <Typography variant="body2" sx={{ mb: 1 }}>
                  <strong>Status:</strong> {viewDoc.status}
                </Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  <strong>Created:</strong> {viewDoc.createdAt ? new Date(viewDoc.createdAt).toLocaleString() : 'Unknown'}
                </Typography>
              </Box>
            </Box>
          )}
        </Box>
      </Modal>
    </Box>
  );
};

export default ArchivalPage;
