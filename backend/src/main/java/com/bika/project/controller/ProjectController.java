package com.bika.project.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.project.dto.ProjectDTO;
import com.bika.project.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(
        summary = "Create a new project",
        description = "Create a new project within a company/department. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Project created successfully",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
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
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company or Department not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    @Operation(
        summary = "Get all projects",
        description = "Retrieve a list of all projects. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Projects retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(
        summary = "Get projects by company",
        description = "Retrieve all projects for a specific company. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Projects retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
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
    public ResponseEntity<List<ProjectDTO>> getProjectsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(projectService.getProjectsByCompany(companyId));
    }

    @Operation(
        summary = "Get projects by department",
        description = "Retrieve all projects for a specific department. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Projects retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
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
    public ResponseEntity<List<ProjectDTO>> getProjectsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(projectService.getProjectsByDepartment(departmentId));
    }

    @Operation(
        summary = "Get project by ID",
        description = "Retrieve a project by its ID. Requires ADMIN, MANAGER, or EMPLOYEE role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Project retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @Operation(
        summary = "Update project",
        description = "Update an existing project's details. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Project updated successfully",
            content = @Content(schema = @Schema(implementation = ProjectDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }

    @Operation(
        summary = "Delete project",
        description = "Delete a project by its ID. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Project deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found",
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
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
} 