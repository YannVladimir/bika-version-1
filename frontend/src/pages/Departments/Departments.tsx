import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Card,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Chip,
  InputAdornment,
  Pagination,
  TextField as MuiTextField,
  styled,
  TableSortLabel,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Alert,
  CircularProgress,
  Grid,
  Fab,
} from "@mui/material";
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  Add as AddIcon,
  Business as DepartmentIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { InputChangeEvent } from "../../types/events";
import { departmentService, Department } from "../../services/api";
import { useAuth } from "../../context/AuthContext";
import { hasRole } from "../../utils/roleUtils";

const StyledCard = styled(Card)(({ theme }) => ({
  borderRadius: 16,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
}));

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  borderBottom: `1px solid ${theme.palette.divider}`,
}));

const StatusChip = styled(Chip)<{ status: "active" | "inactive" }>(
  ({ status, theme }) => ({
    backgroundColor:
      status === "active"
        ? theme.palette.mode === "light"
          ? tokens.secondary.light
          : tokens.secondary.main
        : theme.palette.grey[300],
    color:
      status === "active"
        ? theme.palette.mode === "light"
          ? tokens.secondary.dark
          : theme.palette.getContrastText(tokens.secondary.main)
        : theme.palette.getContrastText(theme.palette.grey[300]),
    borderRadius: 8,
    fontWeight: 500,
  })
);

interface DepartmentFormData {
  name: string;
  code: string;
  description: string;
  companyId: number;
  parentId?: number;
}

interface DepartmentsProps {}

const Departments: React.FC<DepartmentsProps> = () => {
  const { user: currentUser } = useAuth();
  
  // State
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [departmentsPerPage] = useState(10);
  const [sortBy, setSortBy] = useState<keyof Department>("name");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");

  // Dialog states
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  const [editingDepartment, setEditingDepartment] = useState<Department | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [departmentToDelete, setDepartmentToDelete] = useState<Department | null>(null);

  // Form states
  const [formData, setFormData] = useState<DepartmentFormData>({
    name: '',
    code: '',
    description: '',
    companyId: currentUser?.companyId || 1,
  });

  // Load initial data
  useEffect(() => {
    loadData();
  }, [currentUser]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('Loading departments data...', { currentUser });

      if (hasRole(currentUser?.role, 'SUPER_ADMIN')) {
        console.log('Loading all departments for SUPER_ADMIN');
        const departmentsData = await departmentService.getDepartments();
        console.log('Loaded departments:', departmentsData);
        setDepartments(departmentsData);
      } else if (hasRole(currentUser?.role, 'COMPANY_ADMIN') && currentUser?.companyId) {
        console.log('Loading departments for COMPANY_ADMIN, companyId:', currentUser.companyId);
        const departmentsData = await departmentService.getDepartmentsByCompany(currentUser.companyId);
        console.log('Loaded company departments:', departmentsData);
        setDepartments(departmentsData);
      } else {
        console.log('User does not have permission to view departments');
        setError("You don't have permission to view departments");
      }
    } catch (err: any) {
      console.error('Error loading departments:', err);
      setError(err.response?.data?.message || err.message || 'Failed to load departments');
    } finally {
      setLoading(false);
    }
  };

  // Filtering and sorting
  const filteredDepartments = departments.filter(department =>
    department.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    department.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (department.description && department.description.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const sortedDepartments = [...filteredDepartments].sort((a, b) => {
    const aValue = a[sortBy];
    const bValue = b[sortBy];
    
    // Handle undefined values
    if (aValue === undefined && bValue === undefined) return 0;
    if (aValue === undefined) return sortOrder === "asc" ? 1 : -1;
    if (bValue === undefined) return sortOrder === "asc" ? -1 : 1;
    
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      if (aValue < bValue) return sortOrder === "asc" ? -1 : 1;
      if (aValue > bValue) return sortOrder === "asc" ? 1 : -1;
    }
    return 0;
  });

  // Pagination
  const totalPages = Math.ceil(sortedDepartments.length / departmentsPerPage);
  const paginatedDepartments = sortedDepartments.slice(
    (currentPage - 1) * departmentsPerPage,
    currentPage * departmentsPerPage
  );

  // Event handlers
  const handleSort = (column: keyof Department) => {
    if (sortBy === column) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc");
    } else {
      setSortBy(column);
      setSortOrder("asc");
    }
  };

  const handleSearchChange = (e: InputChangeEvent) => {
    setSearchTerm(e.target.value);
    setCurrentPage(1);
  };

  const handlePageChange = (_: unknown, value: number) => {
    setCurrentPage(value);
  };

  // CRUD operations
  const handleAddDepartment = () => {
    setFormData({
      name: '',
      code: '',
      description: '',
      companyId: currentUser?.companyId || 1,
    });
    setAddDialogOpen(true);
  };

  const handleEditDepartment = (department: Department) => {
    setEditingDepartment(department);
    setFormData({
      name: department.name,
      code: department.code,
      description: department.description || '',
      companyId: department.companyId,
      parentId: department.parentId,
    });
    setEditDialogOpen(true);
  };

  const handleDeleteDepartment = (department: Department) => {
    setDepartmentToDelete(department);
    setDeleteDialogOpen(true);
  };

  // Form handlers
  const handleFormChange = (field: keyof DepartmentFormData, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSaveDepartment = async () => {
    try {
      // Basic validation
      if (!formData.name.trim()) {
        setError('Department name is required');
        return;
      }
      if (!formData.code.trim()) {
        setError('Department code is required');
        return;
      }

      if (editingDepartment) {
        // Update existing department
        await departmentService.updateDepartment(editingDepartment.id, {
          name: formData.name,
          code: formData.code,
          description: formData.description,
          companyId: formData.companyId,
          parentId: formData.parentId,
          isActive: true,
        });
      } else {
        // Create new department
        await departmentService.createDepartment({
          name: formData.name,
          code: formData.code,
          description: formData.description,
          companyId: formData.companyId,
          parentId: formData.parentId,
          isActive: true,
        });
      }
      
      setEditDialogOpen(false);
      setAddDialogOpen(false);
      setEditingDepartment(null);
      await loadData();
    } catch (err: any) {
      console.error('Error saving department:', err);
      setError(err.response?.data?.message || 'Failed to save department');
    }
  };

  const handleConfirmDelete = async () => {
    if (!departmentToDelete) return;
    
    try {
      await departmentService.deleteDepartment(departmentToDelete.id);
      setDeleteDialogOpen(false);
      setDepartmentToDelete(null);
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete department');
    }
  };

  const handleCloseDialogs = () => {
    setEditDialogOpen(false);
    setAddDialogOpen(false);
    setDeleteDialogOpen(false);
    setEditingDepartment(null);
    setDepartmentToDelete(null);
    setError(null);
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>Loading departments...</Typography>
      </Box>
    );
  }

  if (error && !departments.length) {
    return (
      <Box p={3}>
        <Alert severity="error">{error}</Alert>
        <Button onClick={loadData} variant="contained" sx={{ mt: 2 }}>
          Retry
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 3 }}>
        <Typography variant="h4" fontWeight="bold">
          Departments
        </Typography>
        {(hasRole(currentUser?.role, 'SUPER_ADMIN') || hasRole(currentUser?.role, 'COMPANY_ADMIN')) && (
          <Fab
            color="primary"
            aria-label="add department"
            onClick={handleAddDepartment}
            sx={{ position: "fixed", bottom: 24, right: 24 }}
          >
            <AddIcon />
          </Fab>
        )}
      </Box>

      {error && (
        <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <StyledCard>
        <Box sx={{ p: 2 }}>
          <MuiTextField
            placeholder="Search departments..."
            value={searchTerm}
            onChange={handleSearchChange}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
            sx={{ mb: 2, width: "100%", maxWidth: 400 }}
          />

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <StyledTableCell>
                    <TableSortLabel
                      active={sortBy === "name"}
                      direction={sortBy === "name" ? sortOrder : "asc"}
                      onClick={() => handleSort("name")}
                    >
                      Name
                    </TableSortLabel>
                  </StyledTableCell>
                  <StyledTableCell>
                    <TableSortLabel
                      active={sortBy === "code"}
                      direction={sortBy === "code" ? sortOrder : "asc"}
                      onClick={() => handleSort("code")}
                    >
                      Code
                    </TableSortLabel>
                  </StyledTableCell>
                  <StyledTableCell>Description</StyledTableCell>
                  <StyledTableCell>
                    <TableSortLabel
                      active={sortBy === "isActive"}
                      direction={sortBy === "isActive" ? sortOrder : "asc"}
                      onClick={() => handleSort("isActive")}
                    >
                      Status
                    </TableSortLabel>
                  </StyledTableCell>
                  <StyledTableCell>Actions</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {paginatedDepartments.length === 0 ? (
                  <TableRow>
                    <StyledTableCell colSpan={5} align="center">
                      <Typography variant="body1" color="text.secondary" sx={{ py: 4 }}>
                        {searchTerm ? 'No departments found matching your search.' : 'No departments found.'}
                      </Typography>
                    </StyledTableCell>
                  </TableRow>
                ) : (
                  paginatedDepartments.map((department) => (
                    <TableRow key={department.id} hover>
                      <StyledTableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <DepartmentIcon sx={{ mr: 1, color: 'text.secondary' }} />
                          <Typography fontWeight="medium">
                            {department.name}
                          </Typography>
                        </Box>
                      </StyledTableCell>
                      <StyledTableCell>
                        <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                          {department.code}
                        </Typography>
                      </StyledTableCell>
                      <StyledTableCell>
                        <Typography variant="body2" color="text.secondary">
                          {department.description || 'No description'}
                        </Typography>
                      </StyledTableCell>
                      <StyledTableCell>
                        <StatusChip
                          label={department.isActive ? "Active" : "Inactive"}
                          status={department.isActive ? "active" : "inactive"}
                        />
                      </StyledTableCell>
                      <StyledTableCell>
                        <Box sx={{ display: "flex", gap: 1 }}>
                          {(hasRole(currentUser?.role, 'SUPER_ADMIN') || 
                            (hasRole(currentUser?.role, 'COMPANY_ADMIN') && department.companyId === currentUser?.companyId)) && (
                            <>
                              <IconButton
                                onClick={() => handleEditDepartment(department)}
                                size="small"
                                color="primary"
                              >
                                <EditIcon fontSize="small" />
                              </IconButton>
                              <IconButton
                                onClick={() => handleDeleteDepartment(department)}
                                size="small"
                                color="error"
                              >
                                <DeleteIcon fontSize="small" />
                              </IconButton>
                            </>
                          )}
                        </Box>
                      </StyledTableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>

          {totalPages > 1 && (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
              <Pagination
                count={totalPages}
                page={currentPage}
                onChange={handlePageChange}
                color="primary"
              />
            </Box>
          )}
        </Box>
      </StyledCard>

      {/* Add Department Dialog */}
      <Dialog open={addDialogOpen} onClose={handleCloseDialogs} maxWidth="md" fullWidth>
        <DialogTitle>Add New Department</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Department Name"
                value={formData.name}
                onChange={(e) => handleFormChange('name', e.target.value)}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Department Code"
                value={formData.code}
                onChange={(e) => handleFormChange('code', e.target.value)}
                fullWidth
                required
                helperText="Unique identifier for the department"
              />
            </Grid>
            <Grid item xs={12}>
              <MuiTextField
                label="Description"
                value={formData.description}
                onChange={(e) => handleFormChange('description', e.target.value)}
                fullWidth
                multiline
                rows={3}
                helperText="Optional description of the department"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialogs}>Cancel</Button>
          <Button onClick={handleSaveDepartment} variant="contained">
            Add Department
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Department Dialog */}
      <Dialog open={editDialogOpen} onClose={handleCloseDialogs} maxWidth="md" fullWidth>
        <DialogTitle>Edit Department</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Department Name"
                value={formData.name}
                onChange={(e) => handleFormChange('name', e.target.value)}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Department Code"
                value={formData.code}
                onChange={(e) => handleFormChange('code', e.target.value)}
                fullWidth
                required
                helperText="Unique identifier for the department"
              />
            </Grid>
            <Grid item xs={12}>
              <MuiTextField
                label="Description"
                value={formData.description}
                onChange={(e) => handleFormChange('description', e.target.value)}
                fullWidth
                multiline
                rows={3}
                helperText="Optional description of the department"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialogs}>Cancel</Button>
          <Button onClick={handleSaveDepartment} variant="contained">
            Save Changes
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={handleCloseDialogs}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete department "{departmentToDelete?.name}"?
            This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialogs}>Cancel</Button>
          <Button onClick={handleConfirmDelete} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Departments; 