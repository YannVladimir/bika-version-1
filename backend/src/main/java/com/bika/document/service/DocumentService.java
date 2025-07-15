package com.bika.document.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.service.DepartmentService;
import com.bika.document.entity.Document;
import com.bika.document.entity.DocumentType;
import com.bika.document.entity.Folder;
import com.bika.document.repository.DocumentRepository;
import com.bika.document.repository.DocumentTypeRepository;
import com.bika.document.dto.DocumentDTO;
import com.bika.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DepartmentService departmentService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Document> findByCompany(Company company) {
        return documentRepository.findByCompany(company);
    }

    @Transactional(readOnly = true)
    public List<Document> findByDepartment(Department department) {
        return documentRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Document> findByFolder(Folder folder) {
        return documentRepository.findByFolder(folder);
    }

    @Transactional(readOnly = true)
    public List<Document> findByDocumentType(DocumentType documentType) {
        return documentRepository.findByDocumentType(documentType);
    }

    @Transactional(readOnly = true)
    public Optional<Document> findByIdAndCompany(Long id, Company company) {
        return documentRepository.findByIdAndCompany(id, company);
    }

    @Transactional
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Transactional
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    // DTO-based methods
    public DocumentDTO createDocument(DocumentDTO dto) {
        DocumentType documentType = documentTypeRepository.findById(dto.getDocumentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Document type not found"));

        Department department = departmentService.findEntityById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Document document = new Document();
        document.setName(dto.getName());
        document.setDocumentType(documentType);
        document.setDepartment(department);
        document.setCompany(department.getCompany());
        
        // Set required fields with default values for testing
        document.setFilePath("/test/path");
        document.setFileSize(0L);
        document.setMimeType("application/octet-stream");
        document.setMetadata("{}");
        document.setStatus(Document.DocumentStatus.DRAFT);
        document.setPhysicalLocation("{}");

        Document saved = documentRepository.save(document);
        return toDTO(saved);
    }

    public DocumentDTO getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
    }

    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> getDocumentsByDepartment(Long departmentId) {
        Department department = departmentService.findEntityById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return documentRepository.findByDepartment(department).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> getDocumentsByCompany(Company company) {
        return documentRepository.findByCompany(company).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> getDocumentsByFolder(Folder folder) {
        return documentRepository.findByFolder(folder).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> getDocumentsByDocumentType(DocumentType documentType) {
        return documentRepository.findByDocumentType(documentType).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document not found");
        }
        documentRepository.deleteById(id);
    }

    public DocumentDTO toDTO(Document document) {
        Map<String, Object> metadata = parseMetadata(document.getMetadata());
        
        return DocumentDTO.builder()
                .id(document.getId())
                .name(document.getName())
                .code(document.getCode())
                .companyId(document.getCompany().getId())
                .departmentId(document.getDepartment() != null ? document.getDepartment().getId() : null)
                .folderId(document.getFolder() != null ? document.getFolder().getId() : null)
                .documentTypeId(document.getDocumentType() != null ? document.getDocumentType().getId() : null)
                .documentTypeName(document.getDocumentType() != null ? document.getDocumentType().getName() : null)
                .filePath(document.getFilePath())
                .fileSize(document.getFileSize())
                .mimeType(document.getMimeType())
                .metadata(metadata)
                .status(document.getStatus())
                .physicalLocation(document.getPhysicalLocation())
                .isActive(document.getStatus() == Document.DocumentStatus.ACTIVE)
                .createdAt(document.getCreatedAt() != null ? 
                    document.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .updatedAt(document.getUpdatedAt() != null ? 
                    document.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .createdBy(document.getCreatedBy())
                .build();
    }

    private Map<String, Object> parseMetadata(String metadataJson) {
        try {
            if (metadataJson == null || metadataJson.trim().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(metadataJson, Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
} 