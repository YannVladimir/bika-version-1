package com.bika.document.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.document.dto.CreateDocumentRequest;
import com.bika.document.dto.DocumentDTO;
import com.bika.document.dto.PhysicalStorageLookupDTO;
import com.bika.document.entity.Document;
import com.bika.document.entity.DocumentType;
import com.bika.document.entity.Folder;
import com.bika.document.repository.DocumentTypeRepository;
import com.bika.document.repository.FolderRepository;
import com.bika.document.service.DocumentService;
import com.bika.document.service.PhysicalStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

@RestController
@RequestMapping("/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document", description = "Document management APIs for archival system")
@Slf4j
public class DocumentController {

    private final DocumentService documentService;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final FolderRepository folderRepository;
    private final ObjectMapper objectMapper;
    private final PhysicalStorageService physicalStorageService;

    @Operation(
        summary = "Get documents by folder",
        description = "Retrieve all documents in a specific folder."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Folder not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByFolder(@PathVariable Long folderId) {
        log.info("DocumentController: getDocumentsByFolder called for folderId: {}", folderId);
        try {
            Optional<Folder> folderOpt = folderRepository.findById(folderId);
            if (folderOpt.isPresent()) {
                List<DocumentDTO> documents = documentService.getDocumentsByFolder(folderOpt.get());
                log.info("DocumentController: Retrieved {} documents for folder {}", documents.size(), folderId);
                return ResponseEntity.ok(documents);
            }
            log.warn("DocumentController: Folder not found with ID: {}", folderId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("DocumentController: Error getting documents by folder: {}", folderId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get documents by company",
        description = "Retrieve all documents for a specific company."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByCompany(@PathVariable Long companyId) {
        log.info("DocumentController: getDocumentsByCompany called for companyId: {}", companyId);
        try {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isPresent()) {
                List<DocumentDTO> documents = documentService.getDocumentsByCompany(companyOpt.get());
                log.info("DocumentController: Retrieved {} documents for company {}", documents.size(), companyId);
                return ResponseEntity.ok(documents);
        }
            log.warn("DocumentController: Company not found with ID: {}", companyId);
        return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("DocumentController: Error getting documents by company: {}", companyId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get document by ID",
        description = "Retrieve a document by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        log.info("DocumentController: getDocumentById called for id: {}", id);
        try {
            return documentService.findById(id)
                    .map(documentService::toDTO)
                    .map(document -> {
                        log.info("DocumentController: Document found with ID: {}", id);
                        return ResponseEntity.ok(document);
                    })
                    .orElseGet(() -> {
                        log.warn("DocumentController: Document not found with ID: {}", id);
        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("DocumentController: Error getting document by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Create a new document",
        description = "Create a new document with dynamic field values and file attachment."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Document created successfully",
            content = @Content(schema = @Schema(implementation = DocumentDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        log.info("DocumentController: createDocument called for name: {}", request.getName());
        try {
            // Find the related entities by ID
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));
            
            Department department = null;
            if (request.getDepartmentId() != null) {
                department = departmentRepository.findById(request.getDepartmentId())
                        .orElse(null);
            }
            
            DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                    .orElseThrow(() -> new RuntimeException("Document type not found with id: " + request.getDocumentTypeId()));
            
            Folder folder = null;
            if (request.getFolderId() != null) {
                folder = folderRepository.findById(request.getFolderId())
                        .orElse(null);
            }
            
            // Convert field values to JSON metadata
            String metadata = "{}";
            if (request.getFieldValues() != null && !request.getFieldValues().isEmpty()) {
                try {
                    metadata = objectMapper.writeValueAsString(request.getFieldValues());
                } catch (Exception e) {
                    log.error("DocumentController: Error converting field values to JSON", e);
                    throw new RuntimeException("Error processing field values", e);
                }
            }
            
            log.info("DocumentController: Creating document with metadata: {}", metadata);
            
            // Create the document entity
            Document document = Document.builder()
                    .name(request.getName())
                    .code(request.getCode())
                    .company(company)
                    .department(department)
                    .documentType(documentType)
                    .folder(folder)
                    .filePath(request.getFilePath())
                    .fileSize(request.getFileSize())
                    .mimeType(request.getMimeType())
                    .metadata(metadata)
                    .status(request.getStatus() != null ? request.getStatus() : Document.DocumentStatus.DRAFT)
                    .physicalLocation(request.getPhysicalLocation())
                    .createdBy("system") // TODO: Get from security context
                    .updatedBy("system")
                    .build();
            
            Document savedDocument = documentService.save(document);
            log.info("DocumentController: Document created successfully with ID: {}", savedDocument.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(documentService.toDTO(savedDocument));
        } catch (Exception e) {
            log.error("DocumentController: Error creating document", e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete document",
        description = "Delete a document by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Document deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.info("DocumentController: deleteDocument called for id: {}", id);
        try {
            documentService.deleteById(id);
            log.info("DocumentController: Document deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DocumentController: Error deleting document with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Download document file",
        description = "Download the file attachment of a document by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File downloaded successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Document or file not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        log.info("DocumentController: downloadDocument called for id: {}", id);
        try {
            Document document = documentService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
            
            if (document.getFilePath() == null || document.getFilePath().isEmpty()) {
                log.warn("DocumentController: No file attached to document with ID: {}", id);
        return ResponseEntity.notFound().build();
    }

            // For now, return a simple response indicating the file path
            // In a real implementation, you would read the file from storage (MinIO, filesystem, etc.)
            String fileContent = "File content for: " + document.getName();
            byte[] fileBytes = fileContent.getBytes();
            
            String fileName = document.getName() + getFileExtension(document.getMimeType());
            
            log.info("DocumentController: File downloaded successfully for document ID: {}", id);
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", document.getMimeType() != null ? document.getMimeType() : "application/octet-stream")
                    .body(fileBytes);
                    
        } catch (Exception e) {
            log.error("DocumentController: Error downloading document with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get physical storage lookup data",
        description = "Get standardized options for physical storage fields like rooms, colors, etc."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lookup data retrieved successfully",
            content = @Content(schema = @Schema(implementation = PhysicalStorageLookupDTO.class))
        )
    })
    @GetMapping("/physical-storage-lookup")
    public ResponseEntity<PhysicalStorageLookupDTO> getPhysicalStorageLookup() {
        log.info("DocumentController: getPhysicalStorageLookup called");
        try {
            PhysicalStorageLookupDTO lookupData = physicalStorageService.getPhysicalStorageLookup();
            return ResponseEntity.ok(lookupData);
        } catch (Exception e) {
            log.error("DocumentController: Error getting physical storage lookup data", e);
            throw e;
        }
    }
    
    private String getFileExtension(String mimeType) {
        if (mimeType == null) return "";
        switch (mimeType) {
            case "application/pdf": return ".pdf";
            case "application/msword": return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return ".docx";
            case "application/vnd.ms-excel": return ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": return ".xlsx";
            case "image/jpeg": return ".jpg";
            case "image/png": return ".png";
            case "text/plain": return ".txt";
            default: return "";
        }
    }
} 
