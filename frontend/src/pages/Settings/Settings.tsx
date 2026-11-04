import React from "react";
import {
  Box,
  Typography,
  Switch,
  FormControlLabel,
  Paper,
  Button,
  Grid,
} from "@mui/material";

const Settings: React.FC = () => {
  return (
    <Box width="100%" boxSizing="border-box">
      <Typography variant="h4" fontWeight={600} mb={4}>
        System Settings
      </Typography>
      <Grid container spacing={3}>
        {/* System Preferences */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, mb: 2 }}>
            <Typography variant="h6" mb={2}>
              System Preferences
            </Typography>
            <FormControlLabel
              control={<Switch defaultChecked />}
              label="Enable Notifications"
            />
            <FormControlLabel
              control={<Switch />}
              label="Auto Backup Documents"
            />
            <FormControlLabel
              control={<Switch />}
              label="Show Document Previews"
            />
          </Paper>
        </Grid>
        {/* Retention Policy */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, mb: 2 }}>
            <Typography variant="h6" mb={2}>
              Retention Policy
            </Typography>
            <Typography variant="body2" mb={2}>
              Set how long documents are retained before automatic archival or
              deletion.
            </Typography>
            <Button variant="outlined">Configure Retention Policy</Button>
          </Paper>
        </Grid>
        {/* User Management */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, mb: 2 }}>
            <Typography variant="h6" mb={2}>
              User Management
            </Typography>
            <Typography variant="body2" mb={2}>
              Manage user roles, permissions, and access to the archival system.
            </Typography>
            <Button variant="outlined">Manage Users & Roles</Button>
          </Paper>
        </Grid>
        {/* Security Settings */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3, mb: 2 }}>
            <Typography variant="h6" mb={2}>
              Security Settings
            </Typography>
            <FormControlLabel
              control={<Switch defaultChecked />}
              label="Require Two-Factor Authentication"
            />
            <FormControlLabel
              control={<Switch />}
              label="Enable Audit Logging"
            />
          </Paper>
        </Grid>
        {/* Backup & Restore */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" mb={2}>
              Backup & Restore
            </Typography>
            <Typography variant="body2" mb={2}>
              Download a backup of your documents or restore from a previous
              backup.
            </Typography>
            <Button variant="outlined" sx={{ mr: 2 }}>
              Download Backup
            </Button>
            <Button variant="outlined" color="secondary">
              Restore Backup
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Settings;
