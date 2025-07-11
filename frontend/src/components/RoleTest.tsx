import React from 'react';
import { Box, Typography, Paper, List, ListItem, ListItemText, Chip } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { getMenuItemsForRole, UserRole } from '../constants/roles';

const RoleTest: React.FC = () => {
  const { user } = useAuth();
  
  if (!user) {
    return (
      <Paper sx={{ p: 2, mb: 2 }}>
        <Typography variant="h6" color="error">No user found</Typography>
      </Paper>
    );
  }

  const userRole = user.role as UserRole;
  const menuItems = getMenuItemsForRole(userRole);

  return (
    <Paper sx={{ p: 2, mb: 2 }}>
      <Typography variant="h6" gutterBottom>Role System Test</Typography>
      
      <Box sx={{ mb: 2 }}>
        <Typography variant="body2" color="text.secondary">
          Current User: {user.firstName} {user.lastName} ({user.email})
        </Typography>
        <Chip 
          label={`Role: ${userRole}`} 
          color="primary" 
          size="small" 
          sx={{ mt: 1 }}
        />
      </Box>

      <Typography variant="subtitle2" gutterBottom>
        Available Menu Items ({menuItems.length}):
      </Typography>
      
      <List dense>
        {menuItems.map((item, index) => (
          <ListItem key={index} sx={{ py: 0.5 }}>
            <ListItemText 
              primary={item.text}
              secondary={item.description}
            />
            <Chip 
              label={item.path} 
              size="small" 
              variant="outlined"
            />
          </ListItem>
        ))}
      </List>

      <Box sx={{ mt: 2, p: 1, bgcolor: 'grey.100', borderRadius: 1 }}>
        <Typography variant="caption" color="text.secondary">
          Debug Info: Check browser console for detailed logging
        </Typography>
      </Box>
    </Paper>
  );
};

export default RoleTest; 