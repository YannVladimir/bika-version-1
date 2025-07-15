package com.bika.drive.service;

import com.bika.drive.dto.CreateDriveFolderRequest;
import com.bika.drive.dto.DriveFolderDTO;
import com.bika.drive.dto.DriveFileDTO;
import com.bika.drive.entity.DriveFolder;
import com.bika.drive.entity.DriveFile;
import com.bika.drive.repository.DriveFolderRepository;
import com.bika.drive.repository.DriveFileRepository;
import com.bika.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriveFolderService {

    private final DriveFolderRepository driveFolderRepository;
    private final DriveFileRepository driveFileRepository;

    @Transactional(readOnly = true)
    public List<DriveFolderDTO> getRootFoldersByUser(User user) {
        List<DriveFolder> folders = driveFolderRepository.findByUserAndParentIsNullAndIsActiveTrue(user);
        return folders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriveFolderDTO> getSubfoldersByParent(User user, Long parentId) {
        Optional<DriveFolder> parentOpt = driveFolderRepository.findByIdAndUserAndIsActiveTrue(parentId, user);
        if (parentOpt.isPresent()) {
            List<DriveFolder> folders = driveFolderRepository.findByParentAndIsActiveTrue(parentOpt.get());
            return folders.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public Optional<DriveFolderDTO> getFolderById(User user, Long id) {
        return driveFolderRepository.findByIdAndUserAndIsActiveTrue(id, user)
                .map(this::toDTOWithContents);
    }

    @Transactional
    public DriveFolderDTO createFolder(User user, CreateDriveFolderRequest request) {
        // Check if folder with same name already exists in the same location
        boolean exists;
        if (request.getParentId() != null) {
            Optional<DriveFolder> parentOpt = driveFolderRepository.findByIdAndUserAndIsActiveTrue(request.getParentId(), user);
            if (parentOpt.isEmpty()) {
                throw new RuntimeException("Parent folder not found");
            }
            exists = driveFolderRepository.existsByUserAndParentAndNameAndIsActiveTrue(user, parentOpt.get(), request.getName());
        } else {
            exists = driveFolderRepository.existsByUserAndParentIsNullAndNameAndIsActiveTrue(user, request.getName());
        }

        if (exists) {
            throw new RuntimeException("Folder with name '" + request.getName() + "' already exists in this location");
        }

        // Get parent folder if specified
        DriveFolder parent = null;
        if (request.getParentId() != null) {
            parent = driveFolderRepository.findByIdAndUserAndIsActiveTrue(request.getParentId(), user)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
        }

        // Generate path
        String path = generatePath(parent, request.getName());

        // Create folder
        DriveFolder folder = DriveFolder.builder()
                .name(request.getName())
                .description(request.getDescription())
                .path(path)
                .parent(parent)
                .user(user)
                .company(user.getCompany())
                .department(user.getDepartment())
                .isActive(true)
                .createdBy(user.getUsername())
                .updatedBy(user.getUsername())
                .build();

        DriveFolder saved = driveFolderRepository.save(folder);
        log.info("Created drive folder: {} for user: {}", saved.getName(), user.getUsername());

        return toDTO(saved);
    }

    @Transactional
    public DriveFolderDTO updateFolder(User user, Long id, CreateDriveFolderRequest request) {
        DriveFolder folder = driveFolderRepository.findByIdAndUserAndIsActiveTrue(id, user)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // Check if new name conflicts with existing folders (if name is being changed)
        if (!folder.getName().equals(request.getName())) {
            boolean exists;
            if (folder.getParent() != null) {
                exists = driveFolderRepository.existsByUserAndParentAndNameAndIsActiveTrue(user, folder.getParent(), request.getName());
            } else {
                exists = driveFolderRepository.existsByUserAndParentIsNullAndNameAndIsActiveTrue(user, request.getName());
            }

            if (exists) {
                throw new RuntimeException("Folder with name '" + request.getName() + "' already exists in this location");
            }

            // Update path if name changed
            String newPath = generatePath(folder.getParent(), request.getName());
            folder.setPath(newPath);
        }

        folder.setName(request.getName());
        folder.setDescription(request.getDescription());
        folder.setUpdatedBy(user.getUsername());

        DriveFolder saved = driveFolderRepository.save(folder);
        log.info("Updated drive folder: {} for user: {}", saved.getName(), user.getUsername());

        return toDTO(saved);
    }

    @Transactional
    public void deleteFolder(User user, Long id) {
        DriveFolder folder = driveFolderRepository.findByIdAndUserAndIsActiveTrue(id, user)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // Check if folder has subfolders or files
        List<DriveFolder> subfolders = driveFolderRepository.findByParentAndIsActiveTrue(folder);
        if (!subfolders.isEmpty()) {
            throw new RuntimeException("Cannot delete folder that contains subfolders");
        }

        // Check if folder has files
        List<DriveFile> folderFiles = driveFileRepository.findByFolderAndIsDeletedFalseAndIsActiveTrue(folder);
        if (!folderFiles.isEmpty()) {
            throw new RuntimeException("Cannot delete folder that contains files");
        }

        folder.setActive(false);
        folder.setUpdatedBy(user.getUsername());
        driveFolderRepository.save(folder);

        log.info("Deleted drive folder: {} for user: {}", folder.getName(), user.getUsername());
    }

    private String generatePath(DriveFolder parent, String name) {
        if (parent == null) {
            return "/" + name;
        }
        return parent.getPath() + "/" + name;
    }

    public DriveFolderDTO toDTO(DriveFolder folder) {
        if (folder == null) return null;

        return DriveFolderDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .path(folder.getPath())
                .description(folder.getDescription())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .companyId(folder.getCompany().getId())
                .departmentId(folder.getDepartment() != null ? folder.getDepartment().getId() : null)
                .userId(folder.getUser().getId())
                .children(new ArrayList<>())
                .files(new ArrayList<>())
                .isActive(folder.isActive())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .createdBy(folder.getCreatedBy())
                .build();
    }

    public DriveFolderDTO toDTOWithContents(DriveFolder folder) {
        if (folder == null) return null;

        DriveFolderDTO dto = toDTO(folder);
        
        // Load subfolders
        List<DriveFolderDTO> children = driveFolderRepository.findByParentAndIsActiveTrue(folder)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        dto.setChildren(children);

        // Load files using repository directly to avoid circular dependency
        List<DriveFileDTO> files = driveFileRepository.findByFolderAndIsDeletedFalseAndIsActiveTrue(folder)
                .stream()
                .map(this::fileToDTO)
                .collect(Collectors.toList());
        dto.setFiles(files);

        return dto;
    }

    private DriveFileDTO fileToDTO(DriveFile file) {
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