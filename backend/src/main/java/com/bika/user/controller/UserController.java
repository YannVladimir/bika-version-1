package com.bika.user.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.user.dto.UserDTO;
import com.bika.user.dto.CreateUserRequest;
import com.bika.user.service.UserService;
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
@RequestMapping("/v1/users")  // Updated to match the pattern used in CompanyController
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Create a new user",
        description = "Create a new user with the provided details."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("UserController: createUser called for email: {}", createUserRequest.getEmail());
        try {
            UserDTO result = userService.createUser(createUserRequest);
            log.info("UserController: User created successfully with ID: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("UserController: Error creating user", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get all users",
        description = "Retrieve a list of all users."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("UserController: getAllUsers called");
        try {
            List<UserDTO> users = userService.getAllUsers();
            log.info("UserController: Retrieved {} users successfully", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("UserController: Error getting all users", e);
            throw e;
        }
    }

    @Operation(
        summary = "Get users by company",
        description = "Retrieve all users for a specific company."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Company not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByCompany(@PathVariable Long companyId) {
        log.info("UserController: getUsersByCompany called for companyId: {}", companyId);
        try {
            List<UserDTO> users = userService.getUsersByCompany(companyId);
            log.info("UserController: Retrieved {} users for company {}", users.size(), companyId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("UserController: Error getting users by company: {}", companyId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get users by department",
        description = "Retrieve all users for a specific department."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Department not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<UserDTO>> getUsersByDepartment(@PathVariable Long departmentId) {
        log.info("UserController: getUsersByDepartment called for departmentId: {}", departmentId);
        try {
            List<UserDTO> users = userService.getUsersByDepartment(departmentId);
            log.info("UserController: Retrieved {} users for department {}", users.size(), departmentId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("UserController: Error getting users by department: {}", departmentId, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a user by their ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("UserController: getUserById called for id: {}", id);
        try {
            UserDTO user = userService.getUserById(id);
            log.info("UserController: User found with ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("UserController: Error getting user by id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Get current user profile",
        description = "Retrieve the current authenticated user's profile."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        log.info("UserController: getCurrentUserProfile called");
        try {
            UserDTO user = userService.getCurrentUserProfile();
            log.info("UserController: Current user profile retrieved successfully");
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("UserController: Error getting current user profile", e);
            throw e;
        }
    }

    @Operation(
        summary = "Update user",
        description = "Update an existing user's details."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("UserController: updateUser called for id: {}", id);
        try {
            UserDTO result = userService.updateUser(id, userDTO);
            log.info("UserController: User updated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("UserController: Error updating user with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Update current user profile",
        description = "Update the current authenticated user's profile."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(@Valid @RequestBody UserDTO userDTO) {
        log.info("UserController: updateCurrentUserProfile called");
        try {
            UserDTO result = userService.updateCurrentUserProfile(userDTO);
            log.info("UserController: Current user profile updated successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("UserController: Error updating current user profile", e);
            throw e;
        }
    }

    @Operation(
        summary = "Deactivate user",
        description = "Deactivate a user account."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User deactivated successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        log.info("UserController: deactivateUser called for id: {}", id);
        try {
            UserDTO result = userService.deactivateUser(id);
            log.info("UserController: User deactivated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("UserController: Error deactivating user with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Activate user",
        description = "Activate a user account."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "User activated successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        log.info("UserController: activateUser called for id: {}", id);
        try {
            UserDTO result = userService.activateUser(id);
            log.info("UserController: User activated successfully with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("UserController: Error activating user with id: {}", id, e);
            throw e;
        }
    }

    @Operation(
        summary = "Delete user",
        description = "Delete a user by their ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("UserController: deleteUser called for id: {}", id);
        try {
            userService.deleteUser(id);
            log.info("UserController: User deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("UserController: Error deleting user with id: {}", id, e);
            throw e;
        }
    }
} 