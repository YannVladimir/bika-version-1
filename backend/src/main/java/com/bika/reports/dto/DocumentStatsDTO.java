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
public class DocumentStatsDTO {
    private Long totalDocuments;
    private Long documentsThisMonth;
    private Long documentsThisWeek;
    private Long documentsToday;
    private Map<String, Long> documentsByType;
    private Map<String, Long> documentsByStatus;
    private Map<String, Long> documentsByCompany;
    private Map<String, Long> documentsByDepartment;
    private Double averageFileSizeMB;
    private Long totalStorageUsedMB;
} 