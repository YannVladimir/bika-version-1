package com.bika.document.service;

import com.bika.company.dto.CompanyDTO;
import com.bika.company.dto.DepartmentDTO;
import com.bika.company.service.CompanyService;
import com.bika.company.service.DepartmentService;
import com.bika.config.TestConfig;
import com.bika.config.TestAuditorAwareConfig;
import com.bika.document.dto.DocumentDTO;
import com.bika.document.dto.DocumentTypeDTO;
import com.bika.document.entity.Document;
import com.bika.document.entity.DocumentType;
import com.bika.document.repository.DocumentRepository;
import com.bika.document.repository.DocumentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import({TestConfig.class, TestAuditorAwareConfig.class})
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentService departmentService;

    private CompanyDTO companyDTO;
    private DepartmentDTO departmentDTO;
    private DocumentTypeDTO documentTypeDTO;
    private DocumentDTO documentDTO;
    private String uniqueSuffix;

    @BeforeEach
    void setUp() {
        // Generate unique suffix for each test to avoid conflicts
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        
        // Create test company with unique values
        companyDTO = CompanyDTO.builder()
                .name("Test Company " + uniqueSuffix)
                .code("TEST" + uniqueSuffix)
                .email("company" + uniqueSuffix + "@test.com")
                .isActive(true)
                .build();
        companyDTO = companyService.createCompany(companyDTO);

        // Create test department with unique values
        departmentDTO = DepartmentDTO.builder()
                .name("Test Department " + uniqueSuffix)
                .code("TEST-DEPT" + uniqueSuffix)
                .companyId(companyDTO.getId())
                .isActive(true)
                .build();
        departmentDTO = departmentService.createDepartment(departmentDTO);

        // Create test document type with unique values
        documentTypeDTO = DocumentTypeDTO.builder()
                .name("Test Document Type " + uniqueSuffix)
                .code("TEST-DOC-TYPE" + uniqueSuffix)
                .companyId(companyDTO.getId())
                .isActive(true)
                .build();
        DocumentType documentType = DocumentType.builder()
                .name(documentTypeDTO.getName())
                .code(documentTypeDTO.getCode())
                .active(documentTypeDTO.getIsActive())
                .company(companyService.findEntityById(companyDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Company not found")))
                .metadataSchema("{\"type\": \"object\", \"properties\": {}}")
                .build();
        documentType = documentTypeRepository.save(documentType);
        documentTypeDTO.setId(documentType.getId());

        // Create test document with unique values
        documentDTO = DocumentDTO.builder()
                .name("Test Document " + uniqueSuffix)
                .documentTypeId(documentTypeDTO.getId())
                .departmentId(departmentDTO.getId())
                .isActive(true)
                .build();
    }

    @Test
    void createDocument_ShouldCreateNewDocument_WhenValidRequest() {
        DocumentDTO createdDocument = documentService.createDocument(documentDTO);
        assertNotNull(createdDocument);
        assertEquals(documentDTO.getName(), createdDocument.getName());
        assertEquals(documentDTO.getDocumentTypeId(), createdDocument.getDocumentTypeId());
        assertEquals(documentDTO.getDepartmentId(), createdDocument.getDepartmentId());
    }

    @Test
    void getDocumentById_ShouldReturnDocument_WhenDocumentExists() {
        DocumentDTO createdDocument = documentService.createDocument(documentDTO);
        DocumentDTO foundDocument = documentService.getDocumentById(createdDocument.getId());
        assertNotNull(foundDocument);
        assertEquals(createdDocument.getId(), foundDocument.getId());
    }

    @Test
    void getAllDocuments_ShouldReturnAllDocuments() {
        documentService.createDocument(documentDTO);
        List<DocumentDTO> documents = documentService.getAllDocuments();
        assertFalse(documents.isEmpty());
        assertEquals(1, documents.size());
    }

    @Test
    void getDocumentsByDepartment_ShouldReturnDocuments_WhenDepartmentExists() {
        DocumentDTO createdDocument = documentService.createDocument(documentDTO);
        List<DocumentDTO> documents = documentService.getDocumentsByDepartment(createdDocument.getDepartmentId());
        assertFalse(documents.isEmpty());
        assertEquals(1, documents.size());
        assertEquals(createdDocument.getId(), documents.get(0).getId());
    }

    @Test
    void deleteDocument_ShouldDeleteDocument_WhenDocumentExists() {
        DocumentDTO createdDocument = documentService.createDocument(documentDTO);
        documentService.deleteDocument(createdDocument.getId());
        assertTrue(documentRepository.findById(createdDocument.getId()).isEmpty());
    }
} 