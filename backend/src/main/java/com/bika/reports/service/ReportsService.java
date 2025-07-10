package com.bika.reports.service;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.common.exception.ResourceNotFoundException;
import com.bika.document.entity.Document;
import com.bika.document.repository.DocumentRepository;
import com.bika.project.entity.Project;
import com.bika.project.repository.ProjectRepository;
import com.bika.reports.dto.DashboardStatsDTO;
import com.bika.reports.dto.DocumentStatsDTO;
import com.bika.reports.dto.TaskStatsDTO;
import com.bika.reports.dto.UserStatsDTO;
import com.bika.task.entity.Task;
import com.bika.task.repository.TaskRepository;
import com.bika.user.entity.User;
import com.bika.user.entity.UserRole;
import com.bika.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

        long totalUsers = userRepository.count();
        long totalDocuments = documentRepository.count();
        long totalTasks = taskRepository.count();
        long totalProjects = projectRepository.count();
        
        long activeUsers = userRepository.findAll().stream()
                .filter(User::isActive)
                .count();
        
        long pendingTasks = taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.TODO)
                .count();
        
        long completedTasks = taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count();
        
        long overdueTasks = taskRepository.findAll().stream()
                .filter(task -> task.getDueDate() != null && 
                        task.getDueDate().isBefore(LocalDateTime.now()) && 
                        task.getStatus() != Task.TaskStatus.COMPLETED)
                .count();

        long documentsUploadedToday = documentRepository.findAll().stream()
                .filter(doc -> doc.getCreatedAt().toLocalDate().equals(today))
                .count();

        long tasksCompletedToday = taskRepository.findAll().stream()
                .filter(task -> task.getCompletedDate() != null && 
                        task.getCompletedDate().toLocalDate().equals(today))
                .count();

        long newUsersThisMonth = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .count();

        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalDocuments(totalDocuments)
                .totalTasks(totalTasks)
                .totalProjects(totalProjects)
                .activeUsers(activeUsers)
                .pendingTasks(pendingTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasks)
                .storageUsedGB(calculateStorageUsed())
                .storageTotalGB(100.0) // Example total storage
                .documentsUploadedToday(documentsUploadedToday)
                .tasksCompletedToday(tasksCompletedToday)
                .newUsersThisMonth(newUsersThisMonth)
                .build();
    }

    @Transactional(readOnly = true)
    public DocumentStatsDTO getDocumentStats(LocalDate fromDate, LocalDate toDate) {
        List<Document> documents = documentRepository.findAll();
        
        if (fromDate != null) {
            documents = documents.stream()
                    .filter(doc -> doc.getCreatedAt().toLocalDate().isAfter(fromDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (toDate != null) {
            documents = documents.stream()
                    .filter(doc -> doc.getCreatedAt().toLocalDate().isBefore(toDate.plusDays(1)))
                    .collect(Collectors.toList());
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

        long documentsThisMonth = documents.stream()
                .filter(doc -> doc.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .count();

        long documentsThisWeek = documents.stream()
                .filter(doc -> doc.getCreatedAt().toLocalDate().isAfter(startOfWeek.minusDays(1)))
                .count();

        long documentsToday = documents.stream()
                .filter(doc -> doc.getCreatedAt().toLocalDate().equals(today))
                .count();

        Map<String, Long> documentsByType = documents.stream()
                .collect(Collectors.groupingBy(
                        doc -> doc.getDocumentType() != null ? doc.getDocumentType().getName() : "Unknown",
                        Collectors.counting()
                ));

        Map<String, Long> documentsByStatus = documents.stream()
                .collect(Collectors.groupingBy(
                        doc -> doc.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> documentsByCompany = documents.stream()
                .filter(doc -> doc.getCompany() != null)
                .collect(Collectors.groupingBy(
                        doc -> doc.getCompany().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> documentsByDepartment = documents.stream()
                .filter(doc -> doc.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        doc -> doc.getDepartment().getName(),
                        Collectors.counting()
                ));

        double averageFileSizeMB = documents.stream()
                .mapToLong(doc -> doc.getFileSize() != null ? doc.getFileSize() : 0)
                .average()
                .orElse(0.0) / (1024 * 1024); // Convert to MB

        long totalStorageUsedMB = documents.stream()
                .mapToLong(doc -> doc.getFileSize() != null ? doc.getFileSize() : 0)
                .sum() / (1024 * 1024); // Convert to MB

        return DocumentStatsDTO.builder()
                .totalDocuments((long) documents.size())
                .documentsThisMonth(documentsThisMonth)
                .documentsThisWeek(documentsThisWeek)
                .documentsToday(documentsToday)
                .documentsByType(documentsByType)
                .documentsByStatus(documentsByStatus)
                .documentsByCompany(documentsByCompany)
                .documentsByDepartment(documentsByDepartment)
                .averageFileSizeMB(averageFileSizeMB)
                .totalStorageUsedMB(totalStorageUsedMB)
                .build();
    }

    @Transactional(readOnly = true)
    public TaskStatsDTO getTaskStats(LocalDate fromDate, LocalDate toDate) {
        List<Task> tasks = taskRepository.findAll();
        
        if (fromDate != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getCreatedAt().toLocalDate().isAfter(fromDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (toDate != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getCreatedAt().toLocalDate().isBefore(toDate.plusDays(1)))
                    .collect(Collectors.toList());
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

        long tasksThisMonth = tasks.stream()
                .filter(task -> task.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .count();

        long tasksThisWeek = tasks.stream()
                .filter(task -> task.getCreatedAt().toLocalDate().isAfter(startOfWeek.minusDays(1)))
                .count();

        long tasksToday = tasks.stream()
                .filter(task -> task.getCreatedAt().toLocalDate().equals(today))
                .count();

        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count();

        long pendingTasks = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.TODO)
                .count();

        long overdueTasks = tasks.stream()
                .filter(task -> task.getDueDate() != null && 
                        task.getDueDate().isBefore(LocalDateTime.now()) && 
                        task.getStatus() != Task.TaskStatus.COMPLETED)
                .count();

        Map<String, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByPriority = tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getPriority().name(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByCompany = tasks.stream()
                .filter(task -> task.getCompany() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCompany().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByDepartment = tasks.stream()
                .filter(task -> task.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getDepartment().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> tasksByAssignee = tasks.stream()
                .filter(task -> task.getAssignedTo() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getAssignedTo().getFirstName() + " " + task.getAssignedTo().getLastName(),
                        Collectors.counting()
                ));

        double averageCompletionTimeDays = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED && 
                        task.getCompletedDate() != null && task.getCreatedAt() != null)
                .mapToDouble(task -> ChronoUnit.DAYS.between(task.getCreatedAt(), task.getCompletedDate()))
                .average()
                .orElse(0.0);

        long tasksCompletedOnTime = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED && 
                        task.getCompletedDate() != null && task.getDueDate() != null &&
                        !task.getCompletedDate().isAfter(task.getDueDate()))
                .count();

        long tasksCompletedLate = tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED && 
                        task.getCompletedDate() != null && task.getDueDate() != null &&
                        task.getCompletedDate().isAfter(task.getDueDate()))
                .count();

        return TaskStatsDTO.builder()
                .totalTasks((long) tasks.size())
                .tasksThisMonth(tasksThisMonth)
                .tasksThisWeek(tasksThisWeek)
                .tasksToday(tasksToday)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .overdueTasks(overdueTasks)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .tasksByCompany(tasksByCompany)
                .tasksByDepartment(tasksByDepartment)
                .tasksByAssignee(tasksByAssignee)
                .averageCompletionTimeDays(averageCompletionTimeDays)
                .tasksCompletedOnTime(tasksCompletedOnTime)
                .tasksCompletedLate(tasksCompletedLate)
                .build();
    }

    @Transactional(readOnly = true)
    public UserStatsDTO getUserStats() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);

        long activeUsers = users.stream()
                .filter(User::isActive)
                .count();

        long inactiveUsers = users.stream()
                .filter(user -> !user.isActive())
                .count();

        long newUsersThisMonth = users.stream()
                .filter(user -> user.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .count();

        long newUsersThisWeek = users.stream()
                .filter(user -> user.getCreatedAt().toLocalDate().isAfter(startOfWeek.minusDays(1)))
                .count();

        long newUsersToday = users.stream()
                .filter(user -> user.getCreatedAt().toLocalDate().equals(today))
                .count();

        Map<String, Long> usersByRole = users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole().name(),
                        Collectors.counting()
                ));

        Map<String, Long> usersByCompany = users.stream()
                .filter(user -> user.getCompany() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getCompany().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> usersByDepartment = users.stream()
                .filter(user -> user.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getDepartment().getName(),
                        Collectors.counting()
                ));

        Map<String, Long> usersByStatus = users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.isActive() ? "ACTIVE" : "INACTIVE",
                        Collectors.counting()
                ));

        long usersWithRecentActivity = users.stream()
                .filter(user -> user.getLastLogin() != null && 
                        user.getLastLogin().isAfter(LocalDateTime.now().minusDays(30)))
                .count();

        long usersNeverLoggedIn = users.stream()
                .filter(user -> user.getLastLogin() == null)
                .count();

        double averageTasksPerUser = userRepository.count() > 0 ? 
                (double) taskRepository.count() / userRepository.count() : 0.0;

        double averageDocumentsPerUser = userRepository.count() > 0 ? 
                (double) documentRepository.count() / userRepository.count() : 0.0;

        return UserStatsDTO.builder()
                .totalUsers((long) users.size())
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .newUsersThisWeek(newUsersThisWeek)
                .newUsersToday(newUsersToday)
                .usersByRole(usersByRole)
                .usersByCompany(usersByCompany)
                .usersByDepartment(usersByDepartment)
                .usersByStatus(usersByStatus)
                .usersWithRecentActivity(usersWithRecentActivity)
                .usersNeverLoggedIn(usersNeverLoggedIn)
                .averageTasksPerUser(averageTasksPerUser)
                .averageDocumentsPerUser(averageDocumentsPerUser)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCompanyStats(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        Map<String, Object> stats = new HashMap<>();
        stats.put("companyName", company.getName());
        stats.put("totalUsers", userRepository.findByCompany(company).size());
        stats.put("totalDocuments", documentRepository.findByCompany(company).size());
        stats.put("totalTasks", taskRepository.findByCompany(company).size());
        stats.put("totalProjects", projectRepository.findByCompany(company).size());
        
        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDepartmentStats(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        Map<String, Object> stats = new HashMap<>();
        stats.put("departmentName", department.getName());
        stats.put("totalUsers", userRepository.findByDepartment(department).size());
        stats.put("totalDocuments", documentRepository.findByDepartment(department).size());
        stats.put("totalTasks", taskRepository.findByDepartment(department).size());
        stats.put("totalProjects", projectRepository.findByDepartment(department).size());
        
        return stats;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDocumentTimeline(LocalDate fromDate, LocalDate toDate) {
        List<Document> documents = documentRepository.findAll();
        
        if (fromDate != null) {
            documents = documents.stream()
                    .filter(doc -> doc.getCreatedAt().toLocalDate().isAfter(fromDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (toDate != null) {
            documents = documents.stream()
                    .filter(doc -> doc.getCreatedAt().toLocalDate().isBefore(toDate.plusDays(1)))
                    .collect(Collectors.toList());
        }

        return documents.stream()
                .collect(Collectors.groupingBy(
                        doc -> doc.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> timelineEntry = new HashMap<>();
                    timelineEntry.put("date", entry.getKey());
                    timelineEntry.put("count", entry.getValue());
                    return timelineEntry;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTaskCompletionTrends(LocalDate fromDate, LocalDate toDate) {
        List<Task> tasks = taskRepository.findAll();
        
        if (fromDate != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getCreatedAt().toLocalDate().isAfter(fromDate.minusDays(1)))
                    .collect(Collectors.toList());
        }
        
        if (toDate != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getCreatedAt().toLocalDate().isBefore(toDate.plusDays(1)))
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED && task.getCompletedDate() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getCompletedDate().toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> trendEntry = new HashMap<>();
                    trendEntry.put("date", entry.getKey());
                    trendEntry.put("completed", entry.getValue());
                    return trendEntry;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStorageStats() {
        List<Document> documents = documentRepository.findAll();
        
        long totalStorageBytes = documents.stream()
                .mapToLong(doc -> doc.getFileSize() != null ? doc.getFileSize() : 0)
                .sum();
        
        double totalStorageGB = totalStorageBytes / (1024.0 * 1024.0 * 1024.0);
        double totalStorageMB = totalStorageBytes / (1024.0 * 1024.0);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStorageBytes", totalStorageBytes);
        stats.put("totalStorageMB", totalStorageMB);
        stats.put("totalStorageGB", totalStorageGB);
        stats.put("totalDocuments", documents.size());
        stats.put("averageFileSizeMB", documents.isEmpty() ? 0.0 : totalStorageMB / documents.size());
        
        return stats;
    }

    private Double calculateStorageUsed() {
        return documentRepository.findAll().stream()
                .mapToLong(doc -> doc.getFileSize() != null ? doc.getFileSize() : 0)
                .sum() / (1024.0 * 1024.0 * 1024.0); // Convert to GB
    }
} 