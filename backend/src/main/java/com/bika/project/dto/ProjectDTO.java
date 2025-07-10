package com.bika.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long companyId;
    private Long departmentId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private boolean isActive;
} 