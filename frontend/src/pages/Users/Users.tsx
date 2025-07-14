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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
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
  PersonOff as DeactivateIcon,
  PersonAdd as ActivateIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { InputChangeEvent, SelectChangeEvent } from "../../types/events";
import { userService, companyService, departmentService, User, UserRole, Company, Department, CreateUserRequest } from "../../services/api";
import { useAuth } from "../../context/AuthContext";
import { getRoleDisplayName, hasRole, hasMinimumRole } from "../../utils/roleUtils";

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

const RoleChip = styled(Chip)(({ theme }) => ({
  borderRadius: 8,
  fontWeight: 500,
}));

interface UsersProps {}

const Users: React.FC<UsersProps> = () => {
  const { user: currentUser } = useAuth();
  
  // State
  const [users, setUsers] = useState<User[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [usersPerPage] = useState(10);
  const [sortBy, setSortBy] = useState<keyof User>("firstName");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");

  // Dialog states
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<User | null>(null);

  // Form states
  const [formData, setFormData] = useState<CreateUserRequest>({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    companyId: 1,
    departmentId: undefined,
    role: UserRole.USER
  });

  // Load initial data
  useEffect(() => {
    loadData();
  }, [currentUser]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('Loading users data...', { currentUser });

      if (hasRole(currentUser?.role, 'SUPER_ADMIN')) {
        console.log('Loading data for SUPER_ADMIN');
        // Super admin can see all users and companies
        const [usersData, companiesData] = await Promise.all([
          userService.getUsers(),
          companyService.getCompanies()
        ]);
        console.log('Loaded users:', usersData);
        console.log('Loaded companies:', companiesData);
        setUsers(usersData);
        setCompanies(companiesData);
      } else if (hasRole(currentUser?.role, 'COMPANY_ADMIN') && currentUser?.companyId) {
        console.log('Loading data for COMPANY_ADMIN, companyId:', currentUser.companyId);
        // Company admin can see users from their company
        const [usersData, companiesData, departmentsData] = await Promise.all([
          userService.getUsersByCompany(currentUser.companyId),
          companyService.getCompanies(),
          departmentService.getDepartmentsByCompany(currentUser.companyId)
        ]);
        console.log('Loaded users for company:', usersData);
        console.log('Loaded departments:', departmentsData);
        setUsers(usersData);
        setCompanies(companiesData);
        setDepartments(departmentsData);
      } else {
        console.log('User does not have permission to view users');
        setError("You don't have permission to view users");
      }
    } catch (err: any) {
      console.error('Error loading data:', err);
      setError(err.response?.data?.message || err.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const loadDepartmentsByCompany = async (companyId: number) => {
    try {
      const departmentsData = await departmentService.getDepartmentsByCompany(companyId);
      setDepartments(departmentsData);
    } catch (err: any) {
      console.error('Error loading departments:', err);
    }
  };

  // Filtering and sorting
  const filteredUsers = users.filter(user =>
    user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.username.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const sortedUsers = [...filteredUsers].sort((a, b) => {
    const aValue = a[sortBy];
    const bValue = b[sortBy];
    
    // Handle undefined values
    if (aValue === undefined && bValue === undefined) return 0;
    if (aValue === undefined) return sortOrder === "asc" ? 1 : -1;
    if (bValue === undefined) return sortOrder === "asc" ? -1 : 1;
    
    if (aValue < bValue) return sortOrder === "asc" ? -1 : 1;
    if (aValue > bValue) return sortOrder === "asc" ? 1 : -1;
    return 0;
  });

  // Pagination
  const totalPages = Math.ceil(sortedUsers.length / usersPerPage);
  const paginatedUsers = sortedUsers.slice(
    (currentPage - 1) * usersPerPage,
    currentPage * usersPerPage
  );

  // Event handlers
  const handleSort = (column: keyof User) => {
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
  const handleAddUser = () => {
    setFormData({
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      companyId: hasRole(currentUser?.role, 'SUPER_ADMIN') ? (companies.length > 0 ? companies[0].id : 1) : currentUser?.companyId || 1,
      departmentId: undefined,
      role: UserRole.USER
    });
    
    if (hasRole(currentUser?.role, 'COMPANY_ADMIN') && currentUser?.companyId) {
      loadDepartmentsByCompany(currentUser.companyId);
    }
    
    setAddDialogOpen(true);
  };

  const handleEditUser = (user: User) => {
    setEditingUser(user);
    setFormData({
      username: user.username,
      email: user.email,
      password: '', // Don't pre-fill password
      firstName: user.firstName,
      lastName: user.lastName,
      companyId: user.companyId,
      departmentId: user.departmentId,
      role: user.role
    });
    
    if (user.companyId) {
      loadDepartmentsByCompany(user.companyId);
    }
    
    setEditDialogOpen(true);
  };

  const handleDeleteUser = (user: User) => {
    setUserToDelete(user);
    setDeleteDialogOpen(true);
  };

  const handleToggleUserStatus = async (user: User) => {
    try {
      if (user.active) {
        await userService.deactivateUser(user.id);
      } else {
        await userService.activateUser(user.id);
      }
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update user status');
    }
  };

  // Form handlers
  const handleFormChange = (field: keyof CreateUserRequest, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));

    // Load departments when company changes
    if (field === 'companyId' && value) {
      loadDepartmentsByCompany(value);
    }
  };

  const handleSaveUser = async () => {
    try {
      if (editingUser) {
        // Update existing user
        await userService.updateUser(editingUser.id, {
          firstName: formData.firstName,
          lastName: formData.lastName,
          role: formData.role,
          companyId: formData.companyId,
          departmentId: formData.departmentId || undefined
        });
      } else {
        // Validate required fields for new user
        if (!formData.password || formData.password.length < 6) {
          setError('Password is required and must be at least 6 characters long');
          return;
        }
        
        // Create new user - ensure proper data types
        const createData: CreateUserRequest = {
          username: formData.username,
          email: formData.email,
          password: formData.password,
          firstName: formData.firstName,
          lastName: formData.lastName,
          companyId: formData.companyId,
          departmentId: formData.departmentId === 0 ? undefined : formData.departmentId,
          role: formData.role
        };
        console.log('Creating user with data:', createData);
        await userService.createUser(createData);
      }
      
      setEditDialogOpen(false);
      setAddDialogOpen(false);
      setEditingUser(null);
      await loadData();
    } catch (err: any) {
      console.error('Error saving user:', err);
      setError(err.response?.data?.message || 'Failed to save user');
    }
  };

  const handleConfirmDelete = async () => {
    if (!userToDelete) return;
    
    try {
      await userService.deleteUser(userToDelete.id);
      setDeleteDialogOpen(false);
      setUserToDelete(null);
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete user');
    }
  };

  const handleCloseDialogs = () => {
    setEditDialogOpen(false);
    setAddDialogOpen(false);
    setDeleteDialogOpen(false);
    setEditingUser(null);
    setUserToDelete(null);
    setError(null);
  };

  // Get available roles based on current user
  const getAvailableRoles = (): UserRole[] => {
    if (hasRole(currentUser?.role, 'SUPER_ADMIN')) {
      return [UserRole.SUPER_ADMIN, UserRole.COMPANY_ADMIN, UserRole.MANAGER, UserRole.USER];
    } else if (hasRole(currentUser?.role, 'COMPANY_ADMIN')) {
      return [UserRole.MANAGER, UserRole.USER];
    }
    return [UserRole.USER];
  };

  // Get company name
  const getCompanyName = (companyId: number): string => {
    const company = companies.find(c => c.id === companyId);
    return company ? company.name : 'Unknown';
  };

  // Get department name
  const getDepartmentName = (departmentId: number | undefined): string => {
    if (!departmentId) return 'No Department';
    const department = departments.find(d => d.id === departmentId);
    return department ? department.name : 'Unknown';
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>Loading users...</Typography>
      </Box>
    );
  }

  if (error && !users.length) {
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
        Users & Roles
      </Typography>
        {(hasRole(currentUser?.role, 'SUPER_ADMIN') || hasRole(currentUser?.role, 'COMPANY_ADMIN')) && (
          <Fab
            color="primary"
            aria-label="add user"
            onClick={handleAddUser}
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
        placeholder="Search users..."
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
                      active={sortBy === "firstName"}
                      direction={sortBy === "firstName" ? sortOrder : "asc"}
                      onClick={() => handleSort("firstName")}
                  >
                    Name
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                      active={sortBy === "email"}
                      direction={sortBy === "email" ? sortOrder : "asc"}
                      onClick={() => handleSort("email")}
                  >
                    Email
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                      active={sortBy === "role"}
                      direction={sortBy === "role" ? sortOrder : "asc"}
                      onClick={() => handleSort("role")}
                  >
                    Role
                  </TableSortLabel>
                </StyledTableCell>
                  <StyledTableCell>Company</StyledTableCell>
                  <StyledTableCell>Department</StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                      active={sortBy === "active"}
                      direction={sortBy === "active" ? sortOrder : "asc"}
                      onClick={() => handleSort("active")}
                  >
                    Status
                  </TableSortLabel>
                </StyledTableCell>
                  <StyledTableCell>Actions</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
                {paginatedUsers.length === 0 ? (
                  <TableRow>
                    <StyledTableCell colSpan={7} align="center">
                      <Typography variant="body1" color="text.secondary" sx={{ py: 4 }}>
                        {searchTerm ? 'No users found matching your search.' : 'No users found.'}
                      </Typography>
                    </StyledTableCell>
                  </TableRow>
                ) : (
                  paginatedUsers.map((user) => (
                    <TableRow key={user.id} hover>
                      <StyledTableCell>
                        <Typography fontWeight="medium">
                          {user.firstName} {user.lastName}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          @{user.username}
                        </Typography>
                      </StyledTableCell>
                  <StyledTableCell>{user.email}</StyledTableCell>
                      <StyledTableCell>
                        <RoleChip
                          label={getRoleDisplayName(user.role)}
                          color={user.role === UserRole.SUPER_ADMIN ? "error" : 
                                 user.role === UserRole.COMPANY_ADMIN ? "warning" :
                                 user.role === UserRole.MANAGER ? "info" : "default"}
                          variant="outlined"
                        />
                      </StyledTableCell>
                      <StyledTableCell>{getCompanyName(user.companyId)}</StyledTableCell>
                      <StyledTableCell>{getDepartmentName(user.departmentId)}</StyledTableCell>
                  <StyledTableCell>
                    <StatusChip
                          label={user.active ? "Active" : "Inactive"}
                          status={user.active ? "active" : "inactive"}
                    />
                  </StyledTableCell>
                      <StyledTableCell>
                        <Box sx={{ display: "flex", gap: 1 }}>
                          {(hasRole(currentUser?.role, 'SUPER_ADMIN') || 
                            (hasRole(currentUser?.role, 'COMPANY_ADMIN') && user.companyId === currentUser?.companyId)) && (
                            <>
                    <IconButton
                                onClick={() => handleEditUser(user)}
                      size="small"
                                color="primary"
                    >
                      <EditIcon fontSize="small" />
                    </IconButton>
                              <IconButton
                                onClick={() => handleToggleUserStatus(user)}
                                size="small"
                                color={user.active ? "warning" : "success"}
                              >
                                {user.active ? <DeactivateIcon fontSize="small" /> : <ActivateIcon fontSize="small" />}
                              </IconButton>
                              {hasRole(currentUser?.role, 'SUPER_ADMIN') && (
                                <IconButton
                                  onClick={() => handleDeleteUser(user)}
                                  size="small"
                                  color="error"
                                >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                              )}
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

      {/* Add User Dialog */}
      <Dialog open={addDialogOpen} onClose={handleCloseDialogs} maxWidth="md" fullWidth>
        <DialogTitle>Add New User</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Username"
                value={formData.username}
                onChange={(e) => handleFormChange('username', e.target.value)}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => handleFormChange('email', e.target.value)}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="First Name"
                value={formData.firstName}
                onChange={(e) => handleFormChange('firstName', e.target.value)}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <MuiTextField
                label="Last Name"
                value={formData.lastName}
                onChange={(e) => handleFormChange('lastName', e.target.value)}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12}>
              <MuiTextField
                label="Password"
                type="password"
                value={formData.password}
                onChange={(e) => handleFormChange('password', e.target.value)}
                fullWidth
                required
                helperText="Minimum 6 characters required"
                error={formData.password.length > 0 && formData.password.length < 6}
              />
            </Grid>
            {hasRole(currentUser?.role, 'SUPER_ADMIN') && (
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth required>
                  <InputLabel>Company</InputLabel>
                  <Select
                    value={formData.companyId}
                    onChange={(e) => handleFormChange('companyId', e.target.value)}
                    label="Company"
                  >
                    {companies.map(company => (
                      <MenuItem key={company.id} value={company.id}>
                        {company.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Department</InputLabel>
                <Select
                  value={formData.departmentId || 0}
                  onChange={(e) => handleFormChange('departmentId', e.target.value === 0 ? undefined : e.target.value)}
                  label="Department"
                >
                  <MenuItem value={0}>No Department</MenuItem>
                  {departments.map(department => (
                    <MenuItem key={department.id} value={department.id}>
                      {department.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel>Role</InputLabel>
                <Select
                  value={formData.role}
                  onChange={(e) => handleFormChange('role', e.target.value)}
                  label="Role"
                >
                  {getAvailableRoles().map(role => (
                    <MenuItem key={role} value={role}>
                      {getRoleDisplayName(role)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialogs}>Cancel</Button>
          <Button onClick={handleSaveUser} variant="contained">
            Add User
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit User Dialog */}
      <Dialog open={editDialogOpen} onClose={handleCloseDialogs} maxWidth="md" fullWidth>
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
            <MuiTextField
                label="First Name"
                value={formData.firstName}
                onChange={(e) => handleFormChange('firstName', e.target.value)}
              fullWidth
                required
            />
            </Grid>
            <Grid item xs={12} sm={6}>
            <MuiTextField
                label="Last Name"
                value={formData.lastName}
                onChange={(e) => handleFormChange('lastName', e.target.value)}
              fullWidth
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel>Role</InputLabel>
                <Select
                  value={formData.role}
                  onChange={(e) => handleFormChange('role', e.target.value)}
              label="Role"
                >
                  {getAvailableRoles().map(role => (
                    <MenuItem key={role} value={role}>
                      {getRoleDisplayName(role)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Department</InputLabel>
                <Select
                  value={formData.departmentId || 0}
                  onChange={(e) => handleFormChange('departmentId', e.target.value === 0 ? undefined : e.target.value)}
                  label="Department"
                >
                  <MenuItem value={0}>No Department</MenuItem>
                  {departments.map(department => (
                    <MenuItem key={department.id} value={department.id}>
                      {department.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialogs}>Cancel</Button>
          <Button onClick={handleSaveUser} variant="contained">
            Save Changes
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={handleCloseDialogs}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete user "{userToDelete?.firstName} {userToDelete?.lastName}"?
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

export default Users;
