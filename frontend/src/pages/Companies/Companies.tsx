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
  CircularProgress,
  Alert,
} from "@mui/material";
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import SearchIcon from "@mui/icons-material/Search";
import companyService from "../../services/api/companyService";
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

const Companies: React.FC = () => {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const rowsPerPage = 5;
  const [companies, setCompanies] = useState<Company[]>([]);
  const [totalCompanies, setTotalCompanies] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [orderBy, setOrderBy] = useState<keyof Company | "">("");
  const [order, setOrder] = useState<"asc" | "desc">("asc");
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editingCompany, setEditingCompany] = useState<Company | null>(null);
  const [companyForm, setCompanyForm] = useState<CreateCompanyRequest>({
    name: "",
    email: "",
    phone: "",
    address: "",
  });
  const [addLoading, setAddLoading] = useState(false);
  const [addError, setAddError] = useState<string | null>(null);

  // Fetch companies from backend
  const fetchCompanies = async () => {
    setLoading(true);
    setError(null);
    try {
      // The backend expects 0-based page, frontend is 1-based
      const resp = await companyService.getCompanies(page - 1, rowsPerPage, search);
      setCompanies(resp.content);
      setTotalCompanies(resp.totalElements);
    } catch (err: any) {
      setError(err?.message || "Failed to load companies");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCompanies();
    // eslint-disable-next-line
  }, [page, search]);

  // Sorting logic (client-side for now)
  function sortComparator(a: Company, b: Company) {
    if (!orderBy) return 0;
    const aValue = a[orderBy] ?? "";
    const bValue = b[orderBy] ?? "";
    if (aValue < bValue) return order === "asc" ? -1 : 1;
    if (aValue > bValue) return order === "asc" ? 1 : -1;
    return 0;
  }

  const sortedCompanies = [...companies].sort(sortComparator);

  // Pagination logic (handled by backend, but keep for UI)
  const pageCount = Math.ceil(totalCompanies / rowsPerPage);

  const handleClose = () => {
    setOpen(false);
    setCompanyForm({ name: "", email: "", phone: "", address: "" });
    setAddError(null);
  };

  const handleAddCompany = async () => {
    setAddLoading(true);
    setAddError(null);
    try {
      await companyService.createCompany(companyForm);
      handleClose();
      fetchCompanies();
    } catch (err: any) {
      setAddError(err?.message || "Failed to add company");
    } finally {
      setAddLoading(false);
    }
  };

  // ... (edit, delete, etc. can be implemented similarly)

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
      <Box mb={2}>
        <TextField
          placeholder="Search companies..."
          value={search}
          onChange={e => { setSearch(e.target.value); setPage(1); }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          size="small"
          sx={{ width: 320 }}
        />
      </Box>

      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight={200}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Alert severity="error">{error}</Alert>
      ) : (
        <StyledCard>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <StyledTableCell>
                    <TableSortLabel
                      active={orderBy === "name"}
                      direction={order}
                      onClick={() => {
                        setOrderBy("name");
                        setOrder(order === "asc" ? "desc" : "asc");
                      }}
                    >
                      Company Name
                    </TableSortLabel>
                  </StyledTableCell>
                  <StyledTableCell>Email</StyledTableCell>
                  <StyledTableCell>Phone</StyledTableCell>
                  <StyledTableCell>Address</StyledTableCell>
                  <StyledTableCell>Status</StyledTableCell>
                  <StyledTableCell>Actions</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {sortedCompanies.map((company) => (
                  <TableRow key={company.id}>
                    <StyledTableCell>
                      <Typography fontWeight={600}>{company.name}</Typography>
                    </StyledTableCell>
                    <StyledTableCell>{company.email}</StyledTableCell>
                    <StyledTableCell>{company.phone}</StyledTableCell>
                    <StyledTableCell>{company.address}</StyledTableCell>
                    <StyledTableCell>
                      <StatusChip
                        label={company.status?.toLowerCase() === "active" ? "active" : "inactive"}
                        status={company.status?.toLowerCase() === "active" ? "active" : "inactive"}
                        size="small"
                      />
                    </StyledTableCell>
                    <StyledTableCell>
                      <IconButton size="small" disabled>
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton size="small" disabled>
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </StyledTableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <Box display="flex" justifyContent="flex-end" p={2}>
            <Pagination
              count={pageCount}
              page={page}
              onChange={(_, value) => setPage(value)}
              color="primary"
            />
          </Box>
        </StyledCard>
      )}

      {/* Add Company Dialog */}
      <Dialog open={open} onClose={handleClose} maxWidth="xs" fullWidth>
        <DialogTitle>Add Company</DialogTitle>
        <DialogContent>
          <TextField
            label="Name"
            value={companyForm.name}
            onChange={e => setCompanyForm(f => ({ ...f, name: e.target.value }))}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Email"
            value={companyForm.email}
            onChange={e => setCompanyForm(f => ({ ...f, email: e.target.value }))}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Phone"
            value={companyForm.phone}
            onChange={e => setCompanyForm(f => ({ ...f, phone: e.target.value }))}
            fullWidth
            margin="normal"
          />
          <TextField
            label="Address"
            value={companyForm.address}
            onChange={e => setCompanyForm(f => ({ ...f, address: e.target.value }))}
            fullWidth
            margin="normal"
          />
          {addError && <Alert severity="error" sx={{ mt: 2 }}>{addError}</Alert>}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} disabled={addLoading}>Cancel</Button>
          <Button
            onClick={handleAddCompany}
            variant="contained"
            disabled={addLoading}
          >
            {addLoading ? <CircularProgress size={20} /> : "Add"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Companies;
