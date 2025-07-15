import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControlLabel,
  Switch,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Divider,
  CircularProgress,
  Alert,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  AddCircle as AddCircleIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { documentTypeService, DocumentType, DocumentField, FieldType } from "../../services/api";

const DocumentTypesPage: React.FC = () => {
  const { user } = useAuth();
  
  // State
  const [documentTypes, setDocumentTypes] = useState<DocumentType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingDocType, setEditingDocType] = useState<DocumentType | null>(null);
  const [newField, setNewField] = useState<Partial<DocumentField>>({
    name: "",
    fieldType: "TEXT",
    required: false,
  });
  const [newOption, setNewOption] = useState("");

  // Load document types on component mount
  useEffect(() => {
    loadDocumentTypes();
  }, [user]);

  const loadDocumentTypes = async () => {
    try {
      setLoading(true);
      setError(null);
      
      if (user?.companyId) {
        const data = await documentTypeService.getDocumentTypesByCompany(user.companyId);
        setDocumentTypes(data);
      }
    } catch (err: any) {
      console.error('Error loading document types:', err);
      setError(err.response?.data?.message || err.message || 'Failed to load document types');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateNew = () => {
    if (!user?.companyId) {
      setError('Company information is required');
      return;
    }
    
    setEditingDocType({
      name: "",
      code: "",
      description: "",
      companyId: user.companyId,
      fields: [],
      isActive: true,
    });
    setOpenDialog(true);
  };

  const handleEdit = (docType: DocumentType) => {
    setEditingDocType(docType);
    setOpenDialog(true);
  };

  const handleSave = async () => {
    if (!editingDocType) return;
    
    try {
      setError(null);
      
      // Basic validation
      if (!editingDocType.name.trim()) {
        setError('Document type name is required');
        return;
      }
      
      if (!editingDocType.code.trim()) {
        setError('Document type code is required');
        return;
      }

      // Ensure display order is set for fields
      const fieldsWithOrder = editingDocType.fields.map((field, index) => ({
        ...field,
        displayOrder: field.displayOrder || index + 1,
        fieldKey: field.fieldKey || documentTypeService.generateFieldKey(field.name),
      }));

      const documentTypeData = {
        ...editingDocType,
        fields: fieldsWithOrder,
      };

      if (editingDocType.id) {
        // Update existing
        await documentTypeService.updateDocumentType(editingDocType.id, documentTypeData);
      } else {
        // Create new
        await documentTypeService.createDocumentType(documentTypeData);
      }
      
      setOpenDialog(false);
      setEditingDocType(null);
      await loadDocumentTypes();
    } catch (err: any) {
      console.error('Error saving document type:', err);
      setError(err.response?.data?.message || 'Failed to save document type');
    }
  };

  const handleDelete = async (docType: DocumentType) => {
    if (!docType.id) return;
    
    if (window.confirm(`Are you sure you want to delete "${docType.name}"?`)) {
      try {
        await documentTypeService.deleteDocumentType(docType.id);
        await loadDocumentTypes();
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to delete document type');
      }
    }
  };

  const handleAddField = () => {
    if (editingDocType && newField.name) {
      const field: DocumentField = {
        name: newField.name,
        fieldKey: documentTypeService.generateFieldKey(newField.name),
        fieldType: newField.fieldType || "TEXT",
        required: newField.required || false,
        description: newField.description,
        active: true,
        displayOrder: editingDocType.fields.length + 1,
        options: newField.fieldType === "SELECT" ? [] : undefined,
      };

      setEditingDocType({
        ...editingDocType,
        fields: [...editingDocType.fields, field],
      });
      
      setNewField({ name: "", fieldType: "TEXT", required: false });
    }
  };

  const handleAddOption = (fieldIndex: number) => {
    if (editingDocType && newOption.trim()) {
      const updatedFields = [...editingDocType.fields];
      const field = updatedFields[fieldIndex];
      
      if (field.options) {
        field.options = [...field.options, newOption.trim()];
      } else {
        field.options = [newOption.trim()];
      }

      setEditingDocType({
        ...editingDocType,
        fields: updatedFields,
      });
      
      setNewOption("");
    }
  };

  const handleDeleteField = (fieldIndex: number) => {
    if (editingDocType) {
      const updatedFields = editingDocType.fields.filter((_, index) => index !== fieldIndex);
      setEditingDocType({
        ...editingDocType,
        fields: updatedFields,
      });
    }
  };

  const handleDeleteOption = (fieldIndex: number, option: string) => {
    if (editingDocType) {
      const updatedFields = [...editingDocType.fields];
      const field = updatedFields[fieldIndex];
      
      if (field.options) {
        field.options = field.options.filter(opt => opt !== option);
      }

      setEditingDocType({
        ...editingDocType,
        fields: updatedFields,
      });
    }
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingDocType(null);
    setNewField({ name: "", fieldType: "TEXT", required: false });
    setNewOption("");
    setError(null);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>Loading document types...</Typography>
      </Box>
    );
  }

  return (
    <Box width="100%" boxSizing="border-box" p={3}>
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={4}
      >
        <Typography variant="h4" fontWeight={600}>
          Document Types
        </Typography>
        <Button
          variant="contained"
          color="secondary"
          startIcon={<AddIcon />}
          onClick={handleCreateNew}
          sx={{ fontWeight: 600, boxShadow: 2 }}
        >
          Create New Document Type
        </Button>
      </Box>

      {error && (
        <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {documentTypes.length === 0 ? (
          <Grid item xs={12}>
            <Typography variant="body1" color="text.secondary" align="center" sx={{ py: 4 }}>
              No document types found. Create your first document type to get started.
            </Typography>
          </Grid>
        ) : (
          documentTypes.map((docType) => (
            <Grid item xs={12} md={6} lg={4} key={docType.id}>
              <Card
                elevation={3}
                sx={{
                  background: `linear-gradient(135deg, ${tokens.primary.light} 0%, ${tokens.secondary.light} 100%)`,
                  color: tokens.grey[900],
                  borderRadius: 3,
                  height: "100%",
                  boxShadow: "0 4px 24px 0 rgba(0,0,0,0.07)",
                  transition: "transform 0.2s",
                  "&:hover": {
                    transform: "translateY(-4px) scale(1.02)",
                    boxShadow: "0 8px 32px 0 rgba(0,0,0,0.12)",
                  },
                }}
              >
                <CardContent>
                  <Box
                    display="flex"
                    justifyContent="space-between"
                    alignItems="center"
                    mb={2}
                  >
                    <Typography variant="h6" fontWeight={600}>
                      {docType.name}
                    </Typography>
                    <Box>
                      <IconButton
                        onClick={() => handleEdit(docType)}
                        color="primary"
                        size="small"
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton
                        onClick={() => handleDelete(docType)}
                        color="error"
                        size="small"
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Box>
                  </Box>
                  <Typography variant="body2" color="text.primary" mb={2}>
                    {docType.description || 'No description'}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Code: {docType.code}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" display="block">
                    Fields: {docType.fields?.length || 0}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))
        )}
      </Grid>

      {/* Create/Edit Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          {editingDocType?.id ? "Edit Document Type" : "Create New Document Type"}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Document Type Name"
                  fullWidth
                  required
                  value={editingDocType?.name || ""}
                  onChange={(e) =>
                    setEditingDocType(
                      editingDocType
                        ? { ...editingDocType, name: e.target.value }
                        : null
                    )
                  }
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Code"
                  fullWidth
                  required
                  value={editingDocType?.code || ""}
                  onChange={(e) =>
                    setEditingDocType(
                      editingDocType
                        ? { ...editingDocType, code: e.target.value }
                        : null
                    )
                  }
                  helperText="Unique identifier for the document type"
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  label="Description"
                  fullWidth
                  multiline
                  rows={2}
                  value={editingDocType?.description || ""}
                  onChange={(e) =>
                    setEditingDocType(
                      editingDocType
                        ? { ...editingDocType, description: e.target.value }
                        : null
                    )
                  }
                />
              </Grid>

              {/* Fields Section */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom>
                  Fields
                </Typography>
                <List>
                  {editingDocType?.fields.map((field, fieldIndex) => (
                    <React.Fragment key={fieldIndex}>
                      <ListItem>
                        <ListItemText
                          primary={field.name}
                          secondary={`${field.fieldType.toLowerCase()} ${
                            field.required ? "(Required)" : ""
                          }`}
                        />
                        <ListItemSecondaryAction>
                          <IconButton
                            edge="end"
                            onClick={() => handleDeleteField(fieldIndex)}
                            color="error"
                          >
                            <DeleteIcon />
                          </IconButton>
                        </ListItemSecondaryAction>
                      </ListItem>
                      {field.fieldType === "SELECT" && (
                        <Box sx={{ pl: 4, pr: 2, pb: 2 }}>
                          <Box display="flex" gap={1} mb={1}>
                            <TextField
                              size="small"
                              label="Add Option"
                              value={newOption}
                              onChange={(e) => setNewOption(e.target.value)}
                            />
                            <Button
                              variant="outlined"
                              color="primary"
                              onClick={() => handleAddOption(fieldIndex)}
                              disabled={!newOption}
                            >
                              Add
                            </Button>
                          </Box>
                          <Box display="flex" gap={0.5} flexWrap="wrap">
                            {field.options?.map((option) => (
                              <Chip
                                key={option}
                                label={option}
                                onDelete={() =>
                                  handleDeleteOption(fieldIndex, option)
                                }
                                color="secondary"
                              />
                            ))}
                          </Box>
                        </Box>
                      )}
                      <Divider />
                    </React.Fragment>
                  ))}
                </List>

                {/* Add New Field */}
                <Box
                  sx={{ mt: 2, p: 2, border: `1px dashed ${tokens.grey[300]}` }}
                >
                  <Typography variant="subtitle1" gutterBottom>
                    Add New Field
                  </Typography>
                  <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} sm={4}>
                      <TextField
                        label="Field Name"
                        fullWidth
                        value={newField.name}
                        onChange={(e) =>
                          setNewField({ ...newField, name: e.target.value })
                        }
                      />
                    </Grid>
                    <Grid item xs={12} sm={3}>
                      <FormControl fullWidth>
                        <InputLabel>Type</InputLabel>
                        <Select
                          value={newField.fieldType}
                          label="Type"
                          onChange={(e) =>
                            setNewField({ ...newField, fieldType: e.target.value as FieldType })
                          }
                        >
                          <MenuItem value="TEXT">Text</MenuItem>
                          <MenuItem value="TEXTAREA">Text Area</MenuItem>
                          <MenuItem value="NUMBER">Number</MenuItem>
                          <MenuItem value="DATE">Date</MenuItem>
                          <MenuItem value="SELECT">Select</MenuItem>
                          <MenuItem value="CHECKBOX">Checkbox</MenuItem>
                          <MenuItem value="EMAIL">Email</MenuItem>
                          <MenuItem value="PHONE">Phone</MenuItem>
                          <MenuItem value="URL">URL</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={12} sm={3}>
                      <FormControlLabel
                        control={
                          <Switch
                            checked={newField.required}
                            onChange={(e) =>
                              setNewField({
                                ...newField,
                                required: e.target.checked,
                              })
                            }
                          />
                        }
                        label="Required"
                      />
                    </Grid>
                    <Grid item xs={12} sm={2}>
                      <Button
                        variant="contained"
                        color="success"
                        onClick={handleAddField}
                        disabled={!newField.name}
                        fullWidth
                        startIcon={<AddCircleIcon />}
                      >
                        Add Field
                      </Button>
                    </Grid>
                  </Grid>
                </Box>
              </Grid>
            </Grid>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="inherit">
            Cancel
          </Button>
          <Button
            onClick={handleSave}
            variant="contained"
            color="primary"
            disabled={!editingDocType?.name || !editingDocType?.code}
            startIcon={<EditIcon />}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DocumentTypesPage;
