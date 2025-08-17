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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department", description = "Department management APIs")
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(
        summary = "Get all departments",
        description = "Retrieve a list of all departments."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Departments retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        log.info("DepartmentController: getAllDepartments called");
        try {
            List<DepartmentDTO> departments = departmentService.getAllDepartments();
            log.info("DepartmentController: Retrieved {} departments successfully", departments.size());
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            log.error("DepartmentController: Error getting all departments", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get departments by company",
        description = "Retrieve all departments for a specific company."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Departments retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepartmentDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsByCompany(@PathVariable Long companyId) {
        log.info("DepartmentController: getDepartmentsByCompany called for companyId: {}", companyId);
        try {
            List<DepartmentDTO> departments = departmentService.getDepartmentsByCompany(companyId);
            log.info("DepartmentController: Retrieved {} departments for company {}", departments.size(), companyId);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            log.error("DepartmentController: Error getting departments by company: {}", companyId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get department by ID",
        description = "Retrieve a department by its ID."
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
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        log.info("DepartmentController: getDepartmentById called for id: {}", id);
        try {
            DepartmentDTO department = departmentService.getDepartmentById(id);
            log.info("DepartmentController: Department found with ID: {}", id);
            return ResponseEntity.ok(department);
        } catch (Exception e) {
            log.error("DepartmentController: Error getting department by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Create a new department",
        description = "Create a new department with the provided details."
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
        )
    })
    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("DepartmentController: createDepartment called for name: {}", departmentDTO.getName());
        try {
            DepartmentDTO result = departmentService.createDepartment(departmentDTO);
            log.info("DepartmentController: Department created successfully with ID: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("DepartmentController: Error creating department", e);
            throw e;
        }
    }

    @Operation(
        summary = "Update department",
        description = "Update an existing department's details."
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
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("DepartmentController: updateDepartment called for id: {}", id);
        try {
            DepartmentDTO result = departmentService.updateDepartment(id, departmentDTO);
            log.info("DepartmentController: Department updated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("DepartmentController: Error updating department with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete department",
        description = "Delete a department by its ID."
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
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        log.info("DepartmentController: deleteDepartment called for id: {}", id);
        try {
            departmentService.deleteDepartment(id);
            log.info("DepartmentController: Department deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("DepartmentController: Error deleting department with id: {}", id, e);
            throw e;
        }
    }
} 
