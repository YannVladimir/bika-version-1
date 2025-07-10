package com.bika.user.controller;

import com.bika.common.dto.ErrorResponse;
import com.bika.user.dto.UserDTO;
import com.bika.user.service.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Get all users",
        description = "Retrieve a list of all users. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
        summary = "Get users by company",
        description = "Retrieve all users for a specific company. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
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
    public ResponseEntity<List<UserDTO>> getUsersByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(userService.getUsersByCompany(companyId));
    }

    @Operation(
        summary = "Get users by department",
        description = "Retrieve all users for a specific department. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
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
    public ResponseEntity<List<UserDTO>> getUsersByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(userService.getUsersByDepartment(departmentId));
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a user by their ID. Users can view their own profile, ADMIN/MANAGER can view any user."
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
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
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
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @Operation(
        summary = "Update user",
        description = "Update an existing user's details. ADMIN can update any user, MANAGER can update users in their company."
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
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
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
        return ResponseEntity.ok(userService.updateCurrentUserProfile(userDTO));
    }

    @Operation(
        summary = "Deactivate user",
        description = "Deactivate a user account. Requires ADMIN role."
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
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @Operation(
        summary = "Activate user",
        description = "Activate a user account. Requires ADMIN role."
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
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @Operation(
        summary = "Delete user",
        description = "Delete a user by their ID. Requires ADMIN role."
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
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 