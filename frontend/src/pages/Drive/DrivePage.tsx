import React, { useState, useEffect, useCallback } from "react";
import {
  Box,
  Typography,
  Paper,
  Grid,
  LinearProgress,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Breadcrumbs,
  Link,
  Card,
  CardContent,
  CardActions,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListItemSecondaryAction,
  Alert,
  CircularProgress,
  Chip,
  Avatar,
  Tooltip,
  Menu,
  MenuItem,
  Divider,
  Stack,
} from "@mui/material";
import {
  Folder as FolderIcon,
  InsertDriveFile as FileIcon,
  Upload as UploadIcon,
  CreateNewFolder as NewFolderIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
  MoreVert as MoreVertIcon,
  CloudUpload as CloudUploadIcon,
  Image as ImageIcon,
  VideoFile as VideoFileIcon,
  AudioFile as AudioFileIcon,
  PictureAsPdf as PdfIcon,
  Description as DocIcon,
  TableChart as ExcelIcon,
  Slideshow as PowerPointIcon,
  TextSnippet as TextIcon,
  Archive as ArchiveIcon,
  Close as CloseIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { 
  driveService, 
  DriveFolder, 
  DriveFile, 
  UserStorageQuota,
  CreateDriveFolderRequest,
  CreateDriveFileRequest
} from "../../services/api";

const DrivePage: React.FC = () => {
  const { user } = useAuth();
  
  // State management
  const [folders, setFolders] = useState<DriveFolder[]>([]);
  const [files, setFiles] = useState<DriveFile[]>([]);
  const [currentPath, setCurrentPath] = useState<number[]>([]);
  const [currentFolder, setCurrentFolder] = useState<DriveFolder | null>(null);
  const [storageQuota, setStorageQuota] = useState<UserStorageQuota | null>(null);
  
  // Loading and error states
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  
  // Dialog states
  const [newFolderDialog, setNewFolderDialog] = useState(false);
  const [uploadDialog, setUploadDialog] = useState(false);
  const [fileViewDialog, setFileViewDialog] = useState(false);
  const [selectedFile, setSelectedFile] = useState<DriveFile | null>(null);
  
  // Form states
  const [newFolderName, setNewFolderName] = useState("");
  const [newFolderDescription, setNewFolderDescription] = useState("");
  const [selectedFiles, setSelectedFiles] = useState<FileList | null>(null);
  
  // Menu states
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [menuItem, setMenuItem] = useState<DriveFolder | DriveFile | null>(null);

  // Load initial data
  useEffect(() => {
    loadData();
  }, [user]);

  // Load current folder contents when path changes
  useEffect(() => {
    if (currentPath.length === 0) {
      // Load root folder
      loadRootContents();
    } else {
      // Load specific folder
      const folderId = currentPath[currentPath.length - 1];
      loadFolderContents(folderId);
    }
  }, [currentPath]);

  const loadData = async () => {
    if (!user) return;
    
    try {
      setLoading(true);
      setError(null);
      
      // Load storage quota and root folders
      const [quotaData, foldersData] = await Promise.all([
        driveService.getStorageQuota(),
        driveService.getRootFolders()
      ]);
      
      setStorageQuota(quotaData);
      setFolders(foldersData);
      
      // Load root files
      const filesData = await driveService.getFilesByFolder();
      setFiles(filesData);
      
    } catch (err) {
      console.error('Error loading drive data:', err);
      setError('Failed to load drive data');
    } finally {
      setLoading(false);
    }
  };

  const loadRootContents = async () => {
    try {
      const [foldersData, filesData] = await Promise.all([
        driveService.getRootFolders(),
        driveService.getFilesByFolder()
      ]);
      
      setFolders(foldersData);
      setFiles(filesData);
      setCurrentFolder(null);
    } catch (err) {
      console.error('Error loading root contents:', err);
      setError('Failed to load folder contents');
    }
  };

  const loadFolderContents = async (folderId: number) => {
    try {
      const folderData = await driveService.getFolderById(folderId);
      setCurrentFolder(folderData);
      setFolders(folderData.children);
      setFiles(folderData.files);
    } catch (err) {
      console.error('Error loading folder contents:', err);
      setError('Failed to load folder contents');
    }
  };

  const handleFolderClick = (folder: DriveFolder) => {
    setCurrentPath([...currentPath, folder.id]);
  };

  const handleBreadcrumbClick = (index: number) => {
    if (index === -1) {
      // Go to root
      setCurrentPath([]);
    } else {
      // Go to specific path level
      setCurrentPath(currentPath.slice(0, index + 1));
    }
  };

  const handleCreateFolder = async () => {
    if (!newFolderName.trim()) return;
    
    try {
      const request: CreateDriveFolderRequest = {
        name: newFolderName.trim(),
        description: newFolderDescription.trim() || undefined,
        parentId: currentPath.length > 0 ? currentPath[currentPath.length - 1] : undefined
      };
      
      await driveService.createFolder(request);
      
      // Refresh current folder contents
      if (currentPath.length === 0) {
        loadRootContents();
      } else {
        loadFolderContents(currentPath[currentPath.length - 1]);
      }
      
      // Reset form
      setNewFolderName("");
      setNewFolderDescription("");
      setNewFolderDialog(false);
      
      // Refresh storage quota
      const quotaData = await driveService.getStorageQuota();
      setStorageQuota(quotaData);
      
    } catch (err) {
      console.error('Error creating folder:', err);
      setError('Failed to create folder');
    }
  };

  const handleFileUpload = async () => {
    if (!selectedFiles || selectedFiles.length === 0) return;
    
    try {
      setUploading(true);
      setError(null);
      
      const currentFolderId = currentPath.length > 0 ? currentPath[currentPath.length - 1] : undefined;
      
      // Upload files one by one
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        
        // Check if file type is supported
        if (!driveService.isFileTypeSupported(file.type)) {
          setError(`File type ${file.type} is not supported for file ${file.name}`);
          continue;
        }
        
        const request = driveService.createFileFromBrowser(file, currentFolderId);
        await driveService.uploadFile(request);
      }
      
      // Refresh current folder contents
      if (currentPath.length === 0) {
        loadRootContents();
      } else {
        loadFolderContents(currentPath[currentPath.length - 1]);
      }
      
      // Refresh storage quota
      const quotaData = await driveService.getStorageQuota();
      setStorageQuota(quotaData);
      
      setSelectedFiles(null);
      setUploadDialog(false);
      
    } catch (err) {
      console.error('Error uploading files:', err);
      setError('Failed to upload files');
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (file: DriveFile) => {
    try {
      const blob = await driveService.downloadFile(file.id);
      
      // Create download link
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = file.name;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // Clean up object URL
      window.URL.revokeObjectURL(url);
      
    } catch (err) {
      console.error('Error downloading file:', err);
      setError('Failed to download file');
    }
  };

  const handleDelete = async (item: DriveFolder | DriveFile) => {
    try {
      if ('mimeType' in item) {
        // It's a file
        await driveService.deleteFile(item.id);
      } else {
        // It's a folder
        await driveService.deleteFolder(item.id);
      }
      
      // Refresh current folder contents
      if (currentPath.length === 0) {
        loadRootContents();
      } else {
        loadFolderContents(currentPath[currentPath.length - 1]);
      }
      
      // Refresh storage quota
      const quotaData = await driveService.getStorageQuota();
      setStorageQuota(quotaData);
      
      setAnchorEl(null);
      setMenuItem(null);
      
    } catch (err) {
      console.error('Error deleting item:', err);
      setError('Failed to delete item');
    }
  };

  const handleMenuClick = (event: React.MouseEvent<HTMLElement>, item: DriveFolder | DriveFile) => {
    setAnchorEl(event.currentTarget);
    setMenuItem(item);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setMenuItem(null);
  };

  const getFileIcon = (mimeType: string) => {
    const iconName = driveService.getFileIcon(mimeType);
    const iconProps = { sx: { color: driveService.getFileTypeColor(mimeType) } };
    
    switch (iconName) {
      case 'Image': return <ImageIcon {...iconProps} />;
      case 'VideoFile': return <VideoFileIcon {...iconProps} />;
      case 'AudioFile': return <AudioFileIcon {...iconProps} />;
      case 'PictureAsPdf': return <PdfIcon {...iconProps} />;
      case 'Description': return <DocIcon {...iconProps} />;
      case 'TableChart': return <ExcelIcon {...iconProps} />;
      case 'Slideshow': return <PowerPointIcon {...iconProps} />;
      case 'TextSnippet': return <TextIcon {...iconProps} />;
      case 'Archive': return <ArchiveIcon {...iconProps} />;
      default: return <FileIcon {...iconProps} />;
    }
  };

  const getBreadcrumbs = () => {
    const breadcrumbs = [
      <Link
        key="root"
        component="button"
        variant="body1"
        onClick={() => handleBreadcrumbClick(-1)}
        sx={{ color: tokens.primary.main }}
      >
        Drive
      </Link>
    ];

    // Add folder names for the path (would need to store folder names in path)
    currentPath.forEach((folderId, index) => {
      breadcrumbs.push(
        <Link
          key={folderId}
          component="button"
          variant="body1"
          onClick={() => handleBreadcrumbClick(index)}
          sx={{ color: tokens.primary.main }}
        >
          Folder {folderId} {/* In real implementation, store folder names */}
        </Link>
      );
    });

    return breadcrumbs;
  };

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
        Drive
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Storage Usage Section */}
      {storageQuota && (
        <Paper
          elevation={0}
          sx={{
            p: 3,
            mb: 4,
            backgroundColor: tokens.grey[100],
            borderRadius: 2,
          }}
        >
          <Grid container spacing={3} alignItems="center">
            <Grid item xs={12} md={8}>
              <Box>
                <Box
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  mb={1}
                >
                  <Typography variant="h6">Storage Usage</Typography>
                  <Typography variant="body2" color="text.secondary">
                    {driveService.formatFileSize(storageQuota.usedStorageBytes)} of {driveService.formatFileSize(storageQuota.maxStorageBytes)} used
                  </Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={storageQuota.usagePercentage}
                  sx={{
                    height: 10,
                    borderRadius: 5,
                    backgroundColor: tokens.grey[200],
                    "& .MuiLinearProgress-bar": {
                      borderRadius: 5,
                      backgroundColor: storageQuota.usagePercentage > 90 ? tokens.secondary.main : tokens.primary.main,
                    },
                  }}
                />
                <Box display="flex" justifyContent="flex-end" mt={0.5}>
                  <Typography variant="body2" color="text.secondary">
                    {Math.round(storageQuota.usagePercentage)}% used
                  </Typography>
                </Box>
              </Box>
            </Grid>
            
            <Grid item xs={12} md={4}>
              <Box display="flex" gap={2} justifyContent="flex-end">
                <Button
                  variant="outlined"
                  startIcon={<NewFolderIcon />}
                  onClick={() => setNewFolderDialog(true)}
                >
                  New Folder
                </Button>
                <Button
                  variant="contained"
                  startIcon={<UploadIcon />}
                  onClick={() => setUploadDialog(true)}
                >
                  Upload File
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      )}

      {/* Navigation Breadcrumbs */}
      <Breadcrumbs sx={{ mb: 2 }}>
        {getBreadcrumbs()}
      </Breadcrumbs>

      {/* Content Section */}
      <Paper
        elevation={0}
        sx={{
          p: 2,
          backgroundColor: tokens.grey[100],
          borderRadius: 2,
        }}
      >
        {folders.length === 0 && files.length === 0 ? (
          <Box textAlign="center" py={8}>
            <CloudUploadIcon sx={{ fontSize: 80, color: tokens.grey[400], mb: 2 }} />
            <Typography variant="h6" color="text.secondary" gutterBottom>
              No files or folders
            </Typography>
            <Typography variant="body2" color="text.secondary" mb={3}>
              Upload your first file or create a folder to get started
            </Typography>
            <Stack direction="row" spacing={2} justifyContent="center">
              <Button
                variant="outlined"
                startIcon={<NewFolderIcon />}
                onClick={() => setNewFolderDialog(true)}
              >
                Create Folder
              </Button>
              <Button
                variant="contained"
                startIcon={<UploadIcon />}
                onClick={() => setUploadDialog(true)}
              >
                Upload Files
              </Button>
            </Stack>
          </Box>
        ) : (
          <Grid container spacing={2}>
            {/* Folders */}
            {folders.map((folder) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={folder.id}>
                <Card
                  elevation={1}
                  sx={{
                    cursor: "pointer",
                    transition: "all 0.2s",
                    "&:hover": {
                      elevation: 3,
                      transform: "translateY(-2px)",
                    },
                  }}
                >
                  <CardContent onClick={() => handleFolderClick(folder)}>
                    <Box display="flex" alignItems="center" gap={2}>
                      <Avatar sx={{ bgcolor: tokens.secondary.main }}>
                        <FolderIcon />
                      </Avatar>
                      <Box flexGrow={1}>
                        <Typography variant="h6" noWrap>
                          {folder.name}
                        </Typography>
                        {folder.description && (
                          <Typography variant="body2" color="text.secondary" noWrap>
                            {folder.description}
                          </Typography>
                        )}
                      </Box>
                    </Box>
                  </CardContent>
                  <CardActions>
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleMenuClick(e, folder);
                      }}
                    >
                      <MoreVertIcon />
                    </IconButton>
                  </CardActions>
                </Card>
              </Grid>
            ))}

            {/* Files */}
            {files.map((file) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={file.id}>
                <Card elevation={1}>
                  <CardContent>
                    <Box display="flex" alignItems="center" gap={2} mb={2}>
                      <Avatar>
                        {getFileIcon(file.mimeType)}
                      </Avatar>
                      <Box flexGrow={1}>
                        <Typography variant="h6" noWrap>
                          {file.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {driveService.formatFileSize(file.fileSize)}
                        </Typography>
                      </Box>
                    </Box>
                    
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                      <Chip
                        label={file.mimeType.split('/')[0].toUpperCase()}
                        size="small"
                        sx={{ 
                          bgcolor: driveService.getFileTypeColor(file.mimeType) + '20',
                          color: driveService.getFileTypeColor(file.mimeType)
                        }}
                      />
                      <Typography variant="caption" color="text.secondary">
                        {file.downloadCount} downloads
                      </Typography>
                    </Box>
                  </CardContent>
                  <CardActions>
                    <Button 
                      size="small" 
                      onClick={() => {
                        setSelectedFile(file);
                        setFileViewDialog(true);
                      }}
                    >
                      View
                    </Button>
                    <Button 
                      size="small" 
                      onClick={() => handleDownload(file)}
                      startIcon={<DownloadIcon />}
                    >
                      Download
                    </Button>
                    <IconButton
                      size="small"
                      onClick={(e) => handleMenuClick(e, file)}
                    >
                      <MoreVertIcon />
                    </IconButton>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Paper>

      {/* Context Menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={() => {
          if (menuItem && 'mimeType' in menuItem) {
            handleDownload(menuItem);
          }
          handleMenuClose();
        }} disabled={!menuItem || !('mimeType' in menuItem)}>
          <DownloadIcon sx={{ mr: 1 }} />
          Download
        </MenuItem>
        <MenuItem onClick={handleMenuClose}>
          <EditIcon sx={{ mr: 1 }} />
          Rename
        </MenuItem>
        <Divider />
        <MenuItem onClick={() => {
          if (menuItem) {
            handleDelete(menuItem);
          }
        }} sx={{ color: 'error.main' }}>
          <DeleteIcon sx={{ mr: 1 }} />
          Delete
        </MenuItem>
      </Menu>

      {/* New Folder Dialog */}
      <Dialog
        open={newFolderDialog}
        onClose={() => setNewFolderDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Create New Folder</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Folder Name"
            fullWidth
            variant="outlined"
            value={newFolderName}
            onChange={(e) => setNewFolderName(e.target.value)}
            sx={{ mb: 2 }}
          />
          <TextField
            margin="dense"
            label="Description (optional)"
            fullWidth
            variant="outlined"
            multiline
            rows={3}
            value={newFolderDescription}
            onChange={(e) => setNewFolderDescription(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setNewFolderDialog(false)}>Cancel</Button>
          <Button 
            onClick={handleCreateFolder} 
            variant="contained"
            disabled={!newFolderName.trim()}
          >
            Create
          </Button>
        </DialogActions>
      </Dialog>

      {/* Upload Files Dialog */}
      <Dialog
        open={uploadDialog}
        onClose={() => setUploadDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Upload Files</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <input
              type="file"
              multiple
              onChange={(e) => setSelectedFiles(e.target.files)}
              style={{ width: '100%', padding: '20px', border: '2px dashed #ccc', borderRadius: '8px' }}
            />
            {selectedFiles && selectedFiles.length > 0 && (
              <Box sx={{ mt: 2 }}>
                <Typography variant="body2" gutterBottom>
                  Selected files:
                </Typography>
                <List dense>
                  {Array.from(selectedFiles).map((file, index) => (
                    <ListItem key={index}>
                      <ListItemIcon>
                        {getFileIcon(file.type)}
                      </ListItemIcon>
                      <ListItemText 
                        primary={file.name}
                        secondary={driveService.formatFileSize(file.size)}
                      />
                    </ListItem>
                  ))}
                </List>
              </Box>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setUploadDialog(false)} disabled={uploading}>
            Cancel
          </Button>
          <Button 
            onClick={handleFileUpload} 
            variant="contained"
            disabled={!selectedFiles || selectedFiles.length === 0 || uploading}
            startIcon={uploading ? <CircularProgress size={20} /> : <UploadIcon />}
          >
            {uploading ? 'Uploading...' : 'Upload'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* File View Dialog */}
      <Dialog
        open={fileViewDialog}
        onClose={() => setFileViewDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          <Box display="flex" alignItems="center" justifyContent="space-between">
            <Box display="flex" alignItems="center" gap={2}>
              {selectedFile && getFileIcon(selectedFile.mimeType)}
              <Box>
                <Typography variant="h6">{selectedFile?.name}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {selectedFile && driveService.formatFileSize(selectedFile.fileSize)}
                </Typography>
              </Box>
            </Box>
            <IconButton onClick={() => setFileViewDialog(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        
        <DialogContent dividers>
          {selectedFile && (
            <Box>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    File Information
                  </Typography>
                  <List dense>
                    <ListItem>
                      <ListItemText 
                        primary="File Type" 
                        secondary={selectedFile.mimeType} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="File Size" 
                        secondary={driveService.formatFileSize(selectedFile.fileSize)} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Downloads" 
                        secondary={selectedFile.downloadCount} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Created" 
                        secondary={new Date(selectedFile.createdAt).toLocaleString()} 
                      />
                    </ListItem>
                    {selectedFile.lastAccessed && (
                      <ListItem>
                        <ListItemText 
                          primary="Last Accessed" 
                          secondary={new Date(selectedFile.lastAccessed).toLocaleString()} 
                        />
                      </ListItem>
                    )}
                  </List>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        
        <DialogActions>
          {selectedFile && (
            <Button 
              onClick={() => handleDownload(selectedFile)}
              startIcon={<DownloadIcon />}
              variant="contained"
            >
              Download
            </Button>
          )}
          <Button onClick={() => setFileViewDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DrivePage; 