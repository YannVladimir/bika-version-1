package com.bika.drive.service;

import com.bika.drive.dto.CreateDriveFileRequest;
import com.bika.drive.dto.DriveFileDTO;
import com.bika.drive.entity.DriveFile;
import com.bika.drive.entity.DriveFolder;
import com.bika.drive.repository.DriveFileRepository;
import com.bika.drive.repository.DriveFolderRepository;
import com.bika.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriveFileService {

    private final DriveFileRepository driveFileRepository;
    private final DriveFolderRepository driveFolderRepository;
    private final DriveStorageService driveStorageService;

    @Transactional(readOnly = true)
    public List<DriveFileDTO> getFilesByUser(User user) {
        List<DriveFile> files = driveFileRepository.findByUserAndIsDeletedFalseAndIsActiveTrue(user);
        return files.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriveFileDTO> getFilesByFolder(User user, Long folderId) {
        if (folderId == null) {
            // Get files in root folder (no parent folder)
            List<DriveFile> files = driveFileRepository.findByUserAndFolderIsNullAndIsDeletedFalseAndIsActiveTrue(user);
            return files.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } else {
            // Get files in specific folder
            Optional<DriveFolder> folderOpt = driveFolderRepository.findByIdAndUserAndIsActiveTrue(folderId, user);
            if (folderOpt.isPresent()) {
                List<DriveFile> files = driveFileRepository.findByFolderAndIsDeletedFalseAndIsActiveTrue(folderOpt.get());
                return files.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
            }
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public Optional<DriveFileDTO> getFileById(User user, Long id) {
        return driveFileRepository.findByIdAndUserAndIsActiveTrue(id, user)
                .map(this::toDTO);
    }

    @Transactional
    public DriveFileDTO createFile(User user, CreateDriveFileRequest request) {
        // Check storage quota
        if (!driveStorageService.hasAvailableSpace(user, request.getFileSize())) {
            throw new RuntimeException("Insufficient storage space. File size exceeds available quota.");
        }

        // Check if file with same name already exists in the same location
        boolean exists;
        DriveFolder folder = null;
        
        if (request.getFolderId() != null) {
            folder = driveFolderRepository.findByIdAndUserAndIsActiveTrue(request.getFolderId(), user)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            exists = driveFileRepository.existsByUserAndFolderAndNameAndIsDeletedFalseAndIsActiveTrue(user, folder, request.getName());
        } else {
            exists = driveFileRepository.existsByUserAndFolderIsNullAndNameAndIsDeletedFalseAndIsActiveTrue(user, request.getName());
        }

        if (exists) {
            throw new RuntimeException("File with name '" + request.getName() + "' already exists in this location");
        }

        // Extract file extension if not provided
        String fileExtension = request.getFileExtension();
        if (fileExtension == null || fileExtension.isEmpty()) {
            fileExtension = extractFileExtension(request.getOriginalFilename());
        }

        // Create file
        DriveFile file = DriveFile.builder()
                .name(request.getName())
                .originalFilename(request.getOriginalFilename())
                .filePath(request.getFilePath())
                .fileSize(request.getFileSize())
                .mimeType(request.getMimeType())
                .fileExtension(fileExtension)
                .folder(folder)
                .user(user)
                .company(user.getCompany())
                .department(user.getDepartment())
                .isActive(true)
                .isDeleted(false)
                .downloadCount(0L)
                .createdBy(user.getUsername())
                .updatedBy(user.getUsername())
                .build();

        DriveFile saved = driveFileRepository.save(file);

        // Update storage usage
        driveStorageService.updateStorageUsage(user, request.getFileSize());

        log.info("Created drive file: {} for user: {} in folder: {}", 
                saved.getName(), user.getUsername(), folder != null ? folder.getName() : "root");

        return toDTO(saved);
    }

    @Transactional
    public DriveFileDTO updateFile(User user, Long id, CreateDriveFileRequest request) {
        DriveFile file = driveFileRepository.findByIdAndUserAndIsActiveTrue(id, user)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Check if new name conflicts with existing files (if name is being changed)
        if (!file.getName().equals(request.getName())) {
            boolean exists;
            if (file.getFolder() != null) {
                exists = driveFileRepository.existsByUserAndFolderAndNameAndIsDeletedFalseAndIsActiveTrue(user, file.getFolder(), request.getName());
            } else {
                exists = driveFileRepository.existsByUserAndFolderIsNullAndNameAndIsDeletedFalseAndIsActiveTrue(user, request.getName());
            }

            if (exists) {
                throw new RuntimeException("File with name '" + request.getName() + "' already exists in this location");
            }
        }

        file.setName(request.getName());
        file.setUpdatedBy(user.getUsername());

        DriveFile saved = driveFileRepository.save(file);
        log.info("Updated drive file: {} for user: {}", saved.getName(), user.getUsername());

        return toDTO(saved);
    }

    @Transactional
    public void deleteFile(User user, Long id) {
        DriveFile file = driveFileRepository.findByIdAndUserAndIsActiveTrue(id, user)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Soft delete
        file.setDeleted(true);
        file.setUpdatedBy(user.getUsername());
        driveFileRepository.save(file);

        // Update storage usage
        driveStorageService.updateStorageUsage(user, -file.getFileSize());

        log.info("Deleted drive file: {} for user: {}", file.getName(), user.getUsername());
    }

    @Transactional
    public void recordFileAccess(User user, Long id) {
        Optional<DriveFile> fileOpt = driveFileRepository.findByIdAndUserAndIsActiveTrue(id, user);
        if (fileOpt.isPresent()) {
            DriveFile file = fileOpt.get();
            file.setLastAccessed(LocalDateTime.now());
            file.setDownloadCount(file.getDownloadCount() + 1);
            driveFileRepository.save(file);
            log.debug("Recorded access for file: {} by user: {}", file.getName(), user.getUsername());
        }
    }

    @Transactional(readOnly = true)
    public List<DriveFileDTO> getFilesByType(User user, String mimeTypePattern) {
        List<DriveFile> files = driveFileRepository.findByUserAndMimeTypeContainingAndIsDeletedFalseAndIsActiveTrue(user, mimeTypePattern);
        return files.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String extractFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }

    public DriveFileDTO toDTO(DriveFile file) {
        if (file == null) return null;

        return DriveFileDTO.builder()
                .id(file.getId())
                .name(file.getName())
                .originalFilename(file.getOriginalFilename())
                .filePath(file.getFilePath())
                .fileSize(file.getFileSize())
                .mimeType(file.getMimeType())
                .fileExtension(file.getFileExtension())
                .folderId(file.getFolder() != null ? file.getFolder().getId() : null)
                .folderName(file.getFolder() != null ? file.getFolder().getName() : null)
                .companyId(file.getCompany().getId())
                .departmentId(file.getDepartment() != null ? file.getDepartment().getId() : null)
                .userId(file.getUser().getId())
                .isActive(file.isActive())
                .isDeleted(file.isDeleted())
                .downloadCount(file.getDownloadCount())
                .lastAccessed(file.getLastAccessed())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .createdBy(file.getCreatedBy())
                .build();
    }
} 