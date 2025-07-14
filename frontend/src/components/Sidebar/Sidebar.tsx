import React from "react";
import { styled } from "@mui/material/styles";
import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Box,
  Typography,
  Tooltip,
} from "@mui/material";
import {
  Dashboard as DashboardIcon,
  Business as CompanyIcon,
  People as UsersIcon,
  Description as DocumentIcon,
  Archive as ArchiveIcon,
  Assessment as ReportsIcon,
  Storage as DriveIcon,
  Settings as SettingsIcon,
  Unarchive as RetrievalIcon,
} from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { tokens } from "../../theme/theme";
import { getMenuItemsForRole, MenuItem, UserRole } from "../../utils/roleUtils";

const DRAWER_WIDTH = 280;

const StyledDrawer = styled(Drawer)(({ theme }) => ({
  width: DRAWER_WIDTH,
  flexShrink: 0,
  "& .MuiDrawer-paper": {
    width: DRAWER_WIDTH,
    boxSizing: "border-box",
    backgroundColor: theme.palette.background.paper,
    color: theme.palette.text.primary,
    borderRight: `1px solid ${theme.palette.divider}`,
  },
}));

const StyledListItem = styled(ListItem)<{ active?: number }>(({ theme, active }) => ({
  margin: "8px 16px",
  borderRadius: "12px",
  background: active ? 
    `linear-gradient(135deg, ${tokens.primary.main} 0%, ${tokens.primary.light} 50%, ${tokens.secondary.main} 100%)` : 
    "transparent",
  color: active ? theme.palette.common.white : theme.palette.text.primary,
  "&:hover": {
    background: `linear-gradient(135deg, ${tokens.primary.main} 0%, ${tokens.primary.light} 50%, ${tokens.secondary.main} 100%)`,
    color: theme.palette.common.white,
    "& .MuiListItemIcon-root": {
      color: theme.palette.common.white,
    },
  },
  cursor: "pointer",
  "& .MuiListItemIcon-root": {
    color: active ? theme.palette.common.white : theme.palette.text.primary,
  },
}));

const Logo = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  padding: "24px 16px",
  borderBottom: `1px solid ${theme.palette.divider}`,
  "& img": {
    width: "120px",
    height: "auto",
  },
}));

// Define all possible menu items with their role permissions
const allMenuItems: MenuItem[] = [
  { 
    text: "Dashboard", 
    icon: <DashboardIcon />, 
    path: "/dashboard", 
    roles: ["SUPER_ADMIN", "COMPANY_ADMIN", "MANAGER", "USER"] 
  },
  { 
    text: "Companies", 
    icon: <CompanyIcon />, 
    path: "/companies", 
    roles: ["SUPER_ADMIN"] 
  },
  { 
    text: "Users & Roles", 
    icon: <UsersIcon />, 
    path: "/users", 
    roles: ["SUPER_ADMIN", "COMPANY_ADMIN"] 
  },
  { 
    text: "Document Types", 
    icon: <DocumentIcon />, 
    path: "/document-types", 
    roles: ["COMPANY_ADMIN", "MANAGER", "USER"] 
  },
  { 
    text: "Archival", 
    icon: <ArchiveIcon />, 
    path: "/archival", 
    roles: ["COMPANY_ADMIN", "MANAGER", "USER"] 
  },
  { 
    text: "Retrieval", 
    icon: <RetrievalIcon />, 
    path: "/retrieval", 
    roles: ["COMPANY_ADMIN", "MANAGER", "USER"] 
  },
  { 
    text: "Reports", 
    icon: <ReportsIcon />, 
    path: "/reports", 
    roles: ["MANAGER"] 
  },
  { 
    text: "Drive", 
    icon: <DriveIcon />, 
    path: "/drive", 
    roles: ["COMPANY_ADMIN", "MANAGER", "USER"] 
  },
  { 
    text: "Settings", 
    icon: <SettingsIcon />, 
    path: "/settings", 
    roles: ["SUPER_ADMIN", "COMPANY_ADMIN", "MANAGER", "USER"] 
  },
];

interface SidebarProps {
  open: boolean;
  collapsed: boolean;
  onClose: () => void;
}

const SidebarBox = styled(Box, {
  shouldForwardProp: (prop) => prop !== 'collapsed',
})<{ collapsed?: boolean }>(
  ({ theme, collapsed }) => ({
    width: collapsed ? 80 : 280,
    minWidth: collapsed ? 80 : 280,
    maxWidth: collapsed ? 80 : 280,
    height: "100vh",
    backgroundColor: theme.palette.background.paper,
    color: theme.palette.text.primary,
    borderRight: `1px solid ${theme.palette.divider}`,
    display: "flex",
    flexDirection: "column",
    transition: "width 0.3s",
    zIndex: 1200,
  })
);

const Sidebar: React.FC<SidebarProps> = ({ open, collapsed, onClose }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();
  const isMobile = window.innerWidth < 900;

  // Get filtered menu items based on user role
  const menuItems = getMenuItemsForRole(user?.role, allMenuItems);

  if (isMobile) {
    // Mobile: use Drawer
    return (
      <StyledDrawer
        variant="temporary"
        open={open}
        onClose={onClose}
        sx={{
          width: 280,
          "& .MuiDrawer-paper": {
            width: 280,
            transition: "width 0.3s",
          },
        }}
      >
        <Logo>
          <img src="/logo.png" alt="Bika Logo" />
        </Logo>
        <List>
          {menuItems.map((item) => (
            <StyledListItem
              key={item.text}
              active={location.pathname === item.path ? 1 : 0}
              onClick={() => {
                navigate(item.path);
                onClose();
              }}
              sx={{ justifyContent: "flex-start", padding: "8px 16px" }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 40,
                  justifyContent: "center",
                }}
              >
                {item.icon}
              </ListItemIcon>
              <ListItemText
                primary={
                  <Typography variant="body1" fontWeight={500}>
                    {item.text}
                  </Typography>
                }
              />
            </StyledListItem>
          ))}
        </List>
      </StyledDrawer>
    );
  }

  // Desktop: use Box as flex child
  return (
    <SidebarBox collapsed={collapsed}>
      <Logo
        sx={{
          justifyContent: collapsed ? "center" : "flex-start",
          padding: collapsed ? "24px 0" : "24px 16px",
        }}
      >
        {!collapsed ? <img src="/logo.png" alt="Bika Logo" /> : null}
      </Logo>
      <List>
        {menuItems.map((item) => (
          <StyledListItem
            key={item.text}
            active={location.pathname === item.path ? 1 : 0}
            onClick={() => navigate(item.path)}
            sx={{
              justifyContent: collapsed ? "center" : "flex-start",
              padding: collapsed ? "8px 0" : "8px 16px",
            }}
          >
            {collapsed ? (
              <Tooltip title={item.text} placement="right">
                <ListItemIcon
                  sx={{
                    minWidth: 40,
                    justifyContent: "center",
                  }}
                >
                  {item.icon}
                </ListItemIcon>
              </Tooltip>
            ) : (
              <ListItemIcon
                sx={{
                  minWidth: 40,
                  justifyContent: "center",
                }}
              >
                {item.icon}
              </ListItemIcon>
            )}
            {!collapsed ? (
              <ListItemText
                primary={
                  <Typography variant="body1" fontWeight={500}>
                    {item.text}
                  </Typography>
                }
              />
            ) : null}
          </StyledListItem>
        ))}
      </List>
    </SidebarBox>
  );
};

export default Sidebar;
