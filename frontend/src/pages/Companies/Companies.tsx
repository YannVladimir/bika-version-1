import React, { useState } from "react";
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
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import SearchIcon from "@mui/icons-material/Search";

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

interface Company {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  status: "active" | "inactive";
  employees: number;
}

const Companies: React.FC = () => {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const rowsPerPage = 5;
  const [companies] = useState<Company[]>([
    {
      id: 1,
      name: "Tech Solutions Inc.",
      email: "contact@techsolutions.com",
      phone: "+1 234 567 890",
      address: "123 Tech Street, Silicon Valley, CA",
      status: "active",
      employees: 150,
    },
    {
      id: 2,
      name: "Global Innovations Ltd.",
      email: "info@globalinnovations.com",
      phone: "+1 987 654 321",
      address: "456 Innovation Ave, New York, NY",
      status: "active",
      employees: 75,
    },
    {
      id: 3,
      name: "Digital Dynamics",
      email: "hello@digitaldynamics.com",
      phone: "+1 555 123 444",
      address: "789 Digital Road, Austin, TX",
      status: "inactive",
      employees: 45,
    },
  ]);
  const [orderBy, setOrderBy] = useState<keyof Company | "">("");
  const [order, setOrder] = useState<"asc" | "desc">("asc");
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editingCompany, setEditingCompany] = useState<Company | null>(null);
  const [companyForm, setCompanyForm] = useState({
    name: "",
    email: "",
    phone: "",
    address: "",
  });

  // Sorting logic
  function sortComparator(a: Company, b: Company) {
    if (!orderBy) return 0;
    const aValue = a[orderBy] ?? "";
    const bValue = b[orderBy] ?? "";
    if (aValue < bValue) return order === "asc" ? -1 : 1;
    if (aValue > bValue) return order === "asc" ? 1 : -1;
    return 0;
  }

  // Filter companies by search
  const filteredCompanies = companies.filter((company) =>
    Object.values(company)
      .join(" ")
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  // Sort filtered companies
  const sortedCompanies = [...filteredCompanies].sort(sortComparator);

  // Pagination logic
  const paginatedCompanies = sortedCompanies.slice(
    (page - 1) * rowsPerPage,
    page * rowsPerPage
  );
  const pageCount = Math.ceil(sortedCompanies.length / rowsPerPage);

  const handleClose = () => {
    setOpen(false);
  };

  const handleEditOpen = (company: Company) => {
    setEditingCompany(company);
    setCompanyForm({
      name: company.name,
      email: company.email,
      phone: company.phone,
      address: company.address,
    });
    setEditDialogOpen(true);
  };

  const handleEditClose = () => {
    setEditDialogOpen(false);
    setEditingCompany(null);
  };

  const handleEditSave = () => {
    if (editingCompany) {
      // TODO: Implement actual company update when API integration is added
      // For now, just close the dialog
      console.log("Company update will be implemented in Phase 2");
    }
    setEditDialogOpen(false);
    setEditingCompany(null);
  };

  return (
    <Box width="100%" boxSizing="border-box">
      <HeaderBox>
        <Typography variant="h4" fontWeight={600}>
          Companies
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpen(true)}
          sx={{
            background: `linear-gradient(90deg, ${tokens.primary.main}, ${tokens.secondary.main})`,
            "&:hover": {
              background: `linear-gradient(90deg, ${tokens.primary.dark}, ${tokens.secondary.dark})`,
            },
          }}
        >
          Add Company
        </Button>
      </HeaderBox>

      {/* Search input */}
      <TextField
        placeholder="Search companies..."
        value={search}
        onChange={(e) => {
          setSearch(e.target.value);
          setPage(1);
        }}
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
                    Company Name
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
                    active={orderBy === "phone"}
                    direction={orderBy === "phone" ? order : "asc"}
                    onClick={() => {
                      if (orderBy === "phone")
                        setOrder(order === "asc" ? "desc" : "asc");
                      else {
                        setOrderBy("phone");
                        setOrder("asc");
                      }
                    }}
                  >
                    Phone
                  </TableSortLabel>
                </StyledTableCell>
                <StyledTableCell>
                  <TableSortLabel
                    active={orderBy === "employees"}
                    direction={orderBy === "employees" ? order : "asc"}
                    onClick={() => {
                      if (orderBy === "employees")
                        setOrder(order === "asc" ? "desc" : "asc");
                      else {
                        setOrderBy("employees");
                        setOrder("asc");
                      }
                    }}
                  >
                    Employees
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
              {paginatedCompanies.map((company) => (
                <TableRow key={company.id}>
                  <StyledTableCell>
                    <Typography variant="body1" fontWeight={500}>
                      {company.name}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {company.address}
                    </Typography>
                  </StyledTableCell>
                  <StyledTableCell>{company.email}</StyledTableCell>
                  <StyledTableCell>{company.phone}</StyledTableCell>
                  <StyledTableCell>{company.employees}</StyledTableCell>
                  <StyledTableCell>
                    <StatusChip
                      label={company.status}
                      status={company.status}
                      size="small"
                    />
                  </StyledTableCell>
                  <StyledTableCell align="right">
                    <IconButton
                      size="small"
                      sx={{ mr: 1 }}
                      onClick={() => handleEditOpen(company)}
                    >
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton size="small" color="error">
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </StyledTableCell>
                </TableRow>
              ))}
              {paginatedCompanies.length === 0 && (
                <TableRow>
                  <StyledTableCell colSpan={6} align="center">
                    No companies found.
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
            onChange={(_, value) => setPage(value)}
            color="primary"
            shape="rounded"
          />
        </Box>
      </StyledCard>

      {/* Add Company Dialog */}
      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>Add New Company</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <TextField fullWidth label="Company Name" margin="dense" />
            <TextField fullWidth label="Email" margin="dense" />
            <TextField fullWidth label="Phone" margin="dense" />
            <TextField
              fullWidth
              label="Address"
              margin="dense"
              multiline
              rows={2}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button
            variant="contained"
            onClick={handleClose}
            sx={{
              background: `linear-gradient(90deg, ${tokens.primary.main}, ${tokens.secondary.main})`,
              "&:hover": {
                background: `linear-gradient(90deg, ${tokens.primary.dark}, ${tokens.secondary.dark})`,
              },
            }}
          >
            Add Company
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Company Dialog */}
      <Dialog
        open={editDialogOpen}
        onClose={handleEditClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Edit Company</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <TextField
              fullWidth
              label="Company Name"
              margin="dense"
              value={companyForm.name}
              onChange={(e) =>
                setCompanyForm((f) => ({ ...f, name: e.target.value }))
              }
            />
            <TextField
              fullWidth
              label="Email"
              margin="dense"
              value={companyForm.email}
              onChange={(e) =>
                setCompanyForm((f) => ({ ...f, email: e.target.value }))
              }
            />
            <TextField
              fullWidth
              label="Phone"
              margin="dense"
              value={companyForm.phone}
              onChange={(e) =>
                setCompanyForm((f) => ({ ...f, phone: e.target.value }))
              }
            />
            <TextField
              fullWidth
              label="Address"
              margin="dense"
              multiline
              rows={2}
              value={companyForm.address}
              onChange={(e) =>
                setCompanyForm((f) => ({ ...f, address: e.target.value }))
              }
            />
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

export default Companies;
