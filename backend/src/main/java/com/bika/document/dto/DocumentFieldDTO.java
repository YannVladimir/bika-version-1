package com.bika.document.dto;

import com.bika.document.entity.DocumentField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFieldDTO {
    private Long id;
    private String name;
    private String fieldKey;
    private DocumentField.FieldType fieldType;
    private boolean required;
    private String description;
    private String defaultValue;
    private String validationRules;
    private List<String> options;
    private Integer displayOrder;
    private boolean active;
} 