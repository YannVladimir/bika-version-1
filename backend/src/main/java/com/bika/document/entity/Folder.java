package com.bika.document.entity;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
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
@Table(name = "folders")
public class Folder extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"departments", "documentTypes", "folders", "users", "projects", "tasks"})
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"company", "parent", "children", "documents", "users", "projects", "tasks"})
    private Department department;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String path;
    
    @Column
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"company", "department", "parent", "children", "documents"})
    private Folder parent;
    
    @OneToMany(mappedBy = "parent")
    @JsonIgnoreProperties({"company", "department", "parent", "children", "documents"})
    private List<Folder> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "folder")
    @JsonIgnoreProperties({"company", "department", "folder", "documentType"})
    private List<Document> documents = new ArrayList<>();
    
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
} 