package com.bika.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalStorageDTO {
    
    @NotBlank(message = "Room is required")
    private String room;
    
    @NotBlank(message = "Cupboard is required")
    private String cupboard;
    
    @NotBlank(message = "Drawer is required")
    private String drawer;
    
    @NotBlank(message = "File number is required")
    private String fileNumber;
    
    @NotBlank(message = "File color is required")
    private String fileColor;
    
    @NotBlank(message = "Document number is required")
    private String documentNumber;
    
    @NotBlank(message = "File section is required")
    private String fileSection;
    
    @NotBlank(message = "Section color is required")
    private String sectionColor;
    
    // Optional file metadata
    private String fileName;
    private String uploadTime;
} 