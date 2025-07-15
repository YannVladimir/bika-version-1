package com.bika.document.entity;

import com.bika.core.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_fields")
public class DocumentField extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    @JsonIgnoreProperties("fields")
    private DocumentType documentType;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String fieldKey; // Internal key for the field
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private FieldType fieldType;
    
    @Column(nullable = false)
    private boolean required = false;
    
    @Column
    private String description;
    
    @Column
    private String defaultValue;
    
    @Column
    private String validationRules; // JSON string for validation rules
    
    @ElementCollection
    @CollectionTable(name = "document_field_options", joinColumns = @JoinColumn(name = "field_id"))
    @Column(name = "option_value")
    private List<String> options; // For select/checkbox fields
    
    @Column
    private Integer displayOrder; // Order of fields in the form
    
    @Builder.Default
    @Column(name = "is_active")
    private boolean active = true;
    
    public enum FieldType {
        TEXT,       // Single line text
        TEXTAREA,   // Multi-line text
        NUMBER,     // Numeric input
        DATE,       // Date picker
        SELECT,     // Dropdown selection
        CHECKBOX,   // Boolean checkbox
        EMAIL,      // Email validation
        PHONE,      // Phone number
        URL         // URL validation
    }
} 