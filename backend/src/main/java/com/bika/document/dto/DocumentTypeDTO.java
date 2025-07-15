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
public class DocumentTypeDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long companyId;
    private List<DocumentFieldDTO> fields;
    private Boolean isActive;
} 