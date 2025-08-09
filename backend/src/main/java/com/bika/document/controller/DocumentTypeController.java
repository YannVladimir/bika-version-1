package com.bika.document.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.document.dto.DocumentTypeDTO;
import com.bika.document.service.DocumentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/document-types")
@RequiredArgsConstructor
@Tag(name = "Document Type", description = "Document type management APIs")
@Slf4j
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @Operation(
        summary = "Get all document types",
        description = "Retrieve a list of all document types."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document types retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<DocumentTypeDTO>> getAllDocumentTypes() {
        log.info("DocumentTypeController: getAllDocumentTypes called");
        try {
            List<DocumentTypeDTO> documentTypes = documentTypeService.getAllDocumentTypesDTO();
            log.info("DocumentTypeController: Retrieved {} document types successfully", documentTypes.size());
            return ResponseEntity.ok(documentTypes);
        } catch (Exception e) {
            log.error("DocumentTypeController: Error getting all document types", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get document types by company",
        description = "Retrieve all document types for a specific company."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document types retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<List<DocumentTypeDTO>> getDocumentTypesByCompany(@PathVariable Long companyId) {
        log.info("DocumentTypeController: getDocumentTypesByCompany called for companyId: {}", companyId);
        try {
            List<DocumentTypeDTO> documentTypes = documentTypeService.getDocumentTypesByCompanyDTO(companyId);
            log.info("DocumentTypeController: Retrieved {} document types for company {}", documentTypes.size(), companyId);
            return ResponseEntity.ok(documentTypes);
        } catch (Exception e) {
            log.error("DocumentTypeController: Error getting document types by company: {}", companyId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get document type by ID",
        description = "Retrieve a document type by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document type retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document type not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<DocumentTypeDTO> getDocumentTypeById(@PathVariable Long id) {
        log.info("DocumentTypeController: getDocumentTypeById called for id: {}", id);
        try {
            return documentTypeService.getDocumentTypeDTOById(id)
                    .map(documentType -> {
                        log.info("DocumentTypeController: Document type found with ID: {}", id);
                        return ResponseEntity.ok(documentType);
                    })
                    .orElseGet(() -> {
                        log.warn("DocumentTypeController: Document type not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("DocumentTypeController: Error getting document type by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Create a new document type",
        description = "Create a new document type with the provided details and fields."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Document type created successfully",
            content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DocumentTypeDTO> createDocumentType(@Valid @RequestBody DocumentTypeDTO documentTypeDTO) {
        log.info("DocumentTypeController: createDocumentType called for name: {}", documentTypeDTO.getName());
        try {
            DocumentTypeDTO result = documentTypeService.createDocumentType(documentTypeDTO);
            log.info("DocumentTypeController: Document type created successfully with ID: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("DocumentTypeController: Error creating document type", e);
            throw e;
        }
    }

    @Operation(
        summary = "Update document type",
        description = "Update an existing document type's details and fields."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document type updated successfully",
            content = @Content(schema = @Schema(implementation = DocumentTypeDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document type not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DocumentTypeDTO> updateDocumentType(
            @PathVariable Long id,
            @Valid @RequestBody DocumentTypeDTO documentTypeDTO) {
        log.info("DocumentTypeController: updateDocumentType called for id: {}", id);
        try {
            DocumentTypeDTO result = documentTypeService.updateDocumentType(id, documentTypeDTO);
            log.info("DocumentTypeController: Document type updated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("DocumentTypeController: Error updating document type with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete document type",
        description = "Delete a document type by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Document type deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document type not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteDocumentType(@PathVariable Long id) {
        log.info("DocumentTypeController: deleteDocumentType called for id: {}", id);
        try {
            documentTypeService.deleteById(id);
            log.info("DocumentTypeController: Document type deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DocumentTypeController: Error deleting document type with id: {}", id, e);
            throw e;
        }
    }
} 