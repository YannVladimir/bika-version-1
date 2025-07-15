package com.bika.document.dto;

import com.bika.document.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String name;
    private String code;
    private Long companyId;
    private Long departmentId;
    private Long folderId;
    private Long documentTypeId;
    private String documentTypeName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Map<String, Object> metadata; // Document field values
    private Document.DocumentStatus status;
    private String physicalLocation;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
} 