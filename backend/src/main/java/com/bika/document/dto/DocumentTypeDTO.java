package com.bika.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeDTO {
    private Long id;
    private String name;
    private String code;
    private Long companyId;
    private Boolean isActive;
} 