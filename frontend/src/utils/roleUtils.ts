// Role-based access control utilities

export type UserRole = 'SUPER_ADMIN' | 'COMPANY_ADMIN' | 'MANAGER' | 'USER';

// Role hierarchy - higher number means higher privilege
export const ROLE_HIERARCHY: Record<UserRole, number> = {
  'USER': 1,
  'MANAGER': 2,
  'COMPANY_ADMIN': 3,
  'SUPER_ADMIN': 4,
};

// Check if user has a specific role
export const hasRole = (userRole: string | undefined, requiredRole: UserRole): boolean => {
  if (!userRole) return false;
  return userRole === requiredRole;
};

// Check if user has any of the required roles
export const hasAnyRole = (userRole: string | undefined, requiredRoles: UserRole[]): boolean => {
  if (!userRole) return false;
  return requiredRoles.includes(userRole as UserRole);
};

// Check if user has minimum role level (equal or higher in hierarchy)
export const hasMinimumRole = (userRole: string | undefined, minimumRole: UserRole): boolean => {
  if (!userRole) return false;
  const userLevel = ROLE_HIERARCHY[userRole as UserRole];
  const minimumLevel = ROLE_HIERARCHY[minimumRole];
  return userLevel >= minimumLevel;
};

// Get user-friendly role display name
export const getRoleDisplayName = (role: string | undefined): string => {
  if (!role) return 'User';
  
  switch (role) {
    case 'SUPER_ADMIN':
      return 'Super Admin';
    case 'COMPANY_ADMIN':
      return 'Admin';
    case 'MANAGER':
      return 'Manager';
    case 'USER':
      return 'User';
    default:
      return role;
  }
};

// Check if user can access a specific route based on role
export const canAccessRoute = (userRole: string | undefined, routePath: string): boolean => {
  if (!userRole) return false;

  // Define route permissions
  const routePermissions: Record<string, UserRole[]> = {
    '/dashboard': ['SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER'],
    '/companies': ['SUPER_ADMIN'],
    '/users': ['SUPER_ADMIN', 'COMPANY_ADMIN'],
    '/departments': ['SUPER_ADMIN', 'COMPANY_ADMIN'],
    '/document-types': ['COMPANY_ADMIN', 'MANAGER', 'USER'],
    '/archival': ['COMPANY_ADMIN', 'MANAGER', 'USER'],
    '/retrieval': ['COMPANY_ADMIN', 'MANAGER', 'USER'],
    '/reports': ['MANAGER'],
    '/drive': ['COMPANY_ADMIN', 'MANAGER', 'USER'],
    '/settings': ['SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER'],
  };

  const allowedRoles = routePermissions[routePath];
  return allowedRoles ? hasAnyRole(userRole, allowedRoles) : false;
};

// Get menu items for a specific role
export interface MenuItem {
  text: string;
  icon: React.ReactNode;
  path: string;
  roles: UserRole[];
}

export const getMenuItemsForRole = (userRole: string | undefined, allMenuItems: MenuItem[]): MenuItem[] => {
  if (!userRole) return [];
  
  return allMenuItems.filter(item => item.roles.includes(userRole as UserRole));
}; 