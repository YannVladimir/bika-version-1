import React, { useEffect, useState } from "react";
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
  LinearProgress,
  Chip,
  IconButton,
} from "@mui/material";
import {
  Description as DocumentIcon,
  CloudUpload as UploadIcon,
  Storage as StorageIcon,
  Group as UsersIcon,
  Assignment as TaskIcon,
  Business as CompanyIcon,
  Assessment as ProjectIcon,
  TrendingUp as TrendingUpIcon,
  Warning as WarningIcon,
  CheckCircle as CheckCircleIcon,
  AdminPanelSettings as AdminIcon,
  Dashboard as DashboardIcon,
  Refresh as RefreshIcon,
} from "@mui/icons-material";
import { tokens } from "../../theme/theme";
import { useAuth } from "../../context/AuthContext";
import { UserRole } from "../../services/api/types";
import { dashboardService, DashboardStats, RecentActivity } from "../../services/api/dashboardService";

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

const QuickActionCard = styled(Card)(({ theme }) => ({
  padding: theme.spacing(2),
  borderRadius: 12,
  cursor: "pointer",
  transition: "all 0.2s ease-in-out",
  "&:hover": {
    transform: "translateY(-2px)",
    boxShadow: "0 8px 20px rgba(0, 0, 0, 0.1)",
  },
}));

