package com.bika.document.dto;

import com.bika.document.entity.Document;
import com.bika.document.validation.ValidPhysicalStorage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Document name is required")
    @Size(min = 1, max = 255, message = "Document name must be between 1 and 255 characters")
    private String name;
    
    @NotBlank(message = "Document code is required")
    @Size(min = 1, max = 50, message = "Document code must be between 1 and 50 characters")
    private String code;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    private Long departmentId;
    private Long folderId;
    
    @NotNull(message = "Document type ID is required")
    private Long documentTypeId;
    
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Map<String, Object> fieldValues; // Dynamic field values based on document type
    private Document.DocumentStatus status;
    
    @ValidPhysicalStorage(message = "Physical storage information is required and must be valid")
    private String physicalLocation;
} 