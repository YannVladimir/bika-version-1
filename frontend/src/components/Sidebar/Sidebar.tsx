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
} from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router-dom";
import { tokens } from "../../theme/theme";

const DRAWER_WIDTH = 280;

const StyledDrawer = styled(Drawer)(({ theme }) => ({
  width: DRAWER_WIDTH,
  flexShrink: 0,
  "& .MuiDrawer-paper": {
    width: DRAWER_WIDTH,
    boxSizing: "border-box",
    background: `linear-gradient(180deg, 
      ${tokens.primary.main} 0%, 
      ${tokens.primary.light} 50%, 
      ${tokens.secondary.main} 100%)`,
    color: theme.palette.common.white,
    borderRight: "none",
  },
}));

const StyledListItem = styled(ListItem)<{ active?: number }>(({ active }) => ({
  margin: "8px 16px",
  borderRadius: "12px",
  backgroundColor: active ? "rgba(255, 255, 255, 0.1)" : "transparent",
  "&:hover": {
    backgroundColor: "rgba(255, 255, 255, 0.2)",
  },
  cursor: "pointer",
}));

const Logo = styled(Box)({
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  padding: "24px 16px",
  "& img": {
    width: "120px",
    height: "auto",
  },
});

const menuItems = [
  { text: "Dashboard", icon: <DashboardIcon />, path: "/dashboard" },
  { text: "Companies", icon: <CompanyIcon />, path: "/companies" },
  { text: "Users & Roles", icon: <UsersIcon />, path: "/users" },
  { text: "Document Types", icon: <DocumentIcon />, path: "/document-types" },
  { text: "Archival", icon: <ArchiveIcon />, path: "/archival" },
  { text: "Retrieval", icon: <ArchiveIcon />, path: "/retrieval" },
  { text: "Reports", icon: <ReportsIcon />, path: "/reports" },
  { text: "Drive", icon: <DriveIcon />, path: "/drive" },
  { text: "Settings", icon: <SettingsIcon />, path: "/settings" },
];

interface SidebarProps {
  open: boolean;
  collapsed: boolean;
  onClose: () => void;
}

const SidebarBox = styled(Box)<{ $collapsed?: boolean }>(
  ({ theme, $collapsed }) => ({
    width: $collapsed ? 80 : 280,
    minWidth: $collapsed ? 80 : 280,
    maxWidth: $collapsed ? 80 : 280,
    height: "100vh",
    background: `linear-gradient(180deg, ${tokens.primary.main} 0%, ${tokens.primary.light} 50%, ${tokens.secondary.main} 100%)`,
    color: theme.palette.common.white,
    borderRight: "none",
    display: "flex",
    flexDirection: "column",
    transition: "width 0.3s",
    zIndex: 1200,
  })
);

const Sidebar: React.FC<SidebarProps> = ({ open, collapsed, onClose }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const isMobile = window.innerWidth < 900;

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
                  color: "inherit",
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
    <SidebarBox $collapsed={collapsed}>
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
                    color: "inherit",
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
                  color: "inherit",
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
