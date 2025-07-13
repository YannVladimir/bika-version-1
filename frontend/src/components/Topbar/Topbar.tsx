import React from "react";
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Box,
  Avatar,
  Menu,
  MenuItem,
  styled,
} from "@mui/material";
import {
  Brightness4 as DarkIcon,
  Brightness7 as LightIcon,
  NotificationsOutlined as NotificationsIcon,
  KeyboardArrowDown as ArrowDownIcon,
  Menu as MenuIcon,
} from "@mui/icons-material";
import { useThemeContext } from "../../theme/ThemeProvider";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { getRoleDisplayName } from "../../utils/roleUtils";

const StyledAppBar = styled(AppBar)(({ theme }) => ({
  backgroundColor: theme.palette.background.paper,
  color: theme.palette.text.primary,
  boxShadow: "none",
  borderBottom: `1px solid ${theme.palette.divider}`,
  zIndex: theme.zIndex.drawer + 1,
}));

const UserProfile = styled(Box)({
  display: "flex",
  alignItems: "center",
  gap: "8px",
  cursor: "pointer",
  padding: "4px 8px",
  borderRadius: "8px",
  "&:hover": {
    backgroundColor: "rgba(0, 0, 0, 0.04)",
  },
});

interface TopbarProps {
  onSidebarToggle: () => void;
}

const Topbar: React.FC<TopbarProps> = ({ onSidebarToggle }) => {
  const { mode, toggleTheme } = useThemeContext();
  const { user, logout } = useAuth();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const navigate = useNavigate();

  const handleProfileClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleProfile = () => {
    handleClose();
    navigate("/profile");
  };

  const handleLogout = async () => {
    handleClose();
    try {
      await logout();
      navigate("/login");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  // Get user initials
  const getUserInitials = () => {
    if (!user) return "U";
    const firstInitial = user.firstName
      ? user.firstName.charAt(0).toUpperCase()
      : "";
    const lastInitial = user.lastName
      ? user.lastName.charAt(0).toUpperCase()
      : "";
    return (
      `${firstInitial}${lastInitial}` || user.email.charAt(0).toUpperCase()
    );
  };

  // Get user display name
  const getUserDisplayName = () => {
    if (!user) return "User";
    return (
      `${user.firstName || ""} ${user.lastName || ""}`.trim() || user.email
    );
  };

  return (
    <StyledAppBar position="fixed">
      <Toolbar>
        <IconButton
          color="inherit"
          edge="start"
          onClick={onSidebarToggle}
          sx={{ mr: 2, display: { xs: "inline-flex", md: "inline-flex" } }}
        >
          <MenuIcon />
        </IconButton>
        <Box sx={{ flexGrow: 1 }} />
        <IconButton color="inherit" onClick={toggleTheme} sx={{ mr: 2 }}>
          {mode === "dark" ? <LightIcon /> : <DarkIcon />}
        </IconButton>
        <IconButton color="inherit" sx={{ mr: 2 }}>
          <NotificationsIcon />
        </IconButton>
        <UserProfile onClick={handleProfileClick}>
          <Avatar
            sx={{
              width: 32,
              height: 32,
              backgroundColor: "primary.main",
            }}
          >
            {getUserInitials()}
          </Avatar>
          <Box>
            <Typography variant="body2" fontWeight={500}>
              {getUserDisplayName()}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {getRoleDisplayName(user?.role)}
            </Typography>
          </Box>
          <ArrowDownIcon sx={{ fontSize: 20 }} />
        </UserProfile>
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleClose}
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "right",
          }}
          transformOrigin={{
            vertical: "top",
            horizontal: "right",
          }}
        >
          <MenuItem onClick={handleProfile}>Profile</MenuItem>
          <MenuItem onClick={handleLogout}>Logout</MenuItem>
        </Menu>
      </Toolbar>
    </StyledAppBar>
  );
};

export default Topbar;
