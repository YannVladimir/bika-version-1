import React from 'react';
import { Box, Typography, Chip, Tooltip } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { USER_ROLES, UserRole } from '../constants/roles';

const roleColors = {
  [USER_ROLES.SUPER_ADMIN]: '#ff6b6b', // Red
  [USER_ROLES.COMPANY_ADMIN]: '#4ecdc4', // Teal
  [USER_ROLES.ADMIN]: '#45b7d1', // Blue
  [USER_ROLES.MANAGER]: '#96ceb4', // Green
  [USER_ROLES.USER]: '#feca57', // Yellow
};

const roleDescriptions = {
  [USER_ROLES.SUPER_ADMIN]: 'Super Administrator - Full system access',
  [USER_ROLES.COMPANY_ADMIN]: 'Company Administrator - Company-wide management',
  [USER_ROLES.ADMIN]: 'Administrator - Department and user management',
  [USER_ROLES.MANAGER]: 'Manager - Team and document management',
  [USER_ROLES.USER]: 'User - Basic document operations',
};

const formatRoleName = (role: string): string => {
  return role.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase());
};

interface UserRoleDisplayProps {
  variant?: 'compact' | 'detailed';
  showDescription?: boolean;
}

const UserRoleDisplay: React.FC<UserRoleDisplayProps> = ({ 
  variant = 'compact', 
  showDescription = false 
}) => {
  const { user } = useAuth();
  
  if (!user) return null;

  const userRole = user.role as UserRole;
  const roleColor = roleColors[userRole] || '#cccccc';
  const roleDescription = roleDescriptions[userRole] || 'Unknown role';

  if (variant === 'compact') {
    return (
      <Tooltip title={showDescription ? roleDescription : undefined}>
        <Chip
          label={formatRoleName(userRole)}
          size="small"
          sx={{
            backgroundColor: roleColor,
            color: 'white',
            fontWeight: 600,
            fontSize: '0.75rem',
          }}
        />
      </Tooltip>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <Typography variant="body2" color="text.secondary">
          Role:
        </Typography>
        <Chip
          label={formatRoleName(userRole)}
          size="small"
          sx={{
            backgroundColor: roleColor,
            color: 'white',
            fontWeight: 600,
          }}
        />
      </Box>
      {showDescription && (
        <Typography variant="caption" color="text.secondary">
          {roleDescription}
        </Typography>
      )}
    </Box>
  );
};

export default UserRoleDisplay; 