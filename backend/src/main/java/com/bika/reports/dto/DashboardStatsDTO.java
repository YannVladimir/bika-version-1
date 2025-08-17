package com.bika.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    // General stats
    private Long totalUsers;
    private Long totalDocuments;
    private Long totalTasks;
    private Long totalProjects;
    private Long totalCompanies;
    
    // User activity stats
    private Long activeUsers;
    private Long newUsersThisMonth;
    
    // Task stats
    private Long pendingTasks;
    private Long completedTasks;
    private Long overdueTasks;
    private Long tasksCompletedToday;
    
    // Storage stats
    private Double storageUsedGB;
    private Double storageTotalGB;
    private Long documentsUploadedToday;
    
    // User-specific stats
    private Long myTasks;
    private Long myProjects;
    private Long departmentTasks;
    private Long companyTasks;
} 