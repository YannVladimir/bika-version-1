package com.bika.reports.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.reports.dto.DashboardStatsDTO;
import com.bika.reports.dto.DocumentStatsDTO;
import com.bika.reports.dto.TaskStatsDTO;
import com.bika.reports.dto.UserStatsDTO;
import com.bika.reports.service.ReportsService;
import com.bika.reports.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Analytics and reporting APIs")
public class ReportsController {

    private final ReportsService reportsService;
    private final DashboardService dashboardService;

    @Operation(
        summary = "Get role-based dashboard statistics",
        description = "Retrieve dashboard statistics based on current user's role"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dashboard statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DashboardStatsDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/dashboard/role-based")
    public ResponseEntity<DashboardStatsDTO> getRoleBasedDashboardStats() {
        return ResponseEntity.ok(dashboardService.getRoleBasedDashboardStats());
    }

    @Operation(
        summary = "Get dashboard statistics",
        description = "Retrieve overall dashboard statistics. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Dashboard statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DashboardStatsDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(reportsService.getDashboardStats());
    }

    @Operation(
        summary = "Get document statistics",
        description = "Retrieve document-related statistics. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentStatsDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/documents")
    public ResponseEntity<DocumentStatsDTO> getDocumentStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportsService.getDocumentStats(fromDate, toDate));
    }

    @Operation(
        summary = "Get task statistics",
        description = "Retrieve task-related statistics. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = TaskStatsDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/tasks")
    public ResponseEntity<TaskStatsDTO> getTaskStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportsService.getTaskStats(fromDate, toDate));
    }

    @Operation(
        summary = "Get user statistics",
        description = "Retrieve user-related statistics. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User statistics retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserStatsDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/users")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        return ResponseEntity.ok(reportsService.getUserStats());
    }

    @Operation(
        summary = "Get company statistics",
        description = "Retrieve statistics for a specific company. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Company statistics retrieved successfully"
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
    public ResponseEntity<Map<String, Object>> getCompanyStats(@PathVariable Long companyId) {
        return ResponseEntity.ok(reportsService.getCompanyStats(companyId));
    }

    @Operation(
        summary = "Get department statistics",
        description = "Retrieve statistics for a specific department. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Department statistics retrieved successfully"
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
    public ResponseEntity<Map<String, Object>> getDepartmentStats(@PathVariable Long departmentId) {
        return ResponseEntity.ok(reportsService.getDepartmentStats(departmentId));
    }

    @Operation(
        summary = "Get document activity timeline",
        description = "Retrieve document activity over time. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Document activity timeline retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/documents/timeline")
    public ResponseEntity<List<Map<String, Object>>> getDocumentTimeline(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportsService.getDocumentTimeline(fromDate, toDate));
    }

    @Operation(
        summary = "Get task completion trends",
        description = "Retrieve task completion trends over time. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task completion trends retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/tasks/trends")
    public ResponseEntity<List<Map<String, Object>>> getTaskCompletionTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportsService.getTaskCompletionTrends(fromDate, toDate));
    }

    @Operation(
        summary = "Get storage usage statistics",
        description = "Retrieve storage usage statistics. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Storage usage statistics retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/storage")
    public ResponseEntity<Map<String, Object>> getStorageStats() {
        return ResponseEntity.ok(reportsService.getStorageStats());
    }
} 
