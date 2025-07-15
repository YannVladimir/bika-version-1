package com.bika.drive.service;

import com.bika.company.entity.Company;
import com.bika.drive.dto.UserStorageQuotaDTO;
import com.bika.drive.entity.UserStorageQuota;
import com.bika.drive.repository.DriveFileRepository;
import com.bika.drive.repository.UserStorageQuotaRepository;
import com.bika.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriveStorageService {

    private final UserStorageQuotaRepository storageQuotaRepository;
    private final DriveFileRepository driveFileRepository;

    @Transactional(readOnly = true)
    public UserStorageQuotaDTO getUserStorageQuota(User user) {
        Optional<UserStorageQuota> quotaOpt = storageQuotaRepository.findByUser(user);
        
        if (quotaOpt.isPresent()) {
            UserStorageQuota quota = quotaOpt.get();
            return UserStorageQuotaDTO.fromEntity(
                quota.getId(),
                quota.getUser().getId(),
                quota.getCompany().getId(),
                quota.getMaxStorageBytes(),
                quota.getUsedStorageBytes()
            );
        } else {
            // Create default quota if doesn't exist
            return createDefaultQuota(user);
        }
    }

    @Transactional
    public UserStorageQuotaDTO createDefaultQuota(User user) {
        UserStorageQuota quota = UserStorageQuota.builder()
                .user(user)
                .company(user.getCompany())
                .maxStorageBytes(2147483648L) // 2GB default
                .usedStorageBytes(calculateActualUsage(user))
                .createdBy("system")
                .updatedBy("system")
                .build();
        
        UserStorageQuota saved = storageQuotaRepository.save(quota);
        log.info("Created default storage quota for user: {} with 2GB limit", user.getUsername());
        
        return UserStorageQuotaDTO.fromEntity(
            saved.getId(),
            saved.getUser().getId(),
            saved.getCompany().getId(),
            saved.getMaxStorageBytes(),
            saved.getUsedStorageBytes()
        );
    }

    @Transactional
    public void updateStorageUsage(User user, Long sizeChange) {
        Optional<UserStorageQuota> quotaOpt = storageQuotaRepository.findByUser(user);
        
        if (quotaOpt.isPresent()) {
            UserStorageQuota quota = quotaOpt.get();
            quota.setUsedStorageBytes(Math.max(0, quota.getUsedStorageBytes() + sizeChange));
            storageQuotaRepository.save(quota);
            log.debug("Updated storage usage for user: {} by {} bytes", user.getUsername(), sizeChange);
        } else {
            // Create quota if doesn't exist
            createDefaultQuota(user);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasAvailableSpace(User user, Long requiredBytes) {
        UserStorageQuotaDTO quota = getUserStorageQuota(user);
        return quota.getAvailableBytes() >= requiredBytes;
    }

    @Transactional
    public void recalculateStorageUsage(User user) {
        Long actualUsage = calculateActualUsage(user);
        
        Optional<UserStorageQuota> quotaOpt = storageQuotaRepository.findByUser(user);
        if (quotaOpt.isPresent()) {
            UserStorageQuota quota = quotaOpt.get();
            quota.setUsedStorageBytes(actualUsage);
            storageQuotaRepository.save(quota);
            log.info("Recalculated storage usage for user: {} to {} bytes", user.getUsername(), actualUsage);
        }
    }

    private Long calculateActualUsage(User user) {
        Long totalSize = driveFileRepository.calculateTotalFileSizeByUser(user);
        return totalSize != null ? totalSize : 0L;
    }

    @Transactional
    public UserStorageQuotaDTO updateMaxStorage(User user, Long newMaxStorageBytes) {
        Optional<UserStorageQuota> quotaOpt = storageQuotaRepository.findByUser(user);
        
        if (quotaOpt.isPresent()) {
            UserStorageQuota quota = quotaOpt.get();
            quota.setMaxStorageBytes(newMaxStorageBytes);
            UserStorageQuota saved = storageQuotaRepository.save(quota);
            
            return UserStorageQuotaDTO.fromEntity(
                saved.getId(),
                saved.getUser().getId(),
                saved.getCompany().getId(),
                saved.getMaxStorageBytes(),
                saved.getUsedStorageBytes()
            );
        } else {
            // Create new quota with specified max storage
            UserStorageQuota quota = UserStorageQuota.builder()
                    .user(user)
                    .company(user.getCompany())
                    .maxStorageBytes(newMaxStorageBytes)
                    .usedStorageBytes(calculateActualUsage(user))
                    .createdBy("system")
                    .updatedBy("system")
                    .build();
            
            UserStorageQuota saved = storageQuotaRepository.save(quota);
            
            return UserStorageQuotaDTO.fromEntity(
                saved.getId(),
                saved.getUser().getId(),
                saved.getCompany().getId(),
                saved.getMaxStorageBytes(),
                saved.getUsedStorageBytes()
            );
        }
    }
} 