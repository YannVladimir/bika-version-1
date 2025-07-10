package com.bika.task.repository;

import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.project.entity.Project;
import com.bika.task.entity.Task;
import com.bika.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByCompany(Company company);
    
    List<Task> findByDepartment(Department department);
    
    List<Task> findByProject(Project project);
    
    List<Task> findByAssignedTo(User assignedTo);
    
    List<Task> findByCreatedBy(User createdBy);
    
    List<Task> findByCompanyAndStatus(Company company, Task.TaskStatus status);
    
    List<Task> findByAssignedToAndStatus(User assignedTo, Task.TaskStatus status);
    
    List<Task> findByDueDateBefore(LocalDateTime date);
    
    List<Task> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.company = :company AND t.isActive = true")
    List<Task> findActiveTasksByCompany(@Param("company") Company company);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.isActive = true")
    List<Task> findActiveTasksByUser(@Param("user") User user);
    
    @Query("SELECT t FROM Task t WHERE t.company = :company AND t.status = 'TODO' AND t.isActive = true")
    List<Task> findPendingTasksByCompany(@Param("company") Company company);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.status = 'TODO' AND t.isActive = true")
    List<Task> findPendingTasksByUser(@Param("user") User user);
} 