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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/companies")  // Changed from /api/v1/companies since context-path is /api
@RequiredArgsConstructor
@Tag(name = "Company", description = "Company management APIs")
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
        summary = "Create a new company",
        description = "Create a new company with the provided details."
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
        )
    })
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        log.debug("CompanyController: createCompany called");
        try {
            CompanyDTO result = companyService.createCompany(companyDTO);
            log.debug("CompanyController: Company created successfully with ID: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("CompanyController: Error creating company", e);
            throw e;
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("CompanyController: TEST endpoint called - CONTROLLER IS WORKING");
        return ResponseEntity.ok("Controller is working!");
    }

    @Operation(
        summary = "Get all companies",
        description = "Retrieve a list of all companies."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Companies retrieved successfully",
            content = @Content(schema = @Schema(implementation = CompanyDTO.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        log.info("CompanyController: getAllCompanies called - ENTRY POINT");
        try {
            log.info("CompanyController: About to call companyService.getAllCompanies()");
            List<CompanyDTO> companies = companyService.getAllCompanies();
            log.info("CompanyController: Successfully retrieved {} companies", companies.size());
            return ResponseEntity.ok(companies);
        } catch (Exception e) {
            log.error("CompanyController: Error getting all companies - Exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
        summary = "Get company by ID",
        description = "Retrieve a company by its ID."
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
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        log.debug("CompanyController: getCompanyById called with id: {}", id);
        try {
            CompanyDTO result = companyService.getCompanyById(id);
            log.debug("CompanyController: Company found with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("CompanyController: Error getting company by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Update company",
        description = "Update an existing company's details."
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
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyDTO companyDTO) {
        log.debug("CompanyController: updateCompany called with id: {}", id);
        try {
            CompanyDTO result = companyService.updateCompany(id, companyDTO);
            log.debug("CompanyController: Company updated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("CompanyController: Error updating company with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete company",
        description = "Delete a company by its ID."
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
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        log.debug("CompanyController: deleteCompany called with id: {}", id);
        try {
            companyService.deleteCompany(id);
            log.debug("CompanyController: Company deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("CompanyController: Error deleting company with id: {}", id, e);
            throw e;
        }
    }
} 