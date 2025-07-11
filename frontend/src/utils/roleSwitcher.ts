import { User } from '../services/api/types';
import { USER_ROLES, UserRole } from '../constants/roles';

export const switchUserRole = (newRole: UserRole): void => {
  const currentUser = localStorage.getItem('user');
  if (currentUser) {
    const user: User = JSON.parse(currentUser);
    const updatedUser: User = {
      ...user,
      role: newRole,
      firstName: getRoleDisplayName(newRole).split(' ')[0],
      lastName: getRoleDisplayName(newRole).split(' ')[1] || '',
    };
    localStorage.setItem('user', JSON.stringify(updatedUser));
    
    // Reload the page to update the UI
    window.location.reload();
  }
};

export const getRoleDisplayName = (role: UserRole): string => {
  switch (role) {
    case USER_ROLES.SUPER_ADMIN:
      return 'Super Admin';
    case USER_ROLES.COMPANY_ADMIN:
      return 'Company Admin';
    case USER_ROLES.ADMIN:
      return 'Admin User';
    case USER_ROLES.MANAGER:
      return 'Manager User';
    case USER_ROLES.USER:
      return 'Normal User';
    default:
      return 'Unknown Role';
  }
};

// Convenience functions for quick role switching
export const switchToSuperAdmin = () => switchUserRole(USER_ROLES.SUPER_ADMIN);
export const switchToCompanyAdmin = () => switchUserRole(USER_ROLES.COMPANY_ADMIN);
export const switchToAdmin = () => switchUserRole(USER_ROLES.ADMIN);
export const switchToManager = () => switchUserRole(USER_ROLES.MANAGER);
export const switchToUser = () => switchUserRole(USER_ROLES.USER); 