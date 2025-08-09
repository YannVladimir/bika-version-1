import React, { useState, useEffect, useMemo } from "react";
import {
  Box,
  Typography,
  Paper,
  Grid,
  TextField,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Switch,
  FormControlLabel,
  MenuItem,
  Chip,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemSecondaryAction,
  Divider,
  Alert,
  CircularProgress,
  InputAdornment,
  Card,
  CardContent,
  CardActions,
  Tooltip,
  Badge,
} from "@mui/material";
import {
  Search as SearchIcon,
  InsertDriveFile as FileIcon,
  Close as CloseIcon,
  Download as DownloadIcon,
  Visibility as ViewIcon,
  FilterList as FilterIcon,
  Clear as ClearIcon,
  CalendarToday as CalendarIcon,
  Person as PersonIcon,
  Business as BusinessIcon,
  Folder as FolderIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { 
  documentService, 
  documentTypeService,
  Document, 
  DocumentType,
  FieldType 
} from "../../services/api";

// Search filters interface
interface SearchFilters {
  name: string;
  code: string;
  documentType: string;
  dateFrom: string;
  dateTo: string;
  status: string;
  createdBy: string;
  fieldValues: { [key: string]: string };
}

const RetrievalPage: React.FC = () => {
  const { user } = useAuth();
  
  // State for documents and document types
  const [documents, setDocuments] = useState<Document[]>([]);
  const [documentTypes, setDocumentTypes] = useState<DocumentType[]>([]);
  const [allDocuments, setAllDocuments] = useState<Document[]>([]);
  
  // Search and filter states
  const [quickSearch, setQuickSearch] = useState("");
  const [advancedSearch, setAdvancedSearch] = useState(false);
  const [filters, setFilters] = useState<SearchFilters>({
    name: "",
    code: "",
    documentType: "",
    dateFrom: "",
    dateTo: "",
    status: "",
    createdBy: "",
    fieldValues: {},
  });
  
  // Modal and UI states
  const [viewDoc, setViewDoc] = useState<Document | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchLoading, setSearchLoading] = useState(false);

  // Load initial data
  useEffect(() => {
    const loadInitialData = async () => {
      if (!user?.companyId) return;
      
      try {
        setLoading(true);
        setError(null);
        
        // Load documents and document types in parallel
        const [documentsData, documentTypesData] = await Promise.all([
          documentService.getDocumentsByCompany(user.companyId),
          documentTypeService.getDocumentTypesByCompany(user.companyId)
        ]);
        
        setAllDocuments(documentsData);
        setDocuments(documentsData);
        setDocumentTypes(documentTypesData);
      } catch (err) {
        console.error('Error loading data:', err);
        setError('Failed to load documents and document types');
      } finally {
        setLoading(false);
      }
    };

    loadInitialData();
  }, [user?.companyId]);

  // Advanced search logic
  const performSearch = useMemo(() => {
    setSearchLoading(true);
    
    let filtered = [...allDocuments];
    
    // Quick search - searches across name, code, and document type
    if (quickSearch.trim()) {
      const searchTerm = quickSearch.toLowerCase().trim();
      filtered = filtered.filter(doc => 
        doc.name?.toLowerCase().includes(searchTerm) ||
        doc.code?.toLowerCase().includes(searchTerm) ||
        doc.documentTypeName?.toLowerCase().includes(searchTerm) ||
        doc.createdBy?.toLowerCase().includes(searchTerm) ||
        // Search in metadata fields
        (doc.metadata && Object.values(doc.metadata).some(value => 
          value?.toString().toLowerCase().includes(searchTerm)
        ))
      );
    }
    
    // Advanced filters
    if (advancedSearch) {
      // Filter by name
      if (filters.name.trim()) {
        filtered = filtered.filter(doc => 
          doc.name?.toLowerCase().includes(filters.name.toLowerCase())
        );
      }
      
      // Filter by code
      if (filters.code.trim()) {
        filtered = filtered.filter(doc => 
          doc.code?.toLowerCase().includes(filters.code.toLowerCase())
        );
      }
      
      // Filter by document type
      if (filters.documentType) {
        filtered = filtered.filter(doc => doc.documentTypeId === Number(filters.documentType));
      }
      
      // Filter by status
      if (filters.status) {
        filtered = filtered.filter(doc => doc.status === filters.status);
      }
      
      // Filter by created by
      if (filters.createdBy.trim()) {
        filtered = filtered.filter(doc => 
          doc.createdBy?.toLowerCase().includes(filters.createdBy.toLowerCase())
        );
      }
      
      // Filter by date range
      if (filters.dateFrom) {
        filtered = filtered.filter(doc => {
          if (!doc.createdAt) return false;
          const docDate = new Date(doc.createdAt).toISOString().split('T')[0];
          return docDate >= filters.dateFrom;
        });
      }
      
      if (filters.dateTo) {
        filtered = filtered.filter(doc => {
          if (!doc.createdAt) return false;
          const docDate = new Date(doc.createdAt).toISOString().split('T')[0];
          return docDate <= filters.dateTo;
        });
      }
      
      // Filter by field values
      Object.entries(filters.fieldValues).forEach(([fieldKey, value]) => {
        if (value.trim()) {
          filtered = filtered.filter(doc => {
            const fieldValue = doc.metadata?.[fieldKey];
            return fieldValue?.toString().toLowerCase().includes(value.toLowerCase());
          });
        }
      });
    }
    
    setTimeout(() => setSearchLoading(false), 100);
    return filtered;
  }, [allDocuments, quickSearch, advancedSearch, filters]);

  // Update documents when search changes
  useEffect(() => {
    setDocuments(performSearch);
  }, [performSearch]);

  // Handle filter changes
  const handleFilterChange = (key: keyof SearchFilters, value: string) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  // Handle field value filter changes
  const handleFieldValueChange = (fieldKey: string, value: string) => {
    setFilters(prev => ({
      ...prev,
      fieldValues: { ...prev.fieldValues, [fieldKey]: value }
    }));
  };

  // Clear all filters
  const clearFilters = () => {
    setQuickSearch("");
    setFilters({
      name: "",
      code: "",
      documentType: "",
      dateFrom: "",
      dateTo: "",
      status: "",
      createdBy: "",
      fieldValues: {},
    });
  };

  // Document actions
  const handleViewDoc = (doc: Document) => setViewDoc(doc);
  const handleCloseViewDoc = () => setViewDoc(null);

  const handleDownload = async (doc: Document) => {
    try {
      if (!doc.filePath) {
        alert('No file attached to this document');
        return;
      }
      
      setError(null);
      
      // Download file from backend
      const blob = await documentService.downloadDocument(doc.id);
      
      // Create download link
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = doc.name || 'document';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // Clean up object URL
      window.URL.revokeObjectURL(url);
      
      console.log('Document downloaded successfully:', doc.name);
    } catch (err) {
      console.error('Error downloading document:', err);
      setError('Failed to download document. Please try again.');
    }
  };

  // Get document type fields for advanced search
  const getDocumentTypeFields = (typeId: number) => {
    const docType = documentTypes.find(dt => dt.id === typeId);
    return docType?.fields || [];
  };

  // Get status color
  const getStatusColor = (status: string | undefined): "default" | "primary" | "secondary" | "error" | "info" | "success" | "warning" => {
    if (!status) return "default";
    
    switch (status.toLowerCase()) {
      case 'active': return 'success';
      case 'draft': return 'warning';
      case 'archived': return 'secondary';
      case 'deleted': return 'error';
      default: return 'default';
    }
  };

  // Get file type icon color based on mime type
  const getFileIconColor = (mimeType?: string) => {
    if (!mimeType) return tokens.grey[500];
    if (mimeType.includes('pdf')) return '#d32f2f';
    if (mimeType.includes('image')) return '#2e7d32';
    if (mimeType.includes('word') || mimeType.includes('document')) return '#1976d2';
    if (mimeType.includes('excel') || mimeType.includes('spreadsheet')) return '#388e3c';
    return tokens.primary.main;
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
        Document Retrieval
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Search Section */}
      <Paper elevation={0} sx={{ p: 3, mb: 3, backgroundColor: tokens.grey[100] }}>
        {/* Quick Search */}
        <Box display="flex" alignItems="center" gap={2} mb={2}>
          <TextField
            fullWidth
            placeholder="Search documents by name, code, type, creator, or field values..."
            value={quickSearch}
            onChange={(e) => setQuickSearch(e.target.value)}
            disabled={advancedSearch}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
              endAdornment: quickSearch && (
                <InputAdornment position="end">
                  <IconButton onClick={() => setQuickSearch("")} size="small">
                    <ClearIcon />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
          <FormControlLabel
            control={
              <Switch
                checked={advancedSearch}
                onChange={(e) => setAdvancedSearch(e.target.checked)}
              />
            }
            label="Advanced"
          />
        </Box>

        {/* Advanced Search Filters */}
        {advancedSearch && (
          <Box>
            <Divider sx={{ mb: 2 }} />
            <Typography variant="h6" gutterBottom>
              <FilterIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
              Advanced Search Filters
            </Typography>
            
            <Grid container spacing={2} mb={2}>
              <Grid item xs={12} sm={6} md={3}>
            <TextField
                  fullWidth
                  label="Document Name"
                  value={filters.name}
                  onChange={(e) => handleFilterChange('name', e.target.value)}
                  size="small"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
            <TextField
                  fullWidth
                  label="Document Code"
                  value={filters.code}
                  onChange={(e) => handleFilterChange('code', e.target.value)}
                  size="small"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
            <TextField
              select
                  fullWidth
              label="Document Type"
                  value={filters.documentType}
                  onChange={(e) => handleFilterChange('documentType', e.target.value)}
                  size="small"
                >
                  <MenuItem value="">All Types</MenuItem>
              {documentTypes.map((type) => (
                <MenuItem key={type.id} value={type.id}>
                  {type.name}
                </MenuItem>
              ))}
            </TextField>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <TextField
                  select
                  fullWidth
                  label="Status"
                  value={filters.status}
                  onChange={(e) => handleFilterChange('status', e.target.value)}
                  size="small"
                >
                  <MenuItem value="">All Status</MenuItem>
                  <MenuItem value="ACTIVE">Active</MenuItem>
                  <MenuItem value="DRAFT">Draft</MenuItem>
                  <MenuItem value="ARCHIVED">Archived</MenuItem>
                </TextField>
              </Grid>
            </Grid>

            <Grid container spacing={2} mb={2}>
              <Grid item xs={12} sm={6} md={3}>
                <TextField
                  fullWidth
                  label="Created By"
                  value={filters.createdBy}
                  onChange={(e) => handleFilterChange('createdBy', e.target.value)}
                  size="small"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
            <TextField
                  fullWidth
              label="Date From"
              type="date"
                  value={filters.dateFrom}
                  onChange={(e) => handleFilterChange('dateFrom', e.target.value)}
              InputLabelProps={{ shrink: true }}
                  size="small"
            />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
            <TextField
                  fullWidth
              label="Date To"
              type="date"
                  value={filters.dateTo}
                  onChange={(e) => handleFilterChange('dateTo', e.target.value)}
              InputLabelProps={{ shrink: true }}
                  size="small"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <Button
                  variant="outlined"
                  onClick={clearFilters}
                  startIcon={<ClearIcon />}
                  fullWidth
                >
                  Clear Filters
                </Button>
              </Grid>
            </Grid>

            {/* Dynamic field filters based on selected document type */}
            {filters.documentType && (
              <Box>
                <Typography variant="subtitle1" gutterBottom sx={{ mt: 2 }}>
                  Document Type Fields
                </Typography>
                <Grid container spacing={2}>
                  {getDocumentTypeFields(Number(filters.documentType)).map((field) => (
                    <Grid item xs={12} sm={6} md={4} key={field.fieldKey}>
                      <TextField
                        fullWidth
                        label={field.name}
                        value={filters.fieldValues[field.fieldKey] || ''}
                        onChange={(e) => handleFieldValueChange(field.fieldKey, e.target.value)}
                        size="small"
                        helperText={`Search in ${field.name} field`}
                      />
                    </Grid>
                  ))}
                </Grid>
              </Box>
            )}
          </Box>
        )}
      </Paper>

      {/* Results Section */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h6">
          Search Results {searchLoading && <CircularProgress size={20} sx={{ ml: 1 }} />}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {documents.length} document{documents.length !== 1 ? 's' : ''} found
        </Typography>
      </Box>

      {/* Documents Grid */}
      {documents.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No documents found
          </Typography>
          <Typography color="text.secondary">
            Try adjusting your search criteria or clear filters to see all documents.
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={2}>
          {documents.map((doc) => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={doc.id}>
              <Card elevation={2} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Box display="flex" alignItems="center" mb={2}>
                    <Avatar sx={{ bgcolor: getFileIconColor(doc.mimeType), mr: 2 }}>
                <FileIcon />
                    </Avatar>
                    <Box flexGrow={1}>
                      <Typography variant="h6" component="div" noWrap>
                        {doc.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" noWrap>
                        {doc.code}
                      </Typography>
                    </Box>
                  </Box>
                  
                  <Box mb={2}>
                    <Chip
                      label={doc.status}
                      color={getStatusColor(doc.status)}
                      size="small"
                      sx={{ mr: 1 }}
                    />
                    {doc.documentTypeName && (
                      <Chip
                        label={doc.documentTypeName}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  </Box>

                  <Typography variant="body2" color="text.secondary" mb={1}>
                    <PersonIcon sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                    {doc.createdBy || 'Unknown'}
                  </Typography>
                  
                  {doc.createdAt && (
                    <Typography variant="body2" color="text.secondary" mb={1}>
                      <CalendarIcon sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                      {new Date(doc.createdAt).toLocaleDateString()}
                    </Typography>
                  )}

                  {doc.fileSize && (
                    <Typography variant="body2" color="text.secondary">
                      Size: {(doc.fileSize / 1024 / 1024).toFixed(2)} MB
                    </Typography>
                  )}
                </CardContent>
                
                <CardActions>
                  <Button 
                    size="small" 
                    onClick={() => handleViewDoc(doc)}
                    startIcon={<ViewIcon />}
                  >
                    View
                  </Button>
                  {doc.filePath && (
                    <Button 
                      size="small" 
                      onClick={() => handleDownload(doc)}
                      startIcon={<DownloadIcon />}
                    >
                      Download
                    </Button>
                  )}
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Document View Modal */}
      <Dialog
        open={!!viewDoc}
        onClose={handleCloseViewDoc}
        maxWidth="md"
        fullWidth
        PaperProps={{
          sx: { minHeight: '60vh' }
        }}
      >
        <DialogTitle>
          <Box display="flex" alignItems="center" justifyContent="space-between">
            <Box>
              <Typography variant="h6">{viewDoc?.name}</Typography>
              <Typography variant="body2" color="text.secondary">
                {viewDoc?.code}
              </Typography>
            </Box>
            <IconButton onClick={handleCloseViewDoc}>
            <CloseIcon />
          </IconButton>
          </Box>
        </DialogTitle>
        
        <DialogContent dividers>
          {viewDoc && (
            <Box>
              {/* Document Info */}
              <Grid container spacing={3} mb={3}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    <BusinessIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                    Document Information
                  </Typography>
                  <List dense>
                    <ListItem>
                      <ListItemText 
                        primary="Document Type" 
                        secondary={viewDoc.documentTypeName || 'Unknown'} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Status" 
                        secondary={
                          <Chip 
                            label={viewDoc.status} 
                            color={getStatusColor(viewDoc.status)}
                            size="small"
                          />
                        } 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Created By" 
                        secondary={viewDoc.createdBy || 'Unknown'} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Created Date" 
                        secondary={viewDoc.createdAt ? new Date(viewDoc.createdAt).toLocaleString() : 'Unknown'} 
                      />
                    </ListItem>
                  </List>
                </Grid>
                
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle1" gutterBottom>
                    <FileIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                    File Information
                  </Typography>
                  <List dense>
                    <ListItem>
                      <ListItemText 
                        primary="File Path" 
                        secondary={viewDoc.filePath || 'No file attached'} 
                      />
                    </ListItem>
                    {viewDoc.fileSize && (
                      <ListItem>
                        <ListItemText 
                          primary="File Size" 
                          secondary={`${(viewDoc.fileSize / 1024 / 1024).toFixed(2)} MB`} 
                        />
                      </ListItem>
                    )}
                    {viewDoc.mimeType && (
                      <ListItem>
                        <ListItemText 
                          primary="File Type" 
                          secondary={viewDoc.mimeType} 
                        />
                      </ListItem>
                    )}
                  </List>
                </Grid>
              </Grid>

              <Divider sx={{ my: 2 }} />

              {/* Document Fields */}
              {viewDoc.metadata && Object.keys(viewDoc.metadata).length > 0 && (
                <Box>
                  <Typography variant="subtitle1" gutterBottom>
                    Document Fields
                  </Typography>
                  <Grid container spacing={2}>
                    {Object.entries(viewDoc.metadata).map(([key, value]) => (
                      <Grid item xs={12} sm={6} key={key}>
                        <Paper variant="outlined" sx={{ p: 2 }}>
                          <Typography variant="body2" color="text.secondary">
                            {key}
                          </Typography>
                          <Typography variant="body1">
                            {value?.toString() || 'No value'}
                </Typography>
                        </Paper>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        
        <DialogActions>
          {viewDoc?.filePath && (
            <Button 
              onClick={() => handleDownload(viewDoc)}
              startIcon={<DownloadIcon />}
              variant="contained"
              sx={{ mr: 1 }}
            >
              Download File
            </Button>
          )}
          <Button onClick={handleCloseViewDoc}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default RetrievalPage;
