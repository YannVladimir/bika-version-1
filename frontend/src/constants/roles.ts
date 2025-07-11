export const USER_ROLES = {
  SUPER_ADMIN: 'SUPER_ADMIN',
  COMPANY_ADMIN: 'COMPANY_ADMIN', 
  ADMIN: 'ADMIN',
  MANAGER: 'MANAGER',
  USER: 'USER'
} as const;

export type UserRole = typeof USER_ROLES[keyof typeof USER_ROLES];

export interface MenuItem {
  text: string;
  path: string;
  icon: string;
  roles: UserRole[];
  description?: string;
}

export const MENU_ITEMS: MenuItem[] = [
  {
    text: "Dashboard",
    path: "/dashboard",
    icon: "Dashboard",
    roles: [USER_ROLES.SUPER_ADMIN, USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER],
    description: "Overview and analytics"
  },
  {
    text: "Companies",
    path: "/companies", 
    icon: "Business",
    roles: [USER_ROLES.SUPER_ADMIN],
    description: "Manage client companies"
  },
  {
    text: "Users & Roles",
    path: "/users",
    icon: "People", 
    roles: [USER_ROLES.SUPER_ADMIN, USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
    description: "Manage users and their roles"
  },
  {
    text: "Document Types",
    path: "/document-types",
    icon: "Description",
    roles: [USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
    description: "Define document types and metadata"
  },
  {
    text: "Archival",
    path: "/archival",
    icon: "Archive",
    roles: [USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER],
    description: "Archive documents and files"
  },
  {
    text: "Retrieval", 
    path: "/retrieval",
    icon: "Search",
    roles: [USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER],
    description: "Search and retrieve archived documents"
  },
  {
    text: "Reports",
    path: "/reports",
    icon: "Assessment",
    roles: [USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER],
    description: "Generate and view reports"
  },
  {
    text: "Drive",
    path: "/drive",
    icon: "Storage",
    roles: [USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER],
    description: "Personal file storage and sharing"
  },
  {
    text: "Settings",
    path: "/settings",
    icon: "Settings",
    roles: [USER_ROLES.SUPER_ADMIN, USER_ROLES.COMPANY_ADMIN, USER_ROLES.ADMIN, USER_ROLES.MANAGER, USER_ROLES.USER],
    description: "System configuration and preferences"
  }
];

export const getMenuItemsForRole = (role: UserRole): MenuItem[] => {
  return MENU_ITEMS.filter(item => item.roles.includes(role));
};

export const hasPermission = (userRole: UserRole, requiredRoles: UserRole[]): boolean => {
  return requiredRoles.includes(userRole);
}; 