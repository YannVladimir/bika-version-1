import apiClient from './client';

export interface DashboardStats {
  // General stats
  totalUsers?: number;
  totalDocuments?: number;
  totalTasks?: number;
  totalProjects?: number;
  totalCompanies?: number;
  
  // User activity stats
  activeUsers?: number;
  newUsersThisMonth?: number;
  
  // Task stats
  pendingTasks?: number;
  completedTasks?: number;
  overdueTasks?: number;
  tasksCompletedToday?: number;
  
  // Storage stats
  storageUsedGB?: number;
  storageTotalGB?: number;
  documentsUploadedToday?: number;
  
  // User-specific stats
  myTasks?: number;
  myProjects?: number;
  departmentTasks?: number;
  companyTasks?: number;
}

export interface RecentActivity {
  id: number;
  user: string;
  action: string;
  document: string;
  time: string;
  type: 'task' | 'document' | 'project' | 'company' | 'department';
}

export const dashboardService = {
  async getRoleBasedDashboardStats(): Promise<DashboardStats> {
    try {
      const response = await apiClient.get<DashboardStats>('/reports/dashboard/role-based');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch role-based dashboard stats:', error);
      throw error;
    }
  },

  async getRecentActivities(): Promise<RecentActivity[]> {
    try {
      // TODO: Implement when backend endpoint is available
      // For now, return mock data
      const mockActivities: RecentActivity[] = [
        {
          id: 1,
          user: "Current User",
          action: "completed",
          document: "Monthly Report Review",
          time: "2 hours ago",
          type: "task"
        },
        {
          id: 2,
          user: "Sarah Wilson",
          action: "uploaded",
          document: "Project Specifications.pdf",
          time: "4 hours ago",
          type: "document"
        },
        {
          id: 3,
          user: "Mike Chen",
          action: "created",
          document: "Q1 Marketing Campaign",
          time: "6 hours ago",
          type: "project"
        },
      ];
      return mockActivities;
    } catch (error) {
      console.error('Failed to fetch recent activities:', error);
      throw error;
    }
  },

  async refreshDashboard(): Promise<{ stats: DashboardStats; activities: RecentActivity[] }> {
    try {
      const [stats, activities] = await Promise.all([
        this.getRoleBasedDashboardStats(),
        this.getRecentActivities()
      ]);
      
      return { stats, activities };
    } catch (error) {
      console.error('Failed to refresh dashboard:', error);
      throw error;
    }
  }
}; 