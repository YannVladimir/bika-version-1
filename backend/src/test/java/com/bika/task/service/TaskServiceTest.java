package com.bika.task.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.common.exception.ResourceNotFoundException;
import com.bika.project.entity.Project;
import com.bika.project.repository.ProjectRepository;
import com.bika.task.dto.TaskDTO;
import com.bika.task.entity.Task;
import com.bika.task.repository.TaskRepository;
import com.bika.user.entity.User;
import com.bika.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private User testUser;
    private User testAssignedUser;
    private Company testCompany;
    private Department testDepartment;
    private Project testProject;
    private TaskDTO testTaskDTO;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(1L)
                .name("Test Company")
                .code("TEST")
                .active(true)
                .build();

        testDepartment = Department.builder()
                .id(1L)
                .name("Test Department")
                .code("DEPT")
                .company(testCompany)
                .active(true)
                .build();

        testProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test Description")
                .company(testCompany)
                .department(testDepartment)
                .isActive(true)
                .code("PROJ")
                .status(Project.ProjectStatus.PLANNING)
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .build();

        testAssignedUser = User.builder()
                .id(2L)
                .username("assigneduser")
                .email("assigned@example.com")
                .password("password")
                .firstName("Assigned")
                .lastName("User")
                .company(testCompany)
                .department(testDepartment)
                .active(true)
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(Task.TaskStatus.TODO)
                .priority(Task.TaskPriority.MEDIUM)
                .company(testCompany)
                .department(testDepartment)
                .project(testProject)
                .assignedTo(testAssignedUser)
                .creator(testUser)
                .dueDate(LocalDateTime.now().plusDays(7))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTaskDTO = TaskDTO.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status("TODO")
                .priority("MEDIUM")
                .companyId(1L)
                .departmentId(1L)
                .projectId(1L)
                .assignedToId(2L)
                .dueDate(LocalDateTime.now().plusDays(7))
                .isActive(true)
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        // When
        List<TaskDTO> result = taskService.getAllTasks();

        // Then
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void getTasksByCompany_ShouldReturnTasksForCompany() {
        // Given
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(taskRepository.findByCompany(testCompany)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> result = taskService.getTasksByCompany(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testCompany.getId(), result.get(0).getCompanyId());
        verify(companyRepository).findById(1L);
        verify(taskRepository).findByCompany(testCompany);
    }

    @Test
    void getTasksByCompany_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTasksByCompany(999L));
        verify(companyRepository).findById(999L);
    }

    @Test
    void getTasksByDepartment_ShouldReturnTasksForDepartment() {
        // Given
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(taskRepository.findByDepartment(testDepartment)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> result = taskService.getTasksByDepartment(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testDepartment.getId(), result.get(0).getDepartmentId());
        verify(departmentRepository).findById(1L);
        verify(taskRepository).findByDepartment(testDepartment);
    }

    @Test
    void getTasksByProject_ShouldReturnTasksForProject() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProject(testProject)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> result = taskService.getTasksByProject(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProject.getId(), result.get(0).getProjectId());
        verify(projectRepository).findById(1L);
        verify(taskRepository).findByProject(testProject);
    }

    @Test
    void getTasksByUser_ShouldReturnTasksForUser() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAssignedUser));
        when(taskRepository.findByAssignedTo(testAssignedUser)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> result = taskService.getTasksByUser(2L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testAssignedUser.getId(), result.get(0).getAssignedToId());
        verify(userRepository).findById(2L);
        verify(taskRepository).findByAssignedTo(testAssignedUser);
    }

    @Test
    void getMyTasks_ShouldReturnCurrentUserTasks() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("assigned@example.com");
        when(userRepository.findByEmail("assigned@example.com")).thenReturn(Optional.of(testAssignedUser));
        when(taskRepository.findByAssignedTo(testAssignedUser)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> result = taskService.getMyTasks();

        // Then
        assertEquals(1, result.size());
        assertEquals(testAssignedUser.getId(), result.get(0).getAssignedToId());
        verify(userRepository).findByEmail("assigned@example.com");
        verify(taskRepository).findByAssignedTo(testAssignedUser);
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskDTO result = taskService.getTaskById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));
        verify(taskRepository).findById(999L);
    }

    @Test
    void createTask_ShouldCreateAndReturnTask() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAssignedUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.createTask(testTaskDTO);

        // Then
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatus() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.updateTaskStatus(1L, "IN_PROGRESS");

        // Then
        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_ShouldSetCompletedDate_WhenStatusIsCompleted() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.updateTaskStatus(1L, "COMPLETED");

        // Then
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertNotNull(result.getCompletedDate());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void assignTask_ShouldAssignTaskToUser() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAssignedUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.assignTask(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(testAssignedUser.getId(), result.getAssignedToId());
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).existsById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        // Given
        when(taskRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(999L));
        verify(taskRepository).existsById(999L);
        verify(taskRepository, never()).deleteById(any());
    }
} 