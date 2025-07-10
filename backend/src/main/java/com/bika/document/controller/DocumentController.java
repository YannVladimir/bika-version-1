package com.bika.document.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.document.dto.CreateDocumentRequest;
import com.bika.document.dto.DocumentDTO;
import com.bika.document.entity.Document;
import com.bika.document.entity.DocumentType;
import com.bika.document.entity.Folder;
import com.bika.document.repository.DocumentTypeRepository;
import com.bika.document.repository.FolderRepository;
import com.bika.document.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final FolderRepository folderRepository;

    @Autowired
    public DocumentController(DocumentService documentService, 
                            CompanyRepository companyRepository,
                            DepartmentRepository departmentRepository,
                            DocumentTypeRepository documentTypeRepository,
                            FolderRepository folderRepository) {
        this.documentService = documentService;
        this.companyRepository = companyRepository;
        this.departmentRepository = departmentRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.folderRepository = folderRepository;
    }

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        return documentService.findById(id)
                .map(documentService::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByCompany(@PathVariable Long companyId) {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isPresent()) {
            return ResponseEntity.ok(documentService.getDocumentsByCompany(companyOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByDepartment(@PathVariable Long departmentId) {
        Optional<Department> departmentOpt = departmentRepository.findById(departmentId);
        if (departmentOpt.isPresent()) {
            return ResponseEntity.ok(documentService.getDocumentsByDepartment(departmentOpt.get().getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByFolder(@PathVariable Long folderId) {
        Optional<Folder> folderOpt = folderRepository.findById(folderId);
        if (folderOpt.isPresent()) {
            return ResponseEntity.ok(documentService.getDocumentsByFolder(folderOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByType(@PathVariable Long typeId) {
        Optional<DocumentType> documentTypeOpt = documentTypeRepository.findById(typeId);
        if (documentTypeOpt.isPresent()) {
            return ResponseEntity.ok(documentService.getDocumentsByDocumentType(documentTypeOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/company/{companyId}/document/{id}")
    public ResponseEntity<DocumentDTO> getDocumentByIdAndCompany(
            @PathVariable Long companyId,
            @PathVariable Long id) {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isPresent()) {
            Optional<Document> document = documentService.findByIdAndCompany(id, companyOpt.get());
            return document.map(documentService::toDTO)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        try {
            System.out.println("Creating document with request: " + request);
            
            // Find the related entities by ID
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));
            
            Department department = null;
            if (request.getDepartmentId() != null) {
                department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));
            }
            
            DocumentType documentType = documentTypeRepository.findById(request.getDocumentTypeId())
                    .orElseThrow(() -> new RuntimeException("Document type not found with id: " + request.getDocumentTypeId()));
            
            Folder folder = null;
            if (request.getFolderId() != null) {
                folder = folderRepository.findById(request.getFolderId())
                        .orElseThrow(() -> new RuntimeException("Folder not found with id: " + request.getFolderId()));
            }
            
            System.out.println("Found entities - Company: " + company.getId() + ", Department: " + (department != null ? department.getId() : "null") + ", DocumentType: " + documentType.getId() + ", Folder: " + (folder != null ? folder.getId() : "null"));
            
            // Create the document entity
            Document document = Document.builder()
                    .name(request.getName())
                    .company(company)
                    .department(department)
                    .documentType(documentType)
                    .folder(folder)
                    .filePath(request.getFilePath())
                    .fileSize(request.getFileSize())
                    .mimeType(request.getMimeType())
                    .metadata(request.getMetadata())
                    .status(request.getStatus())
                    .physicalLocation(request.getPhysicalLocation())
                    .createdBy("system")
                    .updatedBy("system")
                    .build();
            
            System.out.println("Created document entity: " + document.getName());
            
            Document savedDocument = documentService.save(document);
            System.out.println("Saved document with id: " + savedDocument.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(documentService.toDTO(savedDocument));
        } catch (Exception e) {
            System.err.println("Error creating document: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentDTO documentDTO) {
        Optional<Document> existingDocument = documentService.findById(id);
        if (existingDocument.isPresent()) {
            Document document = existingDocument.get();
            document.setName(documentDTO.getName());
            // Only update fields that exist in DocumentDTO
            document.setStatus(documentDTO.getIsActive() != null && documentDTO.getIsActive() ? Document.DocumentStatus.ACTIVE : Document.DocumentStatus.DRAFT);
            Document savedDocument = documentService.save(document);
            return ResponseEntity.ok(documentService.toDTO(savedDocument));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        Optional<Document> document = documentService.findById(id);
        if (document.isPresent()) {
            documentService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DocumentDTO> updateDocumentStatus(
            @PathVariable Long id,
            @RequestParam Document.DocumentStatus status) {
        Optional<Document> documentOpt = documentService.findById(id);
        if (documentOpt.isPresent()) {
            Document existingDocument = documentOpt.get();
            existingDocument.setStatus(status);
            Document savedDocument = documentService.save(existingDocument);
            return ResponseEntity.ok(documentService.toDTO(savedDocument));
        }
        return ResponseEntity.notFound().build();
    }
} 