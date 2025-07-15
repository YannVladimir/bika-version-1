package com.bika.drive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStorageQuotaDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private Long maxStorageBytes;
    private Long usedStorageBytes;
    private Double usedStorageMB;
    private Double maxStorageMB;
    private Double usagePercentage;
    private Long availableBytes;
    private Double availableMB;
    
    // Helper method to create DTO with calculated fields
    public static UserStorageQuotaDTO fromEntity(Long id, Long userId, Long companyId, 
                                                Long maxStorageBytes, Long usedStorageBytes) {
        double usedMB = usedStorageBytes / (1024.0 * 1024.0);
        double maxMB = maxStorageBytes / (1024.0 * 1024.0);
        double percentage = maxStorageBytes > 0 ? (usedStorageBytes * 100.0) / maxStorageBytes : 0.0;
        long availableBytes = Math.max(0, maxStorageBytes - usedStorageBytes);
        double availableMB = availableBytes / (1024.0 * 1024.0);
        
        return UserStorageQuotaDTO.builder()
                .id(id)
                .userId(userId)
                .companyId(companyId)
                .maxStorageBytes(maxStorageBytes)
                .usedStorageBytes(usedStorageBytes)
                .usedStorageMB(usedMB)
                .maxStorageMB(maxMB)
                .usagePercentage(percentage)
                .availableBytes(availableBytes)
                .availableMB(availableMB)
                .build();
    }
} 