package com.bika.drive.entity;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.user.entity.User;
import com.bika.core.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drive_files")
public class DriveFile extends BaseEntity {
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonIgnoreProperties({"company", "department", "user", "parent", "children", "files"})
    private DriveFolder folder;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "mime_type", nullable = false)
    private String mimeType;
    
    @Column(name = "file_extension")
    private String fileExtension;
    
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    
    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;
    
    @Column(name = "download_count")
    @Builder.Default
    private Long downloadCount = 0L;
    
    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
} 