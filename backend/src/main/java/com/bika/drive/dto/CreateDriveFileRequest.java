package com.bika.drive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDriveFileRequest {
    
    @NotBlank(message = "File name is required")
    @Size(min = 1, max = 255, message = "File name must be between 1 and 255 characters")
    private String name;
    
    @NotBlank(message = "Original filename is required")
    private String originalFilename;
    
    @NotBlank(message = "File path is required")
    private String filePath;
    
    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    private Long fileSize;
    
    @NotBlank(message = "MIME type is required")
    private String mimeType;
    
    private String fileExtension;
    
    private Long folderId;
} 