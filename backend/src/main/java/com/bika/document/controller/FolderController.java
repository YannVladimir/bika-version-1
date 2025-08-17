package com.bika.document.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.document.service.FolderService;
import com.bika.document.service.DocumentService;
import com.bika.document.dto.FolderDTO;
import com.bika.document.dto.DocumentDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/folders")
@RequiredArgsConstructor
@Tag(name = "Folder", description = "Folder management APIs for document archival")
@Slf4j
public class FolderController {

    private final FolderService folderService;
    private final DocumentService documentService;

    @Operation(
        summary = "Get root folders by company",
        description = "Retrieve all root folders for a specific company."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Folders retrieved successfully",
            content = @Content(schema = @Schema(implementation = FolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/company/{companyId}/root")
    public ResponseEntity<List<FolderDTO>> getRootFoldersByCompany(@PathVariable Long companyId) {
        log.info("FolderController: getRootFoldersByCompany called for companyId: {}", companyId);
        try {
            List<FolderDTO> folders = folderService.getRootFoldersByCompany(companyId);
            log.info("FolderController: Retrieved {} root folders for company {}", folders.size(), companyId);
            return ResponseEntity.ok(folders);
        } catch (Exception e) {
            log.error("FolderController: Error getting root folders by company: {}", companyId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get folder by ID with contents",
        description = "Retrieve a folder by its ID including child folders and documents."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Folder retrieved successfully",
            content = @Content(schema = @Schema(implementation = FolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Folder not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/contents")
    public ResponseEntity<FolderDTO> getFolderContents(@PathVariable Long id) {
        log.info("FolderController: getFolderContents called for id: {}", id);
        try {
        return folderService.findDTOById(id)
                    .map(folder -> {
                        // Populate documents for this folder
                        List<DocumentDTO> documents = folderService.findById(id)
                                .map(documentService::getDocumentsByFolder)
                                .orElseThrow(() -> new RuntimeException("Folder not found"));
                        folder.setDocuments(documents);
                        
                        log.info("FolderController: Folder found with ID: {} containing {} documents", id, documents.size());
                        return ResponseEntity.ok(folder);
                    })
                    .orElseGet(() -> {
                        log.warn("FolderController: Folder not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("FolderController: Error getting folder contents by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Create a new folder",
        description = "Create a new folder in the specified company and parent folder."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Folder created successfully",
            content = @Content(schema = @Schema(implementation = FolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<FolderDTO> createFolder(@Valid @RequestBody FolderDTO folderDTO) {
        log.info("FolderController: createFolder called for name: {}", folderDTO.getName());
        try {
            FolderDTO result = folderService.createFolder(folderDTO);
            log.info("FolderController: Folder created successfully with ID: {}", result.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("FolderController: Error creating folder", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get folder by ID",
        description = "Retrieve a folder by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Folder retrieved successfully",
            content = @Content(schema = @Schema(implementation = FolderDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Folder not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<FolderDTO> getFolderById(@PathVariable Long id) {
        log.info("FolderController: getFolderById called for id: {}", id);
        try {
            return folderService.findDTOById(id)
                    .map(folder -> {
                        log.info("FolderController: Folder found with ID: {}", id);
                        return ResponseEntity.ok(folder);
                    })
                    .orElseGet(() -> {
                        log.warn("FolderController: Folder not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("FolderController: Error getting folder by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete folder",
        description = "Delete a folder by its ID."
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        log.info("FolderController: deleteFolder called for id: {}", id);
        try {
            folderService.deleteById(id);
            log.info("FolderController: Folder deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("FolderController: Error deleting folder with id: {}", id, e);
            throw e;
        }
    }
} 
