package com.bika.task.entity;

import com.bika.core.BaseEntity;
import com.bika.company.entity.Company;
import com.bika.company.entity.Department;
import com.bika.project.entity.Project;
import com.bika.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Column(nullable = false)
    private boolean isActive = true;
    
    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        REVIEW,
        COMPLETED,
        CANCELLED
    }
    
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
} 