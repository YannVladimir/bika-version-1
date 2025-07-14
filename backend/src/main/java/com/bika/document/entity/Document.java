package com.bika.document.entity;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.core.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Document extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"departments", "documentTypes", "folders", "users", "projects", "tasks"})
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"company", "parent", "children", "documents", "users", "projects", "tasks"})
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonIgnoreProperties({"company", "department", "parent", "children", "documents"})
    private Folder folder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    @JsonIgnoreProperties({"company", "documents"})
    private DocumentType documentType;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "mime_type", nullable = false)
    private String mimeType;
    
    @Column(nullable = false, columnDefinition = "jsonb")
    private String metadata;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "document_status")
    private DocumentStatus status;
    
    @Column(name = "physical_location", columnDefinition = "jsonb")
    private String physicalLocation;
    
    public enum DocumentStatus {
        DRAFT,
        APPROVED,
        ACTIVE,
        ARCHIVED,
        DELETED
    }
} 