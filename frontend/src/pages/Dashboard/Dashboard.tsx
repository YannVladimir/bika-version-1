import React from "react";
import {
  Box,
  Card,
  CardContent,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
  styled,
  Grid,
  Chip,
} from "@mui/material";
import {
  Description as DocumentIcon,
  CloudUpload as UploadIcon,
  Storage as StorageIcon,
  Group as UsersIcon,
  Security as SecurityIcon,
  Business as CompanyIcon,
  Assessment as ReportsIcon,
  Settings as SettingsIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { usePermissions } from "../../hooks/usePermissions";
import UserRoleDisplay from "../../components/UserRoleDisplay";
import RoleSwitcher from "../../components/RoleSwitcher";
import RoleTest from "../../components/RoleTest";

const StatsCard = styled(Card)(({ theme }) => ({
  height: "100%",
  background: theme.palette.background.paper,
  borderRadius: 16,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
  transition: "transform 0.2s ease-in-out",
  "&:hover": {
    transform: "translateY(-4px)",
  },
}));

const StatValue = styled(Typography)({
  fontSize: "2rem",
  fontWeight: 600,
  marginBottom: 8,
});

const ActivityCard = styled(Card)(({ theme }) => ({
  height: "100%",
  background: theme.palette.background.paper,
  borderRadius: 16,
  boxShadow: "0 4px 12px rgba(0, 0, 0, 0.05)",
}));

const GradientBox = styled(Box)({
  background: `linear-gradient(135deg, ${tokens.primary.main}, ${tokens.secondary.main})`,
  borderRadius: "50%",
  padding: 8,
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  color: "white",
});

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const permissions = usePermissions();

  const stats = [
    {
      title: "Total Documents",
      value: "1,234",
      icon: <DocumentIcon />,
    },
    {
      title: "Storage Used",
      value: "45.2 GB",
      icon: <StorageIcon />,
    },
    {
      title: "Active Users",
      value: "56",
      icon: <UsersIcon />,
    },
    {
      title: "Uploads Today",
      value: "28",
      icon: <UploadIcon />,
    },
  ];

  const recentActivities = [
    {
      user: "John Doe",
      action: "uploaded",
      document: "Q4 Financial Report.pdf",
      time: "5 minutes ago",
    },
    {
      user: "Sarah Smith",
      action: "modified",
      document: "Project Proposal.docx",
      time: "2 hours ago",
    },
    {
      user: "Mike Johnson",
      action: "archived",
      document: "Client Contract.pdf",
      time: "4 hours ago",
    },
    {
      user: "Emily Brown",
      action: "shared",
      document: "Marketing Plan.pptx",
      time: "6 hours ago",
    },
  ];

  const userPermissions = [
    {
      name: "Manage Companies",
      description: "Add, edit, and delete client companies",
      hasPermission: permissions.canManageCompanies(),
      icon: <CompanyIcon />,
    },
    {
      name: "Manage Users",
      description: "Add, edit, and delete users within your scope",
      hasPermission: permissions.canManageUsers(),
      icon: <UsersIcon />,
    },
    {
      name: "Document Types",
      description: "Define document types and metadata",
      hasPermission: permissions.canManageDocumentTypes(),
      icon: <DocumentIcon />,
    },
    {
      name: "Archival & Retrieval",
      description: "Archive and retrieve documents",
      hasPermission: permissions.canAccessArchival(),
      icon: <StorageIcon />,
    },
    {
      name: "Reports",
      description: "Generate and view system reports",
      hasPermission: permissions.canAccessReports(),
      icon: <ReportsIcon />,
    },
    {
      name: "Settings",
      description: "Access system configuration",
      hasPermission: permissions.canAccessSettings(),
      icon: <SettingsIcon />,
    },
  ];

  return (
    <Box
      width="100%"
      boxSizing="border-box"
      display="flex"
      flexDirection="column"
      alignItems="flex-start"
    >
      <Box display="flex" justifyContent="space-between" alignItems="center" width="100%" mb={4}>
        <Typography variant="h4" fontWeight={600}>
          Dashboard
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <UserRoleDisplay variant="detailed" showDescription={true} />
          <RoleSwitcher />
        </Box>
      </Box>

      {/* Temporary Role Test Component for Debugging */}
      <RoleTest />

      <Grid container spacing={3} mb={3}>
        {stats.map((stat) => (
          <Grid item xs={12} sm={6} md={3} key={stat.title}>
            <StatsCard>
              <CardContent>
                <Box display="flex" alignItems="center" mb={2}>
                  <GradientBox>{stat.icon}</GradientBox>
                </Box>
                <StatValue>{stat.value}</StatValue>
                <Typography color="text.secondary">{stat.title}</Typography>
              </CardContent>
            </StatsCard>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} lg={6}>
          <ActivityCard>
            <CardContent>
              <Typography variant="h6" fontWeight={500} mb={2}>
                Recent Activities
              </Typography>
              <List>
                {recentActivities.map((activity, index) => (
                  <ListItem
                    key={index}
                    sx={{
                      borderBottom:
                        index < recentActivities.length - 1
                          ? "1px solid rgba(0, 0, 0, 0.08)"
                          : "none",
                      py: 2,
                    }}
                  >
                    <ListItemIcon>
                      <Avatar
                        sx={{
                          width: 40,
                          height: 40,
                          bgcolor: `${tokens.primary.main}`,
                        }}
                      >
                        {activity.user.charAt(0)}
                      </Avatar>
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Typography variant="body1">
                          <strong>{activity.user}</strong> {activity.action}{" "}
                          <strong>{activity.document}</strong>
                        </Typography>
                      }
                      secondary={activity.time}
                    />
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </ActivityCard>
        </Grid>

        <Grid item xs={12} lg={6}>
          <ActivityCard>
            <CardContent>
              <Typography variant="h6" fontWeight={500} mb={2}>
                Your Permissions
              </Typography>
              <List>
                {userPermissions.map((permission, index) => (
                  <ListItem
                    key={index}
                    sx={{
                      borderBottom:
                        index < userPermissions.length - 1
                          ? "1px solid rgba(0, 0, 0, 0.08)"
                          : "none",
                      py: 2,
                    }}
                  >
                    <ListItemIcon>
                      <Avatar
                        sx={{
                          width: 40,
                          height: 40,
                          bgcolor: permission.hasPermission 
                            ? tokens.primary.main 
                            : 'grey.300',
                        }}
                      >
                        {permission.icon}
                      </Avatar>
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Box display="flex" alignItems="center" gap={1}>
                          <Typography variant="body1" fontWeight={500}>
                            {permission.name}
                          </Typography>
                          <Chip
                            label={permission.hasPermission ? "Allowed" : "Denied"}
                            size="small"
                            color={permission.hasPermission ? "success" : "default"}
                            variant={permission.hasPermission ? "filled" : "outlined"}
                          />
                        </Box>
                      }
                      secondary={permission.description}
                    />
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </ActivityCard>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
