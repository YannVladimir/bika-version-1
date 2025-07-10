package com.bika.document.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.config.TestAuditorAwareConfig;
import com.bika.config.TestConfig;
import com.bika.document.dto.CreateDocumentRequest;
import com.bika.document.entity.Document;
import com.bika.document.entity.DocumentType;
import com.bika.document.entity.Folder;
import com.bika.document.repository.DocumentRepository;
import com.bika.document.repository.DocumentTypeRepository;
import com.bika.document.repository.FolderRepository;
import com.bika.document.service.DocumentService;
import com.bika.user.entity.User;
import com.bika.user.entity.UserRole;
import com.bika.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestConfig.class, TestAuditorAwareConfig.class})
public class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext context;

    private Company testCompany;
    private Department testDepartment;
    private DocumentType testDocumentType;
    private Folder testFolder;
    private Document testDocument;
    private User testAdmin;
    private String uniqueSuffix;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        // Create test company
        testCompany = Company.builder()
                .name("Test Company " + uniqueSuffix)
                .code("TC" + uniqueSuffix)
                .address("Test Address")
                .phone("1234567890")
                .email("test@company.com")
                .active(true)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testCompany = companyRepository.save(testCompany);

        // Create test department
        testDepartment = Department.builder()
                .name("Test Department " + uniqueSuffix)
                .code("TD" + uniqueSuffix)
                .company(testCompany)
                .active(true)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        // Create test document type
        testDocumentType = DocumentType.builder()
                .name("Test Document Type " + uniqueSuffix)
                .code("TDT" + uniqueSuffix)
                .description("Test Document Type Description")
                .company(testCompany)
                .metadataSchema("{\"properties\":{\"title\":{\"type\":\"string\"}}}")
                .active(true)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testDocumentType = documentTypeRepository.save(testDocumentType);

        // Create test folder
        testFolder = Folder.builder()
                .name("Test Folder " + uniqueSuffix)
                .path("/test/folder/" + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .isActive(true)
                .description("Test folder description")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testFolder = folderRepository.save(testFolder);

        // Create test document
        testDocument = Document.builder()
                .name("Test Document " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .documentType(testDocumentType)
                .folder(testFolder)
                .status(Document.DocumentStatus.DRAFT)
                .filePath("/test/path/" + uniqueSuffix)
                .mimeType("application/pdf")
                .fileSize(1024L)
                .metadata("{}")
                .physicalLocation("{}")
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testDocument = documentRepository.save(testDocument);

        // Create test admin user
        testAdmin = User.builder()
                .username("admin" + uniqueSuffix)
                .email("admin" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testAdmin = userRepository.save(testAdmin);
    }

    @AfterEach
    void tearDown() {
        documentRepository.deleteAll();
        folderRepository.deleteAll();
        documentTypeRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDocument_ShouldCreateNewDocument_WhenValidRequest() throws Exception {
        // Verify test data exists
        assert testCompany.getId() != null;
        assert testDepartment.getId() != null;
        assert testDocumentType.getId() != null;
        assert testFolder.getId() != null;
        
        CreateDocumentRequest documentRequest = CreateDocumentRequest.builder()
                .name("New Document " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .documentTypeId(testDocumentType.getId())
                .folderId(testFolder.getId())
                .status(Document.DocumentStatus.DRAFT)
                .filePath("/new/path/" + uniqueSuffix)
                .mimeType("application/pdf")
                .fileSize(1024L)
                .metadata("{}")
                .physicalLocation("{}")
                .build();

        mockMvc.perform(post("/api/v1/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Document " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDocuments_ShouldReturnAllDocuments() throws Exception {
        mockMvc.perform(get("/api/v1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDocumentById_ShouldReturnDocument_WhenDocumentExists() throws Exception {
        mockMvc.perform(get("/api/v1/documents/" + testDocument.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getDocumentById_ShouldReturnNotFound_WhenDocumentDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/documents/999999"))
                .andExpect(status().isNotFound());
    }
}