package com.bika.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatsDTO {
    private Long totalTasks;
    private Long tasksThisMonth;
    private Long tasksThisWeek;
    private Long tasksToday;
    private Long completedTasks;
    private Long pendingTasks;
    private Long overdueTasks;
    private Map<String, Long> tasksByStatus;
    private Map<String, Long> tasksByPriority;
    private Map<String, Long> tasksByCompany;
    private Map<String, Long> tasksByDepartment;
    private Map<String, Long> tasksByAssignee;
    private Double averageCompletionTimeDays;
    private Long tasksCompletedOnTime;
    private Long tasksCompletedLate;
} 