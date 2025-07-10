package com.bika.task.dto;

import com.bika.task.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long companyId;
    private Long departmentId;
    private Long projectId;
    private Long assignedToId;
    private Long createdById;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private LocalDateTime completedDate;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for display
    private String assignedToName;
    private String createdByName;
    private String companyName;
    private String departmentName;
    private String projectName;
} 