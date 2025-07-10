package com.bika.project.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.config.TestConfig;
import com.bika.config.TestAuditorAwareConfig;
import com.bika.project.dto.ProjectDTO;
import com.bika.project.entity.Project;
import com.bika.project.repository.ProjectRepository;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestConfig.class, TestAuditorAwareConfig.class})
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "logging.level.com.bika=DEBUG"
})
public class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext context;

    private Company testCompany;
    private Department testDepartment;
    private User testUser;
    private Project testProject;
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
                .build();
        testCompany = companyRepository.save(testCompany);

        // Create test department
        testDepartment = Department.builder()
                .name("Test Department " + uniqueSuffix)
                .code("TD" + uniqueSuffix)
                .company(testCompany)
                .active(true)
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        // Create test user
        testUser = User.builder()
                .username("testuser" + uniqueSuffix)
                .email("testuser" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .role(UserRole.ADMIN)
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .build();
        testUser = userRepository.save(testUser);

        // Create test project
        testProject = Project.builder()
                .name("Test Project " + uniqueSuffix)
                .description("Test Description " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .isActive(true)
                .code("TEST-PROJ-" + uniqueSuffix)
                .status(Project.ProjectStatus.PLANNING)
                .build();
        testProject = projectRepository.save(testProject);
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProject_ShouldSucceed_WhenAdmin() throws Exception {
        ProjectDTO newProjectDTO = ProjectDTO.builder()
                .name("New Project " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .isActive(true)
                .code("NEW-PROJ-" + uniqueSuffix)
                .status("PLANNING")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProjectDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Project " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createProject_ShouldSucceed_WhenManager() throws Exception {
        ProjectDTO newProjectDTO = ProjectDTO.builder()
                .name("New Project " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .isActive(true)
                .code("NEW-PROJ-" + uniqueSuffix)
                .status("PLANNING")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProjectDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Project " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProject_ShouldFail_WhenEmployee() throws Exception {
        ProjectDTO newProjectDTO = ProjectDTO.builder()
                .name("New Project " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .isActive(true)
                .code("NEW-PROJ-" + uniqueSuffix)
                .status("PLANNING")
                .build();

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProjectDTO)))
                .andExpect(status().isForbidden()); // Employee should not be able to create projects
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProjects_ShouldReturnAllProjects() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProjectById_ShouldReturnProject_WhenExists() throws Exception {
        mockMvc.perform(get("/api/v1/projects/" + testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProjectById_ShouldThrowException_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/projects/999999"))
                .andExpect(status().isNotFound());
    }
} 