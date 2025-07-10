package com.bika.document.entity;

import com.bika.company.entity.Company;
import com.bika.core.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_types")
public class DocumentType extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties("documentTypes")
    private Company company;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column
    private String description;
    
    @Column(name = "metadata_schema", nullable = false, columnDefinition = "jsonb")
    private String metadataSchema;
    
    @OneToMany(mappedBy = "documentType")
    @JsonIgnoreProperties({"company", "department", "folder", "documentType"})
    private List<Document> documents = new ArrayList<>();
    
    @Builder.Default
    @Column(name = "is_active")
    private boolean active = true;
} 