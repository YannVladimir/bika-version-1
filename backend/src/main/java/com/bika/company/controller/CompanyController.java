package com.bika.company.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.company.dto.CompanyDTO;
import com.bika.company.service.CompanyService;
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
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Company management APIs")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
        summary = "Create a new company",
        description = "Create a new company with the provided details. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Company created successfully",
            content = @Content(schema = @Schema(implementation = CompanyDTO.class))
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.createCompany(companyDTO));
    }

    @Operation(
        summary = "Get all companies",
        description = "Retrieve a list of all companies. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Companies retrieved successfully",
            content = @Content(schema = @Schema(implementation = CompanyDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(
        summary = "Get company by ID",
        description = "Retrieve a company by its ID. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Company retrieved successfully",
            content = @Content(schema = @Schema(implementation = CompanyDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
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
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @Operation(
        summary = "Update company",
        description = "Update an existing company's details. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Company updated successfully",
            content = @Content(schema = @Schema(implementation = CompanyDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.updateCompany(id, companyDTO));
    }

    @Operation(
        summary = "Delete company",
        description = "Delete a company by its ID. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Company deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
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
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
} 