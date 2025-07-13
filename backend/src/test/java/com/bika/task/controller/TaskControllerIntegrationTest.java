package com.bika.task.controller;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.config.TestConfig;
import com.bika.config.TestAuditorAwareConfig;
import com.bika.project.entity.Project;
import com.bika.project.repository.ProjectRepository;
import com.bika.task.repository.TaskRepository;
import com.bika.task.dto.TaskDTO;
import com.bika.task.entity.Task;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestConfig.class, TestAuditorAwareConfig.class})
public class TaskControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private String uniqueSuffix;
    private Company testCompany;
    private Department testDepartment;
    private Project testProject;
    private User testUser;
    private User testAdmin;

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

        // Create test project
        testProject = Project.builder()
                .name("Test Project " + uniqueSuffix)
                .description("Test Description " + uniqueSuffix)
                .company(testCompany)
                .department(testDepartment)
                .isActive(true)
                .code("PROJ-" + uniqueSuffix)
                .status(Project.ProjectStatus.PLANNING)
                .startDate(java.time.LocalDateTime.now())
                .endDate(java.time.LocalDateTime.now().plusDays(30))
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testProject = projectRepository.save(testProject);

        // Create test user
        testUser = User.builder()
                .username("testuser" + uniqueSuffix)
                .email("testuser" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
        testUser = userRepository.save(testUser);

        // Create test admin
        testAdmin = User.builder()
                .username("testadmin" + uniqueSuffix)
                .email("testadmin" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("Admin")
                .role(UserRole.COMPANY_ADMIN)
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
        // Delete in correct order to avoid foreign key constraint violations
        taskRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTasks_ShouldSucceed_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllTasks_ShouldSucceed_WhenManager() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTasks_ShouldFail_WhenUser() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isForbidden()); // User should not have access to all tasks
    }

    @Test
    @WithMockUser(username = "testuser")
    void getMyTasks_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/my-tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByCompany_ShouldSucceed_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/company/" + testCompany.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByDepartment_ShouldSucceed_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/department/" + testDepartment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByProject_ShouldSucceed_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/project/" + testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByUser_ShouldSucceed_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByStatus_ShouldSucceed_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/status/TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTask_ShouldSucceed_WhenAdmin() throws Exception {
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("New Task " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Task " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createTask_ShouldSucceed_WhenManager() throws Exception {
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("New Task " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Task " + uniqueSuffix));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTask_ShouldFail_WhenUser() throws Exception {
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("New Task " + uniqueSuffix)
                .description("New Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isForbidden()); // User should not be able to create tasks
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskStatus_ShouldSucceed() throws Exception {
        // First create a task
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("Task to Update " + uniqueSuffix)
                .description("Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        String response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract task ID from response
        Map<String, Object> taskResponse = objectMapper.readValue(response, Map.class);
        Long taskId = Long.valueOf(taskResponse.get("id").toString());

        // Update task status
        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/status")
                .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignTask_ShouldSucceed_WhenAdmin() throws Exception {
        // First create a task
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("Task to Assign " + uniqueSuffix)
                .description("Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        String response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract task ID from response
        Map<String, Object> taskResponse = objectMapper.readValue(response, Map.class);
        Long taskId = Long.valueOf(taskResponse.get("id").toString());

        // Assign task to user
        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/assign/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedToId").exists());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void assignTask_ShouldSucceed_WhenManager() throws Exception {
        // First create a task
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("Task to Assign " + uniqueSuffix)
                .description("Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        String response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract task ID from response
        Map<String, Object> taskResponse = objectMapper.readValue(response, Map.class);
        Long taskId = Long.valueOf(taskResponse.get("id").toString());

        // Assign task to user
        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/assign/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedToId").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void assignTask_ShouldFail_WhenUser() throws Exception {
        // First create a task
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("Task to Assign " + uniqueSuffix)
                .description("Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        // User should not be able to create tasks, so this should fail
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isForbidden()); // User should not be able to create tasks
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTask_ShouldSucceed_WhenAdmin() throws Exception {
        // First create a task
        TaskDTO newTaskDTO = TaskDTO.builder()
                .title("Task to Delete " + uniqueSuffix)
                .description("Description " + uniqueSuffix)
                .companyId(testCompany.getId())
                .departmentId(testDepartment.getId())
                .projectId(testProject.getId())
                .priority(Task.TaskPriority.MEDIUM.name())
                .status(Task.TaskStatus.TODO.name())
                .assignedToId(testUser.getId())
                .createdById(testAdmin.getId())
                .isActive(true)
                .build();

        String response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract task ID from response
        Map<String, Object> taskResponse = objectMapper.readValue(response, Map.class);
        Long taskId = Long.valueOf(taskResponse.get("id").toString());

        // Delete task
        mockMvc.perform(delete("/api/v1/tasks/" + taskId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteTask_ShouldFail_WhenManager() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isForbidden()); // Manager should not be able to delete tasks
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByCompany_ShouldFail_WhenCompanyNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/company/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByDepartment_ShouldFail_WhenDepartmentNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/department/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByProject_ShouldFail_WhenProjectNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/project/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTasksByUser_ShouldFail_WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/user/999999"))
                .andExpect(status().isNotFound());
    }
} 