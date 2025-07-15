package com.bika.drive.entity;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.user.entity.User;
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
@Table(name = "drive_folders")
public class DriveFolder extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"departments", "users", "driveFolders", "driveFiles"})
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"company", "parent", "children", "users"})
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"company", "department", "driveFolders", "driveFiles"})
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String path;
    
    @Column
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"company", "department", "user", "parent", "children", "files"})
    private DriveFolder parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"company", "department", "user", "parent", "children", "files"})
    private List<DriveFolder> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"company", "department", "user", "folder"})
    private List<DriveFile> files = new ArrayList<>();
    
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
} 