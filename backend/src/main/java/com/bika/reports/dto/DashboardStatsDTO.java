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
    private Long totalUsers;
    private Long totalDocuments;
    private Long totalTasks;
    private Long totalProjects;
    private Long activeUsers;
    private Long pendingTasks;
    private Long completedTasks;
    private Long overdueTasks;
    private Double storageUsedGB;
    private Double storageTotalGB;
    private Long documentsUploadedToday;
    private Long tasksCompletedToday;
    private Long newUsersThisMonth;
} 