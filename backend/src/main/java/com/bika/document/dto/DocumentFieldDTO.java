package com.bika.document.dto;

import com.bika.document.entity.DocumentField;
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
public class DocumentFieldDTO {
    private Long id;
    
    @NotBlank(message = "Field name is required")
    @Size(min = 1, max = 255, message = "Field name must be between 1 and 255 characters")
    private String name;
    
    @NotBlank(message = "Field key is required")
    @Size(min = 1, max = 100, message = "Field key must be between 1 and 100 characters")
    private String fieldKey;
    
    @NotNull(message = "Field type is required")
    private DocumentField.FieldType fieldType;
    
    private boolean required;
    private String description;
    private String defaultValue;
    private String validationRules;
    private List<String> options;
    private Integer displayOrder;
    private boolean active;
} 