import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  Grid,
  TextField,
  Button,
  Avatar,
  IconButton,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
} from "@mui/material";
import {
  Edit as EditIcon,
  Save as SaveIcon,
  Cancel as CancelIcon,
  PhotoCamera as PhotoCameraIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useTheme } from "@mui/material/styles";
import { InputChangeEvent } from "../../types/events";
import { useAuth } from "../../context/AuthContext";
import { userService, User, ChangePasswordRequest } from "../../services/api";

interface ProfileProps {}

const Profile: React.FC<ProfileProps> = () => {
  const theme = useTheme();
  const mode = theme.palette.mode;
  const { user: authUser } = useAuth();
  
  // State management
  const [userData, setUserData] = useState<User | null>(null);
  const [editedData, setEditedData] = useState<Partial<User>>({});
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [openPasswordDialog, setOpenPasswordDialog] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Load user profile on component mount
  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    if (!authUser) return;
    
    try {
      setLoading(true);
      setError(null);
      
      const profileData = await userService.getUserProfile();
      setUserData(profileData);
      setEditedData({
        firstName: profileData.firstName,
        lastName: profileData.lastName,
        email: profileData.email,
      });
    } catch (err) {
      console.error('Error loading user profile:', err);
      setError('Failed to load user profile');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
    setError(null);
    setSuccess(null);
  };

  const handleSave = async () => {
    if (!userData) return;
    
    try {
      setSaving(true);
      setError(null);
      
      const updatedUser = await userService.updateUserProfile(editedData);
      setUserData(updatedUser);
      setIsEditing(false);
      setSuccess("Profile updated successfully!");
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Error updating profile:', err);
      setError('Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (userData) {
      setEditedData({
        firstName: userData.firstName,
        lastName: userData.lastName,
        email: userData.email,
      });
    }
    setIsEditing(false);
    setError(null);
  };

  const handlePasswordChange = async () => {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setError("New passwords do not match");
      return;
    }
    
    const changePasswordRequest: ChangePasswordRequest = {
      currentPassword: passwordData.currentPassword,
      newPassword: passwordData.newPassword,
    };

    try {
      setSaving(true);
      setError(null);
      
      await userService.changePassword(changePasswordRequest);
      setOpenPasswordDialog(false);
      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setSuccess("Password changed successfully!");
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Error changing password:', err);
      setError('Failed to change password');
    } finally {
      setSaving(false);
    }
  };

  const handleInputChange = (field: keyof User) => (e: InputChangeEvent) => {
    setEditedData(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  const handlePasswordInputChange = (field: keyof typeof passwordData) => (e: InputChangeEvent) => {
    setPasswordData(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  const getInitials = (firstName?: string, lastName?: string) => {
    if (!firstName && !lastName) return 'U';
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`.toUpperCase();
  };

  const getRoleDisplayName = (role?: string) => {
    switch (role) {
      case 'SUPER_ADMIN': return 'Super Administrator';
      case 'COMPANY_ADMIN': return 'Company Administrator';
      case 'MANAGER': return 'Manager';
      case 'USER': return 'User';
      default: return role || 'Unknown';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (!userData) {
    return (
      <Box p={3}>
        <Alert severity="error">
          Failed to load user profile. Please try refreshing the page.
        </Alert>
      </Box>
    );
  }

  return (
    <Box width="100%" boxSizing="border-box" p={3}>
      <Typography
        variant="h4"
        fontWeight={600}
        mb={4}
        color={mode === "dark" ? tokens.grey[100] : tokens.grey[900]}
      >
        Profile
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert
          severity="success"
          sx={{ mb: 3 }}
          onClose={() => setSuccess(null)}
        >
          {success}
        </Alert>
      )}

      <Grid container spacing={3}>
        {/* Profile Picture Section */}
        <Grid item xs={12} md={4}>
          <Paper
            elevation={mode === "dark" ? 2 : 0}
            sx={{
              p: 3,
              backgroundColor:
                mode === "dark" ? tokens.grey[800] : tokens.grey[100],
              borderRadius: 2,
              textAlign: "center",
              color: mode === "dark" ? tokens.grey[100] : tokens.grey[900],
            }}
          >
            <Box position="relative" display="inline-block">
              <Avatar
                src={undefined} // Remove avatar property reference since it doesn't exist on User type
                sx={{
                  width: 150,
                  height: 150,
                  mb: 2,
                  bgcolor: tokens.primary.main,
                  color: "#fff",
                  border:
                    mode === "dark"
                      ? `2px solid ${tokens.primary.light}`
                      : undefined,
                }}
              >
                {getInitials(userData.firstName, userData.lastName)}
              </Avatar>
              <IconButton
                sx={{
                  position: "absolute",
                  bottom: 20,
                  right: 0,
                  bgcolor:
                    mode === "dark" ? tokens.grey[700] : tokens.grey[200],
                  color: mode === "dark" ? tokens.grey[100] : tokens.grey[900],
                  "&:hover": {
                    bgcolor:
                      mode === "dark" ? tokens.grey[600] : tokens.grey[300],
                  },
                }}
              >
                <PhotoCameraIcon />
              </IconButton>
            </Box>
            <Typography
              variant="h6"
              gutterBottom
              color={mode === "dark" ? tokens.grey[100] : tokens.grey[900]}
            >
              {userData.firstName} {userData.lastName}
            </Typography>
            <Typography
              variant="body2"
              color={mode === "dark" ? tokens.grey[300] : "text.secondary"}
            >
              {getRoleDisplayName(userData.role)}
            </Typography>
          </Paper>
        </Grid>

        {/* Profile Details Section */}
        <Grid item xs={12} md={8}>
          <Paper
            elevation={mode === "dark" ? 2 : 0}
            sx={{
              p: 3,
              backgroundColor:
                mode === "dark" ? tokens.grey[800] : tokens.grey[100],
              borderRadius: 2,
              color: mode === "dark" ? tokens.grey[100] : tokens.grey[900],
            }}
          >
            <Box
              display="flex"
              justifyContent="space-between"
              alignItems="center"
              mb={3}
            >
              <Typography
                variant="h6"
                color={mode === "dark" ? tokens.grey[100] : tokens.grey[900]}
              >
                Personal Information
              </Typography>
              {!isEditing ? (
                <Button
                  startIcon={<EditIcon />}
                  onClick={handleEdit}
                  variant="outlined"
                  color={mode === "dark" ? "secondary" : "primary"}
                  disabled={saving}
                >
                  {saving ? <CircularProgress size={24} color="inherit" /> : "Edit Profile"}
                </Button>
              ) : (
                <Box>
                  <IconButton
                    onClick={handleSave}
                    color="primary"
                    sx={{ mr: 1 }}
                    disabled={saving}
                  >
                    {saving ? <CircularProgress size={24} color="inherit" /> : <SaveIcon />}
                  </IconButton>
                  <IconButton onClick={handleCancel} color="error" disabled={saving}>
                    {saving ? <CircularProgress size={24} color="inherit" /> : <CancelIcon />}
                  </IconButton>
                </Box>
              )}
            </Box>

            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="First Name"
                  value={editedData.firstName || ""}
                  onChange={handleInputChange("firstName")}
                  fullWidth
                  disabled={!isEditing || saving}
                  InputLabelProps={{
                    style: {
                      color: mode === "dark" ? tokens.grey[300] : undefined,
                    },
                  }}
                  InputProps={{
                    style: {
                      color: mode === "dark" ? tokens.grey[100] : undefined,
                    },
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="Last Name"
                  value={editedData.lastName || ""}
                  onChange={handleInputChange("lastName")}
                  fullWidth
                  disabled={!isEditing || saving}
                  InputLabelProps={{
                    style: {
                      color: mode === "dark" ? tokens.grey[300] : undefined,
                    },
                  }}
                  InputProps={{
                    style: {
                      color: mode === "dark" ? tokens.grey[100] : undefined,
                    },
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  label="Email"
                  value={editedData.email || ""}
                  onChange={handleInputChange("email")}
                  fullWidth
                  disabled={!isEditing || saving}
                  InputLabelProps={{
                    style: {
                      color: mode === "dark" ? tokens.grey[300] : undefined,
                    },
                  }}
                  InputProps={{
                    style: {
                      color: mode === "dark" ? tokens.grey[100] : undefined,
                    },
                  }}
                />
              </Grid>
            </Grid>

            <Divider
              sx={{
                my: 3,
                borderColor: mode === "dark" ? tokens.grey[700] : undefined,
              }}
            />

            <Box>
              <Typography
                variant="h6"
                mb={2}
                color={mode === "dark" ? tokens.grey[100] : tokens.grey[900]}
              >
                Security
              </Typography>
              <Button
                variant="outlined"
                onClick={() => setOpenPasswordDialog(true)}
                color={mode === "dark" ? "secondary" : "primary"}
                disabled={saving}
              >
                {saving ? <CircularProgress size={24} color="inherit" /> : "Change Password"}
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      {/* Change Password Dialog */}
      <Dialog
        open={openPasswordDialog}
        onClose={() => setOpenPasswordDialog(false)}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: {
            backgroundColor: mode === "dark" ? tokens.grey[900] : "#fff",
            color: mode === "dark" ? tokens.grey[100] : tokens.grey[900],
          },
        }}
      >
        <DialogTitle
          sx={{ color: mode === "dark" ? tokens.grey[100] : tokens.grey[900] }}
        >
          Change Password
        </DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <TextField
              label="Current Password"
              type="password"
              fullWidth
              margin="normal"
              value={passwordData.currentPassword}
              onChange={handlePasswordInputChange("currentPassword")}
              InputLabelProps={{
                style: {
                  color: mode === "dark" ? tokens.grey[300] : undefined,
                },
              }}
              InputProps={{
                style: {
                  color: mode === "dark" ? tokens.grey[100] : undefined,
                },
              }}
            />
            <TextField
              label="New Password"
              type="password"
              fullWidth
              margin="normal"
              value={passwordData.newPassword}
              onChange={handlePasswordInputChange("newPassword")}
              InputLabelProps={{
                style: {
                  color: mode === "dark" ? tokens.grey[300] : undefined,
                },
              }}
              InputProps={{
                style: {
                  color: mode === "dark" ? tokens.grey[100] : undefined,
                },
              }}
            />
            <TextField
              label="Confirm New Password"
              type="password"
              fullWidth
              margin="normal"
              value={passwordData.confirmPassword}
              onChange={handlePasswordInputChange("confirmPassword")}
              InputLabelProps={{
                style: {
                  color: mode === "dark" ? tokens.grey[300] : undefined,
                },
              }}
              InputProps={{
                style: {
                  color: mode === "dark" ? tokens.grey[100] : undefined,
                },
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenPasswordDialog(false)} color="inherit" disabled={saving}>
            Cancel
          </Button>
          <Button
            onClick={handlePasswordChange}
            variant="contained"
            color={mode === "dark" ? "secondary" : "primary"}
            disabled={saving}
          >
            {saving ? <CircularProgress size={24} color="inherit" /> : "Change Password"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Profile;
