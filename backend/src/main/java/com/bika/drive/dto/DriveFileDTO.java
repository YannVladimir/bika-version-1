package com.bika.drive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveFileDTO {
    private Long id;
    private String name;
    private String originalFilename;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String fileExtension;
    private Long folderId;
    private String folderName;
    private Long companyId;
    private Long departmentId;
    private Long userId;
    private Boolean isActive;
    private Boolean isDeleted;
    private Long downloadCount;
    private LocalDateTime lastAccessed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    
    // Helper methods for UI display
    public String getFileSizeFormatted() {
        if (fileSize == null) return "0 B";
        
        long bytes = fileSize;
        if (bytes < 1024) return bytes + " B";
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    public String getFileType() {
        if (mimeType == null) return "Unknown";
        
        if (mimeType.startsWith("image/")) return "Image";
        if (mimeType.startsWith("video/")) return "Video";
        if (mimeType.startsWith("audio/")) return "Audio";
        if (mimeType.equals("application/pdf")) return "PDF";
        if (mimeType.contains("word") || mimeType.contains("document")) return "Document";
        if (mimeType.contains("excel") || mimeType.contains("spreadsheet")) return "Spreadsheet";
        if (mimeType.contains("powerpoint") || mimeType.contains("presentation")) return "Presentation";
        if (mimeType.startsWith("text/")) return "Text";
        if (mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("archive")) return "Archive";
        
        return "File";
    }
} 