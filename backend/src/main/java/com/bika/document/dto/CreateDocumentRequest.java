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
public class CreateDocumentRequest {
    private String name;
    private String code;
    private Long companyId;
    private Long departmentId;
    private Long folderId;
    private Long documentTypeId;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Map<String, Object> fieldValues; // Dynamic field values based on document type
    private Document.DocumentStatus status;
    private String physicalLocation;
} 