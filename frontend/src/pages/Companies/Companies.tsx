import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  Card,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Chip,
  styled,
  Pagination,
  InputAdornment,
  TableSortLabel,
  Alert,
  Snackbar,
  CircularProgress,
  FormControlLabel,
  Switch,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { companyService } from "../../services/api";
import { Company, CreateCompanyRequest } from "../../services/api/types";

const HeaderBox = styled(Box)({
  display: "flex",
  justifyContent: "space-between",
  alignItems: "center",
  marginBottom: 24,
});

const StyledCard = styled(Card)(({ theme }) => ({
  borderRadius: 16,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
}));

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  borderBottom: `1px solid ${theme.palette.divider}`,
}));

const StatusChip = styled(Chip)<{ status: boolean }>(
  ({ status, theme }) => ({
    backgroundColor: status ? tokens.secondary.main : theme.palette.grey[400],
    color: theme.palette.common.white,
    fontWeight: 500,
    fontSize: "0.75rem",
  })
);

const LoadingContainer = styled(Box)({
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  height: "400px",
});

interface CompanyFormData {
  name: string;
  code: string;
  email: string;
  phone: string;
  address: string;
  description: string;
  isActive: boolean;
}

const Companies: React.FC = () => {
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  // Dialog states
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingCompany, setEditingCompany] = useState<Company | null>(null);
  const [deletingCompany, setDeletingCompany] = useState<Company | null>(null);
  
  // Form state
  const [formData, setFormData] = useState<CompanyFormData>({
    name: "",
    code: "",
    email: "",
    phone: "",
    address: "",
    description: "",
    isActive: true,
  });
  const [formErrors, setFormErrors] = useState<Partial<CompanyFormData>>({});
  const [submitting, setSubmitting] = useState(false);
  
  // Table state
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const [orderBy, setOrderBy] = useState<keyof Company>("name");
  const [order, setOrder] = useState<"asc" | "desc">("asc");
  const rowsPerPage = 10;

  // Load companies on component mount
  useEffect(() => {
    loadCompanies();
  }, []);

  const loadCompanies = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await companyService.getCompanies();
      setCompanies(data);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to load companies");
    } finally {
      setLoading(false);
    }
  };

  const validateForm = (): boolean => {
    const errors: Partial<CompanyFormData> = {};
    
    if (!formData.name.trim()) errors.name = "Company name is required";
    if (!formData.code.trim()) errors.code = "Company code is required";
    if (!formData.email.trim()) errors.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = "Please enter a valid email address";
    }
    
    // Check for duplicate code (excluding current company when editing)
    const duplicateCode = companies.find(
      (company) => 
        company.code.toLowerCase() === formData.code.toLowerCase() &&
        company.id !== editingCompany?.id
    );
    if (duplicateCode) {
      errors.code = "Company code already exists";
    }
    
    // Check for duplicate email (excluding current company when editing)
    const duplicateEmail = companies.find(
      (company) => 
        company.email.toLowerCase() === formData.email.toLowerCase() &&
        company.id !== editingCompany?.id
    );
    if (duplicateEmail) {
      errors.email = "Email already exists";
    }
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const resetForm = () => {
    setFormData({
      name: "",
      code: "",
      email: "",
      phone: "",
      address: "",
      description: "",
      isActive: true,
    });
    setFormErrors({});
  };

  const handleCreateOpen = () => {
    resetForm();
    setCreateDialogOpen(true);
  };

  const handleCreateClose = () => {
    setCreateDialogOpen(false);
    resetForm();
  };

  const handleEditOpen = (company: Company) => {
    setEditingCompany(company);
    setFormData({
      name: company.name,
      code: company.code,
      email: company.email,
      phone: company.phone || "",
      address: company.address || "",
      description: company.description || "",
      isActive: company.isActive,
    });
    setFormErrors({});
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setEditingCompany(null);
    resetForm();
  };

  const handleDeleteOpen = (company: Company) => {
    setDeletingCompany(company);
    setDeleteDialogOpen(true);
  };

  const handleDeleteClose = () => {
    setDeleteDialogOpen(false);
    setDeletingCompany(null);
  };

  const handleCreate = async () => {
    if (!validateForm()) return;
    
    try {
      setSubmitting(true);
      const createData: CreateCompanyRequest = {
        name: formData.name.trim(),
        code: formData.code.trim(),
        email: formData.email.trim(),
        phone: formData.phone.trim() || undefined,
        address: formData.address.trim() || undefined,
        description: formData.description.trim() || undefined,
        isActive: formData.isActive,
      };
      
      await companyService.createCompany(createData);
      setSuccess("Company created successfully");
      handleCreateClose();
      loadCompanies();
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to create company");
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdate = async () => {
    if (!editingCompany || !validateForm()) return;
    
    try {
      setSubmitting(true);
      const updateData: CreateCompanyRequest = {
        name: formData.name.trim(),
        code: formData.code.trim(),
        email: formData.email.trim(),
        phone: formData.phone.trim() || undefined,
        address: formData.address.trim() || undefined,
        description: formData.description.trim() || undefined,
        isActive: formData.isActive,
      };
      
      await companyService.updateCompany(editingCompany.id, updateData);
      setSuccess("Company updated successfully");
      handleEditClose();
      loadCompanies();
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to update company");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deletingCompany) return;
    
    try {
      setSubmitting(true);
      await companyService.deleteCompany(deletingCompany.id);
      setSuccess("Company deleted successfully");
      handleDeleteClose();
      loadCompanies();
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to delete company");
    } finally {
      setSubmitting(false);
    }
  };

  const handleInputChange = (field: keyof CompanyFormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    setFormData(prev => ({ ...prev, [field]: value }));
    // Clear error when user starts typing
    if (formErrors[field]) {
      setFormErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSort = (property: keyof Company) => {
    const isAsc = orderBy === property && order === "asc";
    setOrder(isAsc ? "desc" : "asc");
    setOrderBy(property);
  };

  // Filter and sort companies
  const filteredCompanies = companies.filter((company) =>
    Object.values(company)
      .join(" ")
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  const sortedCompanies = [...filteredCompanies].sort((a, b) => {
    const aValue = a[orderBy] ?? "";
    const bValue = b[orderBy] ?? "";
    
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      return order === "asc" 
        ? aValue.localeCompare(bValue)
        : bValue.localeCompare(aValue);
    }
    
    if (aValue < bValue) return order === "asc" ? -1 : 1;
    if (aValue > bValue) return order === "asc" ? 1 : -1;
    return 0;
  });

  // Pagination
  const paginatedCompanies = sortedCompanies.slice(
    (page - 1) * rowsPerPage,
    page * rowsPerPage
  );
  const pageCount = Math.ceil(sortedCompanies.length / rowsPerPage);

  if (loading) {
    return (
      <LoadingContainer>
        <CircularProgress />
      </LoadingContainer>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <HeaderBox>
        <Typography variant="h4" fontWeight="bold">
          Companies Management
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleCreateOpen}
          sx={{
            borderRadius: 2,
            textTransform: "none",
            fontWeight: 500,
          }}
        >
          Add Company
        </Button>
      </HeaderBox>

      <StyledCard sx={{ p: 3 }}>
        <Box sx={{ mb: 3 }}>
      <TextField
        placeholder="Search companies..."
        value={search}
            onChange={(e) => setSearch(e.target.value)}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
        }}
            sx={{ width: 300 }}
      />
        </Box>

        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "name"}
                    direction={orderBy === "name" ? order : "asc"}
                    onClick={() => handleSort("name")}
                  >
                    Company Name
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "code"}
                    direction={orderBy === "code" ? order : "asc"}
                    onClick={() => handleSort("code")}
                  >
                    Code
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "email"}
                    direction={orderBy === "email" ? order : "asc"}
                    onClick={() => handleSort("email")}
                  >
                    Email
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>Phone</StyledTableCell>
                <StyledTableCell>Address</StyledTableCell>
                <StyledTableCell>Status</StyledTableCell>
                <StyledTableCell>Actions</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedCompanies.map((company) => (
                <TableRow key={company.id} hover>
                  <StyledTableCell>
                    <Typography variant="body2" fontWeight={500}>
                      {company.name}
                    </Typography>
                  </StyledTableCell>
                  <StyledTableCell>
                    <Typography variant="body2" color="text.secondary">
                      {company.code}
                    </Typography>
                  </StyledTableCell>
                  <StyledTableCell>
                    <Typography variant="body2" color="text.secondary">
                      {company.email}
                    </Typography>
                  </StyledTableCell>
                  <StyledTableCell>
                    <Typography variant="body2" color="text.secondary">
                      {company.phone || "-"}
                    </Typography>
                  </StyledTableCell>
                  <StyledTableCell>
                    <Typography variant="body2" color="text.secondary">
                      {company.address || "-"}
                    </Typography>
                  </StyledTableCell>
                  <StyledTableCell>
                    <StatusChip
                      status={company.isActive}
                      label={company.isActive ? "Active" : "Inactive"}
                      size="small"
                    />
                  </StyledTableCell>
                  <StyledTableCell>
                    <IconButton
                      size="small"
                      onClick={() => handleEditOpen(company)}
                      sx={{ mr: 1 }}
                    >
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDeleteOpen(company)}
                      color="error"
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </StyledTableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        {pageCount > 1 && (
          <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
          <Pagination
            count={pageCount}
            page={page}
              onChange={(_, newPage) => setPage(newPage)}
            color="primary"
          />
        </Box>
        )}
      </StyledCard>

      {/* Create Company Dialog */}
      <Dialog
        open={createDialogOpen}
        onClose={handleCreateClose}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Add New Company</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <TextField
              label="Company Name"
              fullWidth
              required
              value={formData.name}
              onChange={handleInputChange("name")}
              error={!!formErrors.name}
              helperText={formErrors.name}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Company Code"
              fullWidth
              required
              value={formData.code}
              onChange={handleInputChange("code")}
              error={!!formErrors.code}
              helperText={formErrors.code}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Email"
              type="email"
              fullWidth
              required
              value={formData.email}
              onChange={handleInputChange("email")}
              error={!!formErrors.email}
              helperText={formErrors.email}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Phone"
              fullWidth
              value={formData.phone}
              onChange={handleInputChange("phone")}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Address"
              fullWidth
              multiline
              rows={2}
              value={formData.address}
              onChange={handleInputChange("address")}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Description"
              fullWidth
              multiline
              rows={3}
              value={formData.description}
              onChange={handleInputChange("description")}
              sx={{ mb: 2 }}
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formData.isActive}
                  onChange={handleInputChange("isActive")}
                />
              }
              label="Active"
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCreateClose}>Cancel</Button>
          <Button
            onClick={handleCreate}
            variant="contained"
            disabled={submitting}
          >
            {submitting ? <CircularProgress size={20} /> : "Create"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Company Dialog */}
      <Dialog
        open={editDialogOpen}
        onClose={handleEditClose}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Edit Company</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <TextField
              label="Company Name"
              fullWidth
              required
              value={formData.name}
              onChange={handleInputChange("name")}
              error={!!formErrors.name}
              helperText={formErrors.name}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Company Code"
              fullWidth
              required
              value={formData.code}
              onChange={handleInputChange("code")}
              error={!!formErrors.code}
              helperText={formErrors.code}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Email"
              type="email"
              fullWidth
              required
              value={formData.email}
              onChange={handleInputChange("email")}
              error={!!formErrors.email}
              helperText={formErrors.email}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Phone"
              fullWidth
              value={formData.phone}
              onChange={handleInputChange("phone")}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Address"
              fullWidth
              multiline
              rows={2}
              value={formData.address}
              onChange={handleInputChange("address")}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Description"
              fullWidth
              multiline
              rows={3}
              value={formData.description}
              onChange={handleInputChange("description")}
              sx={{ mb: 2 }}
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formData.isActive}
                  onChange={handleInputChange("isActive")}
                />
              }
              label="Active"
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose}>Cancel</Button>
          <Button
            onClick={handleUpdate}
            variant="contained"
            disabled={submitting}
          >
            {submitting ? <CircularProgress size={20} /> : "Update"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={handleDeleteClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete the company "{deletingCompany?.name}"?
            This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteClose}>Cancel</Button>
          <Button
            onClick={handleDelete}
            variant="contained"
            color="error"
            disabled={submitting}
          >
            {submitting ? <CircularProgress size={20} /> : "Delete"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Success/Error Snackbars */}
      <Snackbar
        open={!!success}
        autoHideDuration={4000}
        onClose={() => setSuccess(null)}
      >
        <Alert onClose={() => setSuccess(null)} severity="success">
          {success}
        </Alert>
      </Snackbar>

      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
      >
        <Alert onClose={() => setError(null)} severity="error">
          {error}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Companies;