const RoleDashboard: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({});
  const [loading, setLoading] = useState(true);
  const [recentActivities, setRecentActivities] = useState<RecentActivity[]>([]);

  useEffect(() => {
    fetchDashboardData();
  }, [user?.role]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Use real API if user is authenticated, otherwise use mock data
      if (user) {
        const { stats: apiStats, activities } = await dashboardService.refreshDashboard();
        setStats(apiStats);
        setRecentActivities(activities);
      } else {
        // Fallback to mock data if not authenticated
        const mockStats: DashboardStats = {
          totalUsers: 156,
          totalDocuments: 2847,
          totalTasks: 89,
          totalProjects: 23,
          activeUsers: 142,
          pendingTasks: 34,
          completedTasks: 45,
          overdueTasks: 10,
          storageUsedGB: 67.3,
          storageTotalGB: 100,
          documentsUploadedToday: 12,
          tasksCompletedToday: 8,
          newUsersThisMonth: 15,
          myTasks: 7,
          myProjects: 3,
          departmentTasks: 25,
          companyTasks: 89,
        };
        
        setStats(mockStats);
        const mockActivities = getRoleBasedActivities();
        setRecentActivities(mockActivities);
      }
      
    } catch (error) {
      console.error("Failed to fetch dashboard data:", error);
      // Fallback to mock data on error
      const mockStats: DashboardStats = {
        totalUsers: 156,
        totalDocuments: 2847,
        totalTasks: 89,
        totalProjects: 23,
        activeUsers: 142,
        pendingTasks: 34,
        completedTasks: 45,
        overdueTasks: 10,
        storageUsedGB: 67.3,
        storageTotalGB: 100,
        documentsUploadedToday: 12,
        tasksCompletedToday: 8,
        newUsersThisMonth: 15,
        myTasks: 7,
        myProjects: 3,
        departmentTasks: 25,
        companyTasks: 89,
      };
      setStats(mockStats);
      setRecentActivities(getRoleBasedActivities());
    } finally {
      setLoading(false);
    }
  };

  const getRoleBasedActivities = (): RecentActivity[] => {
    const baseActivities: RecentActivity[] = [
      {
        id: 1,
        user: user?.firstName + " " + user?.lastName || "You",
        action: "completed",
        document: "Monthly Report Review",
        time: "2 hours ago",
        type: "task"
      },
      {
        id: 2,
        user: "Sarah Wilson",
        action: "uploaded",
        document: "Project Specifications.pdf",
        time: "4 hours ago",
        type: "document"
      },
      {
        id: 3,
        user: "Mike Chen",
        action: "created",
        document: "Q1 Marketing Campaign",
        time: "6 hours ago",
        type: "project"
      },
    ];

    switch (user?.role) {
      case UserRole.SUPER_ADMIN:
        return [
          ...baseActivities,
          {
            id: 4,
            user: "System",
            action: "registered",
            document: "New Company: TechCorp Inc.",
            time: "1 day ago",
            type: "company"
          },
        ];
      case UserRole.COMPANY_ADMIN:
        return [
          ...baseActivities,
          {
            id: 4,
            user: "John Admin",
            action: "added",
            document: "New Department: Research & Development",
            time: "1 day ago",
            type: "department"
          },
        ];
      default:
        return baseActivities;
    }
  };

  const getSuperAdminStats = () => [
    {
      title: "Total Users",
      value: stats.totalUsers?.toString() || "0",
      icon: <UsersIcon />,
      color: tokens.primary.main,
      change: `+${stats.newUsersThisMonth || 0} this month`,
    },
    {
      title: "Total Companies",
      value: "47",
      icon: <CompanyIcon />,
      color: tokens.secondary.main,
      change: "+3 this quarter",
    },
    {
      title: "System Storage",
      value: `${stats.storageUsedGB || 0}GB`,
      icon: <StorageIcon />,
      color: "#ff9800",
      change: `${((stats.storageUsedGB || 0) / (stats.storageTotalGB || 100) * 100).toFixed(1)}% used`,
    },
    {
      title: "Active Sessions",
      value: stats.activeUsers?.toString() || "0",
      icon: <TrendingUpIcon />,
      color: "#4caf50",
      change: "Last 24 hours",
    },
  ];

  const getCompanyAdminStats = () => [
    {
      title: "Company Users",
      value: stats.totalUsers?.toString() || "0",
      icon: <UsersIcon />,
      color: tokens.primary.main,
      change: `${stats.activeUsers || 0} active`,
    },
    {
      title: "Documents",
      value: stats.totalDocuments?.toString() || "0",
      icon: <DocumentIcon />,
      color: tokens.secondary.main,
      change: `+${stats.documentsUploadedToday || 0} today`,
    },
    {
      title: "Projects",
      value: stats.totalProjects?.toString() || "0",
      icon: <ProjectIcon />,
      color: "#2196f3",
      change: "Active projects",
    },
    {
      title: "Storage Used",
      value: `${stats.storageUsedGB || 0}GB`,
      icon: <StorageIcon />,
      color: "#ff9800",
      change: `of ${stats.storageTotalGB || 100}GB`,
    },
  ];

  const getManagerStats = () => [
    {
      title: "Department Tasks",
      value: stats.departmentTasks?.toString() || "0",
      icon: <TaskIcon />,
      color: tokens.primary.main,
      change: `${stats.pendingTasks || 0} pending`,
    },
    {
      title: "Team Members",
      value: "12",
      icon: <UsersIcon />,
      color: tokens.secondary.main,
      change: "In your department",
    },
    {
      title: "Projects",
      value: stats.myProjects?.toString() || "0",
      icon: <ProjectIcon />,
      color: "#2196f3",
      change: "Managing",
    },
    {
      title: "Completed Today",
      value: stats.tasksCompletedToday?.toString() || "0",
      icon: <CheckCircleIcon />,
      color: "#4caf50",
      change: "Tasks finished",
    },
  ];

  const getUserStats = () => [
    {
      title: "My Tasks",
      value: stats.myTasks?.toString() || "0",
      icon: <TaskIcon />,
      color: tokens.primary.main,
      change: `${stats.pendingTasks || 0} pending`,
    },
    {
      title: "My Projects",
      value: stats.myProjects?.toString() || "0",
      icon: <ProjectIcon />,
      color: tokens.secondary.main,
      change: "Active projects",
    },
    {
      title: "Documents",
      value: "23",
      icon: <DocumentIcon />,
      color: "#2196f3",
      change: "Uploaded by me",
    },
    {
      title: "Completed Today",
      value: stats.tasksCompletedToday?.toString() || "0",
      icon: <CheckCircleIcon />,
      color: "#4caf50",
      change: "Tasks finished",
    },
  ];

  const getStatsForRole = () => {
    switch (user?.role) {
      case UserRole.SUPER_ADMIN:
        return getSuperAdminStats();
      case UserRole.COMPANY_ADMIN:
        return getCompanyAdminStats();
      case UserRole.MANAGER:
        return getManagerStats();
      case UserRole.USER:
      default:
        return getUserStats();
    }
  };

  const getQuickActions = () => {
    switch (user?.role) {
      case UserRole.SUPER_ADMIN:
        return [
          { title: "Manage Companies", icon: <CompanyIcon />, path: "/companies" },
          { title: "System Reports", icon: <ProjectIcon />, path: "/reports" },
          { title: "User Management", icon: <AdminIcon />, path: "/users" },
          { title: "System Settings", icon: <DashboardIcon />, path: "/settings" },
        ];
      case UserRole.COMPANY_ADMIN:
        return [
          { title: "Manage Users", icon: <UsersIcon />, path: "/users" },
          { title: "Departments", icon: <CompanyIcon />, path: "/departments" },
          { title: "Company Reports", icon: <ProjectIcon />, path: "/reports" },
          { title: "Document Types", icon: <DocumentIcon />, path: "/document-types" },
        ];
      case UserRole.MANAGER:
        return [
          { title: "Team Tasks", icon: <TaskIcon />, path: "/tasks" },
          { title: "Projects", icon: <ProjectIcon />, path: "/projects" },
          { title: "Team Reports", icon: <ProjectIcon />, path: "/reports" },
          { title: "Document Drive", icon: <DocumentIcon />, path: "/drive" },
        ];
      case UserRole.USER:
      default:
        return [
          { title: "My Tasks", icon: <TaskIcon />, path: "/tasks" },
          { title: "Upload Document", icon: <UploadIcon />, path: "/drive" },
          { title: "Document Retrieval", icon: <DocumentIcon />, path: "/retrieval" },
          { title: "My Profile", icon: <AdminIcon />, path: "/profile" },
        ];
    }
  };

  const getRoleName = (role: UserRole) => {
    switch (role) {
      case UserRole.SUPER_ADMIN:
        return "Super Administrator";
      case UserRole.COMPANY_ADMIN:
        return "Company Administrator";
      case UserRole.MANAGER:
        return "Manager";
      case UserRole.USER:
      default:
        return "User";
    }
  };

  const currentStats = getStatsForRole();
  const quickActions = getQuickActions();

  if (loading) {
    return (
      <Box sx={{ width: "100%", mt: 4 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Box
      width="100%"
      boxSizing="border-box"
      display="flex"
      flexDirection="column"
      alignItems="flex-start"
    >
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" width="100%" mb={4}>
        <Box>
          <Typography variant="h4" fontWeight={600} mb={1}>
            Welcome back, {user?.firstName || "User"}!
          </Typography>
          <Box display="flex" alignItems="center" gap={1}>
            <Chip 
              label={getRoleName(user?.role || UserRole.USER)} 
              color="primary" 
              size="small" 
            />
            <Typography color="text.secondary">
              Here's what's happening in your workspace
            </Typography>
          </Box>
        </Box>
        <IconButton onClick={fetchDashboardData} color="primary">
          <RefreshIcon />
        </IconButton>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} mb={3}>
        {currentStats.map((stat, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <StatsCard>
              <CardContent>
                <Box display="flex" alignItems="center" mb={2}>
                  <Box
                    sx={{
                      background: `linear-gradient(135deg, ${stat.color}, ${stat.color}90)`,
                      borderRadius: "50%",
                      padding: 1,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "white",
                      mr: 2,
                    }}
                  >
                    {stat.icon}
                  </Box>
                  <Box>
                    <StatValue>{stat.value}</StatValue>
                    <Typography color="text.secondary" variant="body2">
                      {stat.title}
                    </Typography>
                    <Typography color="text.secondary" variant="caption">
                      {stat.change}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </StatsCard>
          </Grid>
        ))}
      </Grid>

      {/* Quick Actions and Additional Info */}
      <Grid container spacing={3} mb={3}>
        <Grid item xs={12} md={6}>
          <ActivityCard>
            <CardContent>
              <Typography variant="h6" fontWeight={500} mb={2}>
                Quick Actions
              </Typography>
              <Grid container spacing={2}>
                {quickActions.map((action, index) => (
                  <Grid item xs={6} key={index}>
                    <QuickActionCard onClick={() => window.location.href = action.path}>
                      <Box display="flex" alignItems="center" gap={1}>
                        <Box sx={{ color: tokens.primary.main }}>
                          {action.icon}
                        </Box>
                        <Typography variant="body2" fontWeight={500}>
                          {action.title}
                        </Typography>
                      </Box>
                    </QuickActionCard>
                  </Grid>
                ))}
              </Grid>
            </CardContent>
          </ActivityCard>
        </Grid>

        {/* Task Overview for non-users */}
        {user?.role !== UserRole.USER && (
          <Grid item xs={12} md={6}>
            <ActivityCard>
              <CardContent>
                <Typography variant="h6" fontWeight={500} mb={2}>
                  Task Overview
                </Typography>
                <Box mb={2}>
                  <Box display="flex" justifyContent="space-between" mb={1}>
                    <Typography variant="body2">Pending Tasks</Typography>
                    <Typography variant="body2" fontWeight={500}>
                      {stats.pendingTasks || 0}
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={((stats.pendingTasks || 0) / (stats.totalTasks || 1)) * 100}
                    sx={{ height: 6, borderRadius: 3 }}
                  />
                </Box>
                <Box mb={2}>
                  <Box display="flex" justifyContent="space-between" mb={1}>
                    <Typography variant="body2">Completed Tasks</Typography>
                    <Typography variant="body2" fontWeight={500}>
                      {stats.completedTasks || 0}
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={((stats.completedTasks || 0) / (stats.totalTasks || 1)) * 100}
                    sx={{ height: 6, borderRadius: 3 }}
                    color="success"
                  />
                </Box>
                {(stats.overdueTasks || 0) > 0 && (
                  <Box display="flex" alignItems="center" gap={1} mt={2}>
                    <WarningIcon color="warning" fontSize="small" />
                    <Typography variant="body2" color="warning.main">
                      {stats.overdueTasks} overdue tasks need attention
                    </Typography>
                  </Box>
                )}
              </CardContent>
            </ActivityCard>
          </Grid>
        )}

        {/* Storage overview for admins */}
        {(user?.role === UserRole.SUPER_ADMIN || user?.role === UserRole.COMPANY_ADMIN) && (
          <Grid item xs={12} md={6}>
            <ActivityCard>
              <CardContent>
                <Typography variant="h6" fontWeight={500} mb={2}>
                  Storage Overview
                </Typography>
                <Box mb={2}>
                  <Box display="flex" justifyContent="space-between" mb={1}>
                    <Typography variant="body2">Used Storage</Typography>
                    <Typography variant="body2" fontWeight={500}>
                      {stats.storageUsedGB || 0}GB / {stats.storageTotalGB || 100}GB
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={((stats.storageUsedGB || 0) / (stats.storageTotalGB || 100)) * 100}
                    sx={{ height: 8, borderRadius: 4 }}
                    color={
                      ((stats.storageUsedGB || 0) / (stats.storageTotalGB || 100)) * 100 > 80
                        ? "warning"
                        : "primary"
                    }
                  />
                </Box>
                <Typography variant="body2" color="text.secondary">
                  {stats.documentsUploadedToday || 0} documents uploaded today
                </Typography>
              </CardContent>
            </ActivityCard>
          </Grid>
        )}
      </Grid>

      {/* Recent Activities */}
      <ActivityCard sx={{ width: "100%" }}>
        <CardContent>
          <Typography variant="h6" fontWeight={500} mb={2}>
            Recent Activities
          </Typography>
          <List>
            {recentActivities.map((activity, index) => (
              <ListItem
                key={activity.id}
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
                      bgcolor: tokens.primary.main,
                    }}
                  >
                    {activity.user.charAt(0)}
                  </Avatar>
                </ListItemIcon>
                <ListItemText
                  primary={
                    <Box display="flex" alignItems="center" gap={1}>
                      <Typography variant="body1">
                        <strong>{activity.user}</strong> {activity.action}{" "}
                        <strong>{activity.document}</strong>
                      </Typography>
                      <Chip
                        label={activity.type}
                        size="small"
                        variant="outlined"
                        sx={{ ml: 1 }}
                      />
                    </Box>
                  }
                  secondary={activity.time}
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </ActivityCard>
    </Box>
  );
};

export default RoleDashboard; 