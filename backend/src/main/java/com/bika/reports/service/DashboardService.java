package com.bika.reports.service;

import com.bika.reports.dto.DashboardStatsDTO;
import com.bika.user.entity.User;
import com.bika.user.repository.UserRepository;
import com.bika.document.repository.DocumentRepository;
import com.bika.task.repository.TaskRepository;
import com.bika.project.repository.ProjectRepository;
import com.bika.company.repository.CompanyRepository;
import com.bika.company.repository.DepartmentRepository;
import com.bika.task.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getRoleBasedDashboardStats() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        switch (currentUser.getRole()) {
            case SUPER_ADMIN:
                return getSuperAdminStats();
            case COMPANY_ADMIN:
                return getCompanyAdminStats(currentUser);
            case MANAGER:
                return getManagerStats(currentUser);
            case USER:
            default:
                return getUserStats(currentUser);
        }
    }

    private DashboardStatsDTO getSuperAdminStats() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        long totalUsers = userRepository.count();
        long totalCompanies = companyRepository.count();
        long totalDocuments = documentRepository.count();
        long totalTasks = taskRepository.count();
        long totalProjects = projectRepository.count();
        
        long activeUsers = userRepository.findAll().stream()
                .filter(User::isActive)
                .count();
        
        long newUsersThisMonth = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                .count();

        long documentsUploadedToday = documentRepository.findAll().stream()
                .filter(doc -> doc.getCreatedAt().toLocalDate().equals(today))
                .count();

        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalCompanies(totalCompanies)
                .totalDocuments(totalDocuments)
                .totalTasks(totalTasks)
                .totalProjects(totalProjects)
                .activeUsers(activeUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .documentsUploadedToday(documentsUploadedToday)
                .storageUsedGB(calculateStorageUsed())
                .storageTotalGB(1000.0)
                .build();
    }

    private DashboardStatsDTO getCompanyAdminStats(User currentUser) {
        LocalDate today = LocalDate.now();
        
        long companyUsers = userRepository.findByCompany(currentUser.getCompany()).size();
        long companyDocuments = documentRepository.findByCompany(currentUser.getCompany()).size();
        long companyTasks = taskRepository.findByCompany(currentUser.getCompany()).size();
        long companyProjects = projectRepository.findByCompany(currentUser.getCompany()).size();
        
        long activeCompanyUsers = userRepository.findByCompany(currentUser.getCompany()).stream()
                .filter(User::isActive)
                .count();

        long pendingTasks = taskRepository.findByCompany(currentUser.getCompany()).stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.TODO)
                .count();

        long completedTasks = taskRepository.findByCompany(currentUser.getCompany()).stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count();

        long overdueTasks = taskRepository.findByCompany(currentUser.getCompany()).stream()
                .filter(task -> task.getDueDate() != null && 
                        task.getDueDate().isBefore(LocalDateTime.now()) && 
                        task.getStatus() != Task.TaskStatus.COMPLETED)
                .count();

        long documentsUploadedToday = documentRepository.findByCompany(currentUser.getCompany()).stream()
                .filter(doc -> doc.getCreatedAt().toLocalDate().equals(today))
                .count();

        // Simplified - count completed tasks created today instead
        long tasksCompletedToday = taskRepository.findByCompany(currentUser.getCompany()).stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED &&
                        task.getUpdatedAt().toLocalDate().equals(today))
                .count();

        return DashboardStatsDTO.builder()
                .totalUsers(companyUsers)
                .totalDocuments(companyDocuments)
                .totalTasks(companyTasks)
                .totalProjects(companyProjects)
                .activeUsers(activeCompanyUsers)
                .pendingTasks(pendingTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasks)
                .documentsUploadedToday(documentsUploadedToday)
                .tasksCompletedToday(tasksCompletedToday)
                .storageUsedGB(calculateCompanyStorageUsed(currentUser.getCompany().getId()))
                .storageTotalGB(100.0)
                .build();
    }

    private DashboardStatsDTO getManagerStats(User currentUser) {
        LocalDate today = LocalDate.now();
        
        long departmentTasks = 0;
        long departmentUsers = 0;
        long departmentProjects = 0;
        long pendingTasks = 0;
        long completedTasks = 0;
        long overdueTasks = 0;
        long tasksCompletedToday = 0;
        
        if (currentUser.getDepartment() != null) {
            departmentTasks = taskRepository.findByDepartment(currentUser.getDepartment()).size();
            departmentUsers = userRepository.findByDepartment(currentUser.getDepartment()).size();
            departmentProjects = projectRepository.findByDepartment(currentUser.getDepartment()).size();

            pendingTasks = taskRepository.findByDepartment(currentUser.getDepartment()).stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.TODO)
                    .count();

            completedTasks = taskRepository.findByDepartment(currentUser.getDepartment()).stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                    .count();

            overdueTasks = taskRepository.findByDepartment(currentUser.getDepartment()).stream()
                    .filter(task -> task.getDueDate() != null && 
                            task.getDueDate().isBefore(LocalDateTime.now()) && 
                            task.getStatus() != Task.TaskStatus.COMPLETED)
                    .count();

            tasksCompletedToday = taskRepository.findByDepartment(currentUser.getDepartment()).stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED &&
                            task.getUpdatedAt().toLocalDate().equals(today))
                    .count();
        }

        long myTasks = taskRepository.findByAssignedTo(currentUser).size();
        long myProjects = projectRepository.findByCompany(currentUser.getCompany()).size();

        return DashboardStatsDTO.builder()
                .totalTasks(departmentTasks)
                .totalUsers(departmentUsers)
                .totalProjects(departmentProjects)
                .pendingTasks(pendingTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasks)
                .tasksCompletedToday(tasksCompletedToday)
                .myTasks(myTasks)
                .myProjects(myProjects)
                .departmentTasks(departmentTasks)
                .build();
    }

    private DashboardStatsDTO getUserStats(User currentUser) {
        LocalDate today = LocalDate.now();
        
        long myTasks = taskRepository.findByAssignedTo(currentUser).size();
        
        // Simplified document count for user's company
        long myDocuments = documentRepository.findByCompany(currentUser.getCompany()).size();
        
        long pendingTasks = taskRepository.findByAssignedTo(currentUser).stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.TODO)
                .count();

        long completedTasks = taskRepository.findByAssignedTo(currentUser).stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                .count();

        long overdueTasks = taskRepository.findByAssignedTo(currentUser).stream()
                .filter(task -> task.getDueDate() != null && 
                        task.getDueDate().isBefore(LocalDateTime.now()) && 
                        task.getStatus() != Task.TaskStatus.COMPLETED)
                .count();

        long tasksCompletedToday = taskRepository.findByAssignedTo(currentUser).stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED &&
                        task.getUpdatedAt().toLocalDate().equals(today))
                .count();

        long myProjects = projectRepository.findByCompany(currentUser.getCompany()).size();

        return DashboardStatsDTO.builder()
                .myTasks(myTasks)
                .totalDocuments(myDocuments)
                .myProjects(myProjects)
                .pendingTasks(pendingTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasks)
                .tasksCompletedToday(tasksCompletedToday)
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private double calculateStorageUsed() {
        return documentRepository.findAll().stream()
                .mapToDouble(doc -> doc.getFileSize() != null ? doc.getFileSize() / (1024.0 * 1024.0 * 1024.0) : 0.0)
                .sum();
    }

    private double calculateCompanyStorageUsed(Long companyId) {
        return companyRepository.findById(companyId)
                .map(company -> documentRepository.findByCompany(company).stream()
                        .mapToDouble(doc -> doc.getFileSize() != null ? doc.getFileSize() / (1024.0 * 1024.0 * 1024.0) : 0.0)
                        .sum())
                .orElse(0.0);
    }
} 