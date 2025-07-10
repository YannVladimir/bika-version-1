package com.bika.document.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.config.TestConfig;
import com.bika.config.TestAuditorAwareConfig;
import com.bika.document.entity.Folder;
import com.bika.document.repository.FolderRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestConfig.class, TestAuditorAwareConfig.class})
public class FolderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext context;

    private Company testCompany;
    private Department testDepartment;
    private Folder testFolder;
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

        // Create test folder
        testFolder = Folder.builder()
                .name("Test Folder " + uniqueSuffix)
                .description("Test Description " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .path("/test/folder/" + uniqueSuffix)
                .isActive(true)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testFolder = folderRepository.save(testFolder);
    }

    @AfterEach
    void tearDown() {
        folderRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFolder_ShouldSucceed_WhenAdmin() throws Exception {
        Folder folderRequest = Folder.builder()
                .name("New Folder " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .path("/new/folder/" + uniqueSuffix)
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(folderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Folder " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createFolder_ShouldSucceed_WhenManager() throws Exception {
        Folder folderRequest = Folder.builder()
                .name("New Folder " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .path("/new/folder/" + uniqueSuffix)
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(folderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Folder " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createFolder_ShouldFail_WhenEmployee() throws Exception {
        Folder folderRequest = Folder.builder()
                .name("New Folder " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .path("/new/folder/" + uniqueSuffix)
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(folderRequest)))
                .andExpect(status().isForbidden()); // Employee should not be able to create folders
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllFolders_ShouldReturnAllFolders() throws Exception {
        mockMvc.perform(get("/api/v1/folders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFolderById_ShouldReturnFolder_WhenExists() throws Exception {
        mockMvc.perform(get("/api/v1/folders/" + testFolder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getFolderById_ShouldThrowException_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/folders/999999"))
                .andExpect(status().isNotFound());
    }
}