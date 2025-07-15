package com.bika.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {
    private Long id;
    private String name;
    private String path;
    private String description;
    private Long parentId;
    private Long companyId;
    private Long departmentId;
    private List<FolderDTO> children;
    private List<DocumentDTO> documents;
    private Boolean isActive;
} 