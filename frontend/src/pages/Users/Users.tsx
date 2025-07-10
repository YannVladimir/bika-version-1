import React, { useState } from "react";
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
} from "@mui/material";
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { InputChangeEvent, SelectChangeEvent } from "../../types/events";

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
          : theme.palette.common.white
        : theme.palette.grey[700],
  })
);

interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  status: "active" | "inactive";
}

const mockUsers: User[] = [
  {
    id: 1,
    name: "Alice Johnson",
    email: "alice.johnson@email.com",
    role: "Administrator",
    status: "active",
  },
  {
    id: 2,
    name: "Bob Smith",
    email: "bob.smith@email.com",
    role: "Editor",
    status: "active",
  },
  {
    id: 3,
    name: "Carol Lee",
    email: "carol.lee@email.com",
    role: "Viewer",
    status: "inactive",
  },
  {
    id: 4,
    name: "David Kim",
    email: "david.kim@email.com",
    role: "Editor",
    status: "active",
  },
  {
    id: 5,
    name: "Eva Brown",
    email: "eva.brown@email.com",
    role: "Viewer",
    status: "active",
  },
  {
    id: 6,
    name: "Frank Green",
    email: "frank.green@email.com",
    role: "Administrator",
    status: "inactive",
  },
  {
    id: 7,
    name: "Grace White",
    email: "grace.white@email.com",
    role: "Editor",
    status: "active",
  },
  {
    id: 8,
    name: "Henry Black",
    email: "henry.black@email.com",
    role: "Viewer",
    status: "active",
  },
  {
    id: 9,
    name: "Ivy Blue",
    email: "ivy.blue@email.com",
    role: "Editor",
    status: "inactive",
  },
  {
    id: 10,
    name: "Jack Red",
    email: "jack.red@email.com",
    role: "Viewer",
    status: "active",
  },
];

const Users: React.FC = () => {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const rowsPerPage = 5;
  const [users] = useState<User[]>(mockUsers);
  const [orderBy, setOrderBy] = useState<keyof User | "">("");
  const [order, setOrder] = useState<"asc" | "desc">("asc");
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [userForm, setUserForm] = useState({
    name: "",
    email: "",
    role: "",
    status: "active" as "active" | "inactive",
  });

  // Sorting logic
  function sortComparator(a: User, b: User) {
    if (!orderBy) return 0;
    const aValue = a[orderBy] ?? "";
    const bValue = b[orderBy] ?? "";
    if (aValue < bValue) return order === "asc" ? -1 : 1;
    if (aValue > bValue) return order === "asc" ? 1 : -1;
    return 0;
  }

  // Filter users by search
  const filteredUsers = users.filter((user) =>
    Object.values(user).join(" ").toLowerCase().includes(search.toLowerCase())
  );

  // Sort filtered users
  const sortedUsers = [...filteredUsers].sort(sortComparator);

  // Pagination logic
  const paginatedUsers = sortedUsers.slice(
    (page - 1) * rowsPerPage,
    page * rowsPerPage
  );
  const pageCount = Math.ceil(sortedUsers.length / rowsPerPage);

  const handleEditOpen = (user: User) => {
    setEditingUser(user);
    setUserForm({
      name: user.name,
      email: user.email,
      role: user.role,
      status: user.status,
    });
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setEditingUser(null);
  };

  const handleEditSave = () => {
    if (editingUser) {
      // TODO: Implement actual user update when API integration is added
      // For now, just close the dialog
      console.log("User update will be implemented in Phase 2");
    }
    setEditDialogOpen(false);
    setEditingUser(null);
  };

  const handleSearchChange = (e: InputChangeEvent) => {
    setSearch(e.target.value);
    setPage(1);
  };

  const handlePageChange = (_: unknown, value: number) => {
    setPage(value);
  };

  const handleNameChange = (e: InputChangeEvent) => {
    setUserForm((f) => ({ ...f, name: e.target.value }));
  };

  const handleEmailChange = (e: InputChangeEvent) => {
    setUserForm((f) => ({ ...f, email: e.target.value }));
  };

  const handleRoleChange = (e: InputChangeEvent) => {
    setUserForm((f) => ({ ...f, role: e.target.value }));
  };

  const handleStatusChange = (e: SelectChangeEvent) => {
    setUserForm((f) => ({
      ...f,
      status: e.target.value as "active" | "inactive",
    }));
  };

  return (
    <Box width="100%" boxSizing="border-box">
      <Typography variant="h4" fontWeight={600} mb={4}>
        Users & Roles
      </Typography>

      {/* Search input */}
      <MuiTextField
        placeholder="Search users..."
        value={search}
        onChange={handleSearchChange}
        size="small"
        sx={{ mb: 2, width: 320 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
        }}
      />

      <StyledCard>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "name"}
                    direction={orderBy === "name" ? order : "asc"}
                    onClick={() => {
                      if (orderBy === "name")
                        setOrder(order === "asc" ? "desc" : "asc");
                      else {
                        setOrderBy("name");
                        setOrder("asc");
                      }
                    }}
                  >
                    Name
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "email"}
                    direction={orderBy === "email" ? order : "asc"}
                    onClick={() => {
                      if (orderBy === "email")
                        setOrder(order === "asc" ? "desc" : "asc");
                      else {
                        setOrderBy("email");
                        setOrder("asc");
                      }
                    }}
                  >
                    Email
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "role"}
                    direction={orderBy === "role" ? order : "asc"}
                    onClick={() => {
                      if (orderBy === "role")
                        setOrder(order === "asc" ? "desc" : "asc");
                      else {
                        setOrderBy("role");
                        setOrder("asc");
                      }
                    }}
                  >
                    Role
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "status"}
                    direction={orderBy === "status" ? order : "asc"}
                    onClick={() => {
                      if (orderBy === "status")
                        setOrder(order === "asc" ? "desc" : "asc");
                      else {
                        setOrderBy("status");
                        setOrder("asc");
                      }
                    }}
                  >
                    Status
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell align="right">Actions</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedUsers.map((user) => (
                <TableRow key={user.id}>
                  <StyledTableCell>{user.name}</StyledTableCell>
                  <StyledTableCell>{user.email}</StyledTableCell>
                  <StyledTableCell>{user.role}</StyledTableCell>
                  <StyledTableCell>
                    <StatusChip
                      label={user.status}
                      status={user.status}
                      size="small"
                    />
                  </StyledTableCell>
                  <StyledTableCell align="right">
                    <IconButton
                      size="small"
                      sx={{ mr: 1 }}
                      onClick={() => handleEditOpen(user)}
                    >
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton size="small" color="error">
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </StyledTableCell>
                </TableRow>
              ))}
              {paginatedUsers.length === 0 && (
                <TableRow>
                  <StyledTableCell colSpan={5} align="center">
                    No users found.
                  </StyledTableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
        {/* Pagination */}
        <Box display="flex" justifyContent="flex-end" p={2}>
          <Pagination
            count={pageCount}
            page={page}
            onChange={handlePageChange}
            color="primary"
            shape="rounded"
          />
        </Box>
      </StyledCard>

      <Dialog
        open={editDialogOpen}
        onClose={handleEditClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Edit User</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <MuiTextField
              fullWidth
              label="Name"
              margin="dense"
              value={userForm.name}
              onChange={handleNameChange}
            />
            <MuiTextField
              fullWidth
              label="Email"
              margin="dense"
              value={userForm.email}
              onChange={handleEmailChange}
            />
            <MuiTextField
              fullWidth
              label="Role"
              margin="dense"
              value={userForm.role}
              onChange={handleRoleChange}
            />
            <MuiTextField
              fullWidth
              label="Status"
              margin="dense"
              select
              value={userForm.status}
              onChange={handleStatusChange}
              SelectProps={{ native: true }}
            >
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </MuiTextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose}>Cancel</Button>
          <Button
            variant="contained"
            onClick={handleEditSave}
            sx={{
              background: `linear-gradient(90deg, ${tokens.primary.main}, ${tokens.secondary.main})`,
              "&:hover": {
                background: `linear-gradient(90deg, ${tokens.primary.dark}, ${tokens.secondary.dark})`,
              },
            }}
          >
            Save Changes
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Users;
