import React, { useState } from 'react';
import {
  Box,
  Button,
  Menu,
  MenuItem,
  Typography,
  Chip,
  Divider,
} from '@mui/material';
import { KeyboardArrowDown as ArrowDownIcon } from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { USER_ROLES, UserRole } from '../constants/roles';
import { switchUserRole, getRoleDisplayName } from '../utils/roleSwitcher';

const roleColors = {
  [USER_ROLES.SUPER_ADMIN]: '#ff6b6b',
  [USER_ROLES.COMPANY_ADMIN]: '#4ecdc4',
  [USER_ROLES.ADMIN]: '#45b7d1',
  [USER_ROLES.MANAGER]: '#96ceb4',
  [USER_ROLES.USER]: '#feca57',
};

const RoleSwitcher: React.FC = () => {
  const { user } = useAuth();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleRoleSwitch = (newRole: UserRole) => {
    switchUserRole(newRole);
    handleClose();
  };

  if (!user) return null;

  const currentRole = user.role as UserRole;
  const currentRoleColor = roleColors[currentRole] || '#cccccc';

  return (
    <Box>
      <Button
        onClick={handleClick}
        variant="outlined"
        size="small"
        endIcon={<ArrowDownIcon />}
        sx={{
          borderColor: currentRoleColor,
          color: currentRoleColor,
          '&:hover': {
            borderColor: currentRoleColor,
            backgroundColor: `${currentRoleColor}10`,
          },
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="body2">Switch Role:</Typography>
          <Chip
            label={getRoleDisplayName(currentRole)}
            size="small"
            sx={{
              backgroundColor: currentRoleColor,
              color: 'white',
              fontWeight: 600,
              fontSize: '0.7rem',
            }}
          />
        </Box>
      </Button>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        <MenuItem disabled>
          <Typography variant="body2" color="text.secondary">
            Select Role for Testing
          </Typography>
        </MenuItem>
        <Divider />
        {Object.entries(USER_ROLES).map(([key, role]) => (
          <MenuItem
            key={role}
            onClick={() => handleRoleSwitch(role)}
            selected={currentRole === role}
            sx={{
              backgroundColor: currentRole === role ? `${roleColors[role]}20` : 'transparent',
              '&:hover': {
                backgroundColor: `${roleColors[role]}10`,
              },
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
              <Chip
                label={getRoleDisplayName(role)}
                size="small"
                sx={{
                  backgroundColor: roleColors[role],
                  color: 'white',
                  fontWeight: 600,
                  fontSize: '0.7rem',
                }}
              />
              <Typography variant="body2" color="text.secondary">
                {currentRole === role ? '(Current)' : ''}
              </Typography>
            </Box>
          </MenuItem>
        ))}
      </Menu>
    </Box>
  );
};

export default RoleSwitcher; 