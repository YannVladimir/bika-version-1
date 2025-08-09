package com.bika.document.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeDTO {
    private Long id;
    
    @NotBlank(message = "Document type name is required")
    @Size(min = 1, max = 255, message = "Document type name must be between 1 and 255 characters")
    private String name;
    
    @NotBlank(message = "Document type code is required")
    @Size(min = 1, max = 50, message = "Document type code must be between 1 and 50 characters")
    private String code;
    
    private String description;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    @Valid
    private List<DocumentFieldDTO> fields;
    
    private Boolean isActive;
} 