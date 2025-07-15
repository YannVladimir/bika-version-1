package com.bika.drive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveFolderDTO {
    private Long id;
    private String name;
    private String path;
    private String description;
    private Long parentId;
    private Long companyId;
    private Long departmentId;
    private Long userId;
    private List<DriveFolderDTO> children;
    private List<DriveFileDTO> files;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
} 