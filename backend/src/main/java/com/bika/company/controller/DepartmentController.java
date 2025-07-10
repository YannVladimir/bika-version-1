package com.bika.company.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.company.dto.DepartmentDTO;
import com.bika.company.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department", description = "Department management APIs")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(
        summary = "Create a new department",
        description = "Create a new department within a company. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Department created successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
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
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.createDepartment(departmentDTO));
    }

    @Operation(
        summary = "Get all departments",
        description = "Retrieve a list of all departments. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Departments retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @Operation(
        summary = "Get departments by company",
        description = "Retrieve all departments for a specific company. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Departments retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
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
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(departmentService.getDepartmentsByCompany(companyId));
    }

    @Operation(
        summary = "Get department by ID",
        description = "Retrieve a department by its ID. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Department retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Department not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @Operation(
        summary = "Update department",
        description = "Update an existing department's details. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Department updated successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Department not found",
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
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, departmentDTO));
    }

    @Operation(
        summary = "Delete department",
        description = "Delete a department by its ID. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Department deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Department not found",
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
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
} 