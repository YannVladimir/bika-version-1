package com.bika.drive.entity;

import com.bika.company.entity.Company;
import com.bika.user.entity.User;
import com.bika.core.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_storage_quota", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "company_id"}))
public class UserStorageQuota extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"company", "department", "storageQuota"})
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"departments", "users", "storageQuotas"})
    private Company company;
    
    @Column(name = "max_storage_bytes", nullable = false)
    @Builder.Default
    private Long maxStorageBytes = 2147483648L; // 2GB default
    
    @Column(name = "used_storage_bytes", nullable = false)
    @Builder.Default
    private Long usedStorageBytes = 0L;
    
    // Helper methods for easier usage
    public double getUsedStorageMB() {
        return usedStorageBytes / (1024.0 * 1024.0);
    }
    
    public double getMaxStorageMB() {
        return maxStorageBytes / (1024.0 * 1024.0);
    }
    
    public double getUsagePercentage() {
        if (maxStorageBytes == 0) return 0.0;
        return (usedStorageBytes * 100.0) / maxStorageBytes;
    }
    
    public long getAvailableBytes() {
        return Math.max(0, maxStorageBytes - usedStorageBytes);
    }
    
    public boolean hasAvailableSpace(long requiredBytes) {
        return getAvailableBytes() >= requiredBytes;
    }
} 