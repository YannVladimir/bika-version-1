import { useAuth } from '../context/AuthContext';
import { hasPermission, UserRole } from '../constants/roles';

export const usePermissions = () => {
  const { user } = useAuth();
  
  const userRole = (user?.role as UserRole) || 'USER';
  
  const canAccess = (requiredRoles: UserRole[]): boolean => {
    return hasPermission(userRole, requiredRoles);
  };
  
  const isSuperAdmin = (): boolean => {
    return userRole === 'SUPER_ADMIN';
  };
  
  const isCompanyAdmin = (): boolean => {
    return userRole === 'COMPANY_ADMIN';
  };
  
  const isAdmin = (): boolean => {
    return userRole === 'ADMIN';
  };
  
  const isManager = (): boolean => {
    return userRole === 'MANAGER';
  };
  
  const isUser = (): boolean => {
    return userRole === 'USER';
  };
  
  const canManageCompanies = (): boolean => {
    return canAccess(['SUPER_ADMIN']);
  };
  
  const canManageUsers = (): boolean => {
    return canAccess(['SUPER_ADMIN', 'COMPANY_ADMIN', 'ADMIN', 'MANAGER']);
  };
  
  const canManageDocumentTypes = (): boolean => {
    return canAccess(['COMPANY_ADMIN', 'ADMIN', 'MANAGER']);
  };
  
  const canAccessArchival = (): boolean => {
    return canAccess(['COMPANY_ADMIN', 'ADMIN', 'MANAGER', 'USER']);
  };
  
  const canAccessReports = (): boolean => {
    return canAccess(['COMPANY_ADMIN', 'ADMIN', 'MANAGER']);
  };
  
  const canAccessSettings = (): boolean => {
    return canAccess(['SUPER_ADMIN', 'COMPANY_ADMIN', 'ADMIN', 'MANAGER', 'USER']);
  };
  
  return {
    userRole,
    canAccess,
    isSuperAdmin,
    isCompanyAdmin,
    isAdmin,
    isManager,
    isUser,
    canManageCompanies,
    canManageUsers,
    canManageDocumentTypes,
    canAccessArchival,
    canAccessReports,
    canAccessSettings,
  };
}; 