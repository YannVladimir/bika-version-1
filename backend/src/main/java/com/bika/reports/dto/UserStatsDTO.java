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
public class UserStatsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long newUsersThisMonth;
    private Long newUsersThisWeek;
    private Long newUsersToday;
    private Map<String, Long> usersByRole;
    private Map<String, Long> usersByCompany;
    private Map<String, Long> usersByDepartment;
    private Map<String, Long> usersByStatus;
    private Long usersWithRecentActivity;
    private Long usersNeverLoggedIn;
    private Double averageTasksPerUser;
    private Double averageDocumentsPerUser;
} 