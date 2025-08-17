package com.bika.drive.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.drive.dto.*;
import com.bika.drive.service.DriveFolderService;
import com.bika.drive.service.DriveFileService;
import com.bika.drive.service.DriveStorageService;
import com.bika.user.entity.User;
import com.bika.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/drive")
@RequiredArgsConstructor
@Tag(name = "Drive", description = "Personal Drive APIs for file storage management")
@Slf4j
public class DriveController {

    private final DriveFolderService driveFolderService;
    private final DriveFileService driveFileService;
    private final DriveStorageService driveStorageService;
    private final UserRepository userRepository;

    // Storage Quota Endpoints

    @Operation(
        summary = "Get user storage quota",
        description = "Retrieve current user's storage quota and usage information."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Storage quota retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserStorageQuotaDTO.class))
        )
    })
    @GetMapping("/storage/quota")
    public ResponseEntity<UserStorageQuotaDTO> getStorageQuota() {
        log.info("DriveController: getStorageQuota called");
        try {
            User currentUser = getCurrentUser();
            UserStorageQuotaDTO quota = driveStorageService.getUserStorageQuota(currentUser);
            log.info("DriveController: Retrieved storage quota for user: {}", currentUser.getUsername());
            return ResponseEntity.ok(quota);
        } catch (Exception e) {
            log.error("DriveController: Error getting storage quota", e);
            throw e;
        }
    }

    // Folder Endpoints

    @Operation(
        summary = "Get root folders",
        description = "Retrieve all root folders for the current user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Root folders retrieved successfully",
            content = @Content(schema = @Schema(implementation = DriveFolderDTO.class))
        )
    })
    @GetMapping("/folders/root")
    public ResponseEntity<List<DriveFolderDTO>> getRootFolders() {
        log.info("DriveController: getRootFolders called");
        try {
            User currentUser = getCurrentUser();
            List<DriveFolderDTO> folders = driveFolderService.getRootFoldersByUser(currentUser);
            log.info("DriveController: Retrieved {} root folders for user: {}", folders.size(), currentUser.getUsername());
            return ResponseEntity.ok(folders);
        } catch (Exception e) {
            log.error("DriveController: Error getting root folders", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get folder by ID",
        description = "Retrieve a specific folder with its contents by ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Folder retrieved successfully",
            content = @Content(schema = @Schema(implementation = DriveFolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Folder not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/folders/{id}")
    public ResponseEntity<DriveFolderDTO> getFolderById(@PathVariable Long id) {
        log.info("DriveController: getFolderById called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            Optional<DriveFolderDTO> folder = driveFolderService.getFolderById(currentUser, id);
            if (folder.isPresent()) {
                log.info("DriveController: Folder found with ID: {}", id);
                return ResponseEntity.ok(folder.get());
            } else {
                log.warn("DriveController: Folder not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("DriveController: Error getting folder by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Create a new folder",
        description = "Create a new folder in the user's drive."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Folder created successfully",
            content = @Content(schema = @Schema(implementation = DriveFolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/folders")
    public ResponseEntity<DriveFolderDTO> createFolder(@Valid @RequestBody CreateDriveFolderRequest request) {
        log.info("DriveController: createFolder called for name: {}", request.getName());
        try {
            User currentUser = getCurrentUser();
            DriveFolderDTO folder = driveFolderService.createFolder(currentUser, request);
            log.info("DriveController: Folder created successfully with ID: {}", folder.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(folder);
        } catch (Exception e) {
            log.error("DriveController: Error creating folder", e);
            throw e;
        }
    }

    @Operation(
        summary = "Update folder",
        description = "Update an existing folder's information."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Folder updated successfully",
            content = @Content(schema = @Schema(implementation = DriveFolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Folder not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/folders/{id}")
    public ResponseEntity<DriveFolderDTO> updateFolder(@PathVariable Long id, @Valid @RequestBody CreateDriveFolderRequest request) {
        log.info("DriveController: updateFolder called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            DriveFolderDTO folder = driveFolderService.updateFolder(currentUser, id, request);
            log.info("DriveController: Folder updated successfully with ID: {}", id);
            return ResponseEntity.ok(folder);
        } catch (Exception e) {
            log.error("DriveController: Error updating folder with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete folder",
        description = "Delete an existing folder."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Folder deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Folder not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/folders/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        log.info("DriveController: deleteFolder called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            driveFolderService.deleteFolder(currentUser, id);
            log.info("DriveController: Folder deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DriveController: Error deleting folder with id: {}", id, e);
            throw e;
        }
    }

    // File Endpoints

    @Operation(
        summary = "Get all user files",
        description = "Retrieve all files for the current user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Files retrieved successfully",
            content = @Content(schema = @Schema(implementation = DriveFileDTO.class))
        )
    })
    @GetMapping("/files")
    public ResponseEntity<List<DriveFileDTO>> getAllFiles() {
        log.info("DriveController: getAllFiles called");
        try {
            User currentUser = getCurrentUser();
            List<DriveFileDTO> files = driveFileService.getFilesByUser(currentUser);
            log.info("DriveController: Retrieved {} files for user: {}", files.size(), currentUser.getUsername());
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("DriveController: Error getting all files", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get files by folder",
        description = "Retrieve files in a specific folder or root folder if folderId is null."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Files retrieved successfully",
            content = @Content(schema = @Schema(implementation = DriveFileDTO.class))
        )
    })
    @GetMapping("/files/folder")
    public ResponseEntity<List<DriveFileDTO>> getFilesByFolder(@RequestParam(required = false) Long folderId) {
        log.info("DriveController: getFilesByFolder called for folderId: {}", folderId);
        try {
            User currentUser = getCurrentUser();
            List<DriveFileDTO> files = driveFileService.getFilesByFolder(currentUser, folderId);
            log.info("DriveController: Retrieved {} files for folder: {}", files.size(), folderId);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("DriveController: Error getting files by folder: {}", folderId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get file by ID",
        description = "Retrieve a specific file by ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File retrieved successfully",
            content = @Content(schema = @Schema(implementation = DriveFileDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "File not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/files/{id}")
    public ResponseEntity<DriveFileDTO> getFileById(@PathVariable Long id) {
        log.info("DriveController: getFileById called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            Optional<DriveFileDTO> file = driveFileService.getFileById(currentUser, id);
            if (file.isPresent()) {
                log.info("DriveController: File found with ID: {}", id);
                return ResponseEntity.ok(file.get());
            } else {
                log.warn("DriveController: File not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("DriveController: Error getting file by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Upload a new file",
        description = "Upload a new file to the user's drive."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "File uploaded successfully",
            content = @Content(schema = @Schema(implementation = DriveFileDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or insufficient storage",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/files")
    public ResponseEntity<DriveFileDTO> uploadFile(@Valid @RequestBody CreateDriveFileRequest request) {
        log.info("DriveController: uploadFile called for name: {}", request.getName());
        try {
            User currentUser = getCurrentUser();
            DriveFileDTO file = driveFileService.createFile(currentUser, request);
            log.info("DriveController: File uploaded successfully with ID: {}", file.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(file);
        } catch (Exception e) {
            log.error("DriveController: Error uploading file", e);
            throw e;
        }
    }

    @Operation(
        summary = "Update file",
        description = "Update an existing file's information."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File updated successfully",
            content = @Content(schema = @Schema(implementation = DriveFileDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "File not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/files/{id}")
    public ResponseEntity<DriveFileDTO> updateFile(@PathVariable Long id, @Valid @RequestBody CreateDriveFileRequest request) {
        log.info("DriveController: updateFile called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            DriveFileDTO file = driveFileService.updateFile(currentUser, id, request);
            log.info("DriveController: File updated successfully with ID: {}", id);
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            log.error("DriveController: Error updating file with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete file",
        description = "Delete an existing file."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "File deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "File not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        log.info("DriveController: deleteFile called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            driveFileService.deleteFile(currentUser, id);
            log.info("DriveController: File deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DriveController: Error deleting file with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Download file",
        description = "Download a file by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File downloaded successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "File not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        log.info("DriveController: downloadFile called for id: {}", id);
        try {
            User currentUser = getCurrentUser();
            Optional<DriveFileDTO> fileOpt = driveFileService.getFileById(currentUser, id);
            
            if (fileOpt.isEmpty()) {
                log.warn("DriveController: File not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            DriveFileDTO file = fileOpt.get();
            
            // Record file access
            driveFileService.recordFileAccess(currentUser, id);
            
            // For now, return a simple response indicating the file
            // In a real implementation, you would read the file from storage (MinIO, filesystem, etc.)
            String fileContent = "File content for: " + file.getName();
            byte[] fileBytes = fileContent.getBytes();
            
            String fileName = file.getName() + (file.getFileExtension() != null ? "." + file.getFileExtension() : "");
            
            log.info("DriveController: File downloaded successfully for file ID: {}", id);
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", file.getMimeType() != null ? file.getMimeType() : "application/octet-stream")
                    .body(fileBytes);
                    
        } catch (Exception e) {
            log.error("DriveController: Error downloading file with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get files by type",
        description = "Retrieve files filtered by MIME type pattern."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Files retrieved successfully",
            content = @Content(schema = @Schema(implementation = DriveFileDTO.class))
        )
    })
    @GetMapping("/files/type/{mimeType}")
    public ResponseEntity<List<DriveFileDTO>> getFilesByType(@PathVariable String mimeType) {
        log.info("DriveController: getFilesByType called for mimeType: {}", mimeType);
        try {
            User currentUser = getCurrentUser();
            List<DriveFileDTO> files = driveFileService.getFilesByType(currentUser, mimeType);
            log.info("DriveController: Retrieved {} files for mimeType: {}", files.size(), mimeType);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("DriveController: Error getting files by type: {}", mimeType, e);
            throw e;
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsernameWithCompany(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
} 
