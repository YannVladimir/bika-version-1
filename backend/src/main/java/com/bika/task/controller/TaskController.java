package com.bika.task.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.task.dto.TaskDTO;
import com.bika.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    @Operation(
        summary = "Get all tasks",
        description = "Retrieve a list of all tasks. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(
        summary = "Get my tasks",
        description = "Retrieve tasks assigned to the current user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDTO>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks());
    }

    @Operation(
        summary = "Get tasks by company",
        description = "Retrieve all tasks for a specific company. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskDTO>> getTasksByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(taskService.getTasksByCompany(companyId));
    }

    @Operation(
        summary = "Get tasks by department",
        description = "Retrieve all tasks for a specific department. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Department not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskDTO>> getTasksByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(taskService.getTasksByDepartment(departmentId));
    }

    @Operation(
        summary = "Get tasks by project",
        description = "Retrieve all tasks for a specific project. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @Operation(
        summary = "Get tasks by user",
        description = "Retrieve all tasks assigned to a specific user. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @Operation(
        summary = "Get tasks by status",
        description = "Retrieve all tasks with a specific status. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @Operation(
        summary = "Get task by ID",
        description = "Retrieve a task by its ID. Users can view tasks assigned to them, ADMIN/MANAGER can view any task."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(
        summary = "Create task",
        description = "Create a new task. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(201).body(createdTask);
    }

    @Operation(
        summary = "Update task",
        description = "Update an existing task. ADMIN/MANAGER can update any task, users can update tasks assigned to them."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task updated successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO));
    }

    @Operation(
        summary = "Update task status",
        description = "Update the status of a task. Users can update status of tasks assigned to them."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task status updated successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    @Operation(
        summary = "Assign task",
        description = "Assign a task to a user. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task assigned successfully",
            content = @Content(schema = @Schema(implementation = TaskDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task or user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignTask(taskId, userId));
    }

    @Operation(
        summary = "Delete task",
        description = "Delete a task by its ID. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Task deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
} 