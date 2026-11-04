import {
  hasRole,
  hasAnyRole,
  hasMinimumRole,
  getRoleDisplayName,
  canAccessRoute,
  getMenuItemsForRole,
} from './roleUtils';

describe('hasRole', () => {
  it('is true when the role matches exactly', () => {
    expect(hasRole('MANAGER', 'MANAGER')).toBe(true);
  });

  it('is false when the role differs', () => {
    expect(hasRole('USER', 'MANAGER')).toBe(false);
  });

  it('is false when the role is undefined', () => {
    expect(hasRole(undefined, 'MANAGER')).toBe(false);
  });
});

describe('hasAnyRole', () => {
  it('is true when the role is one of the allowed set', () => {
    expect(hasAnyRole('COMPANY_ADMIN', ['SUPER_ADMIN', 'COMPANY_ADMIN'])).toBe(true);
  });

  it('is false when the role is not in the allowed set', () => {
    expect(hasAnyRole('USER', ['SUPER_ADMIN', 'COMPANY_ADMIN'])).toBe(false);
  });

  it('is false when the role is undefined', () => {
    expect(hasAnyRole(undefined, ['USER'])).toBe(false);
  });
});

describe('hasMinimumRole', () => {
  it('is true when the role exceeds the minimum', () => {
    expect(hasMinimumRole('SUPER_ADMIN', 'MANAGER')).toBe(true);
  });

  it('is true when the role exactly meets the minimum', () => {
    expect(hasMinimumRole('MANAGER', 'MANAGER')).toBe(true);
  });

  it('is false when the role is below the minimum', () => {
    expect(hasMinimumRole('USER', 'MANAGER')).toBe(false);
  });

  it('is false when the role is undefined', () => {
    expect(hasMinimumRole(undefined, 'USER')).toBe(false);
  });
});

describe('getRoleDisplayName', () => {
  it.each([
    ['SUPER_ADMIN', 'Super Admin'],
    ['COMPANY_ADMIN', 'Admin'],
    ['MANAGER', 'Manager'],
    ['USER', 'User'],
  ])('renders %s as "%s"', (role, expected) => {
    expect(getRoleDisplayName(role)).toBe(expected);
  });

  it('falls back to "User" when the role is undefined', () => {
    expect(getRoleDisplayName(undefined)).toBe('User');
  });

  it('echoes back an unrecognized role rather than hiding it', () => {
    expect(getRoleDisplayName('SOMETHING_NEW')).toBe('SOMETHING_NEW');
  });
});

describe('canAccessRoute', () => {
  it('denies access when the role is undefined', () => {
    expect(canAccessRoute(undefined, '/dashboard')).toBe(false);
  });

  it('denies access to an unknown route for any role', () => {
    expect(canAccessRoute('SUPER_ADMIN', '/not-a-real-route')).toBe(false);
  });

  it('allows every role to reach the dashboard', () => {
    for (const role of ['SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER']) {
      expect(canAccessRoute(role, '/dashboard')).toBe(true);
    }
  });

  it('restricts /companies to SUPER_ADMIN only', () => {
    expect(canAccessRoute('SUPER_ADMIN', '/companies')).toBe(true);
    expect(canAccessRoute('COMPANY_ADMIN', '/companies')).toBe(false);
  });

  // Documents current behavior, not necessarily intended: /reports only
  // lists MANAGER, so SUPER_ADMIN and COMPANY_ADMIN are denied despite
  // otherwise outranking MANAGER in the role hierarchy. Flagging this
  // rather than "fixing" it, since it may be a deliberate scoping rule.
  it('currently restricts /reports to MANAGER even for higher roles', () => {
    expect(canAccessRoute('MANAGER', '/reports')).toBe(true);
    expect(canAccessRoute('SUPER_ADMIN', '/reports')).toBe(false);
    expect(canAccessRoute('COMPANY_ADMIN', '/reports')).toBe(false);
  });
});

describe('getMenuItemsForRole', () => {
  const allItems = [
    { text: 'Dashboard', icon: null, path: '/dashboard', roles: ['SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'USER'] as const },
    { text: 'Companies', icon: null, path: '/companies', roles: ['SUPER_ADMIN'] as const },
  ];

  it('returns nothing for an undefined role', () => {
    expect(getMenuItemsForRole(undefined, allItems as any)).toEqual([]);
  });

  it('filters to only the items the role is allowed to see', () => {
    const result = getMenuItemsForRole('USER', allItems as any);
    expect(result.map((item) => item.path)).toEqual(['/dashboard']);
  });

  it('shows every item to SUPER_ADMIN', () => {
    const result = getMenuItemsForRole('SUPER_ADMIN', allItems as any);
    expect(result.map((item) => item.path)).toEqual(['/dashboard', '/companies']);
  });
});
