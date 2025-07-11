# Role-Based Sidebar System

## Overview

The Bika application now features a comprehensive role-based access control (RBAC) system that dynamically shows different menu items based on the user's role. This ensures that users only see and can access features they have permission to use.

## User Roles

### 1. SUPER_ADMIN
- **Description**: Super Administrator (C-NOTE company admin)
- **Scope**: Global system access
- **Key Permissions**:
  - Manage all companies
  - Manage all users across companies
  - Access system settings
  - Full administrative control

### 2. COMPANY_ADMIN
- **Description**: Company Administrator (client company admin)
- **Scope**: Company-wide management
- **Key Permissions**:
  - Manage users within their company
  - Define document types
  - Access archival, retrieval, reports, and drive
  - Company-level settings

### 3. ADMIN
- **Description**: Administrator
- **Scope**: Department and user management
- **Key Permissions**:
  - Manage users within their scope
  - Define document types
  - Access archival, retrieval, reports, and drive
  - Administrative settings

### 4. MANAGER
- **Description**: Manager
- **Scope**: Team and document management
- **Key Permissions**:
  - Manage users within their scope
  - Define document types
  - Access archival, retrieval, reports, and drive
  - Team management features

### 5. USER
- **Description**: Normal user
- **Scope**: Basic document operations
- **Key Permissions**:
  - Access archival and retrieval
  - Use personal drive
  - Access personal settings
  - Basic document operations

## Menu Items by Role

| Menu Item | SUPER_ADMIN | COMPANY_ADMIN | ADMIN | MANAGER | USER |
|-----------|-------------|---------------|-------|---------|------|
| Dashboard | ✅ | ✅ | ✅ | ✅ | ✅ |
| Companies | ✅ | ❌ | ❌ | ❌ | ❌ |
| Users & Roles | ✅ | ✅ | ✅ | ✅ | ❌ |
| Document Types | ❌ | ✅ | ✅ | ✅ | ❌ |
| Archival | ✅ | ✅ | ✅ | ✅ | ✅ |
| Retrieval | ✅ | ✅ | ✅ | ✅ | ✅ |
| Reports | ❌ | ✅ | ✅ | ✅ | ❌ |
| Drive | ✅ | ✅ | ✅ | ✅ | ✅ |
| Settings | ✅ | ✅ | ✅ | ✅ | ✅ |

## Implementation Details

### Files Created/Modified

1. **`src/constants/roles.ts`**
   - Defines user roles and menu structure
   - Contains permission checking utilities
   - Maps roles to menu items

2. **`src/utils/iconMapper.ts`**
   - Maps icon names to Material-UI components
   - Provides fallback icons

3. **`src/components/Sidebar/Sidebar.tsx`**
   - Updated to use role-based menu filtering
   - Integrates with authentication context

4. **`src/components/RoleBasedRoute.tsx`**
   - New component for route-level protection
   - Redirects unauthorized users

5. **`src/components/UserRoleDisplay.tsx`**
   - Displays user role with color coding
   - Shows role descriptions

6. **`src/hooks/usePermissions.ts`**
   - Custom hook for permission checking
   - Provides convenient permission methods

7. **`src/App.tsx`**
   - Updated routes with role-based protection
   - Wraps protected pages with RoleBasedRoute

8. **`src/components/Topbar/Topbar.tsx`**
   - Integrated role display in topbar
   - Shows user role prominently

### Key Features

#### Dynamic Menu Generation
```typescript
const userRole = (user?.role as UserRole) || 'USER';
const menuItems = getMenuItemsForRole(userRole);
```

#### Route Protection
```typescript
<RoleBasedRoute allowedRoles={[USER_ROLES.SUPER_ADMIN]}>
  <Companies />
</RoleBasedRoute>
```

#### Permission Checking
```typescript
const permissions = usePermissions();
if (permissions.canManageCompanies()) {
  // Show company management features
}
```

#### Role Display
```typescript
<UserRoleDisplay variant="compact" showDescription={true} />
```

## Usage Examples

### Checking Permissions in Components
```typescript
import { usePermissions } from '../hooks/usePermissions';

const MyComponent = () => {
  const permissions = usePermissions();
  
  return (
    <div>
      {permissions.canManageUsers() && (
        <button>Manage Users</button>
      )}
      
      {permissions.isSuperAdmin() && (
        <button>System Settings</button>
      )}
    </div>
  );
};
```

### Protecting Routes
```typescript
<Route 
  path="/admin" 
  element={
    <RoleBasedRoute allowedRoles={[USER_ROLES.ADMIN, USER_ROLES.SUPER_ADMIN]}>
      <AdminPage />
    </RoleBasedRoute>
  } 
/>
```

### Displaying User Role
```typescript
// Compact display in topbar
<UserRoleDisplay variant="compact" />

// Detailed display with description
<UserRoleDisplay variant="detailed" showDescription={true} />
```

## Security Features

1. **Client-Side Protection**: Routes are protected at the component level
2. **Server-Side Validation**: Backend validates permissions for all API calls
3. **Graceful Fallbacks**: Users are redirected to accessible pages
4. **Visual Feedback**: Clear indication of user role and permissions

## Future Enhancements

1. **Granular Permissions**: More specific permissions (e.g., READ_ONLY, WRITE, DELETE)
2. **Department-Level Access**: Permissions based on department membership
3. **Temporary Permissions**: Time-limited access grants
4. **Audit Logging**: Track permission changes and access attempts
5. **Permission Groups**: Predefined permission sets for common roles

## Testing

To test different roles, you can modify the user role in the authentication service or create test users with different roles. The system will automatically adjust the sidebar and available features based on the user's role. 