package lk.helphub.api.admin.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.admin.application.dto.*;
import lk.helphub.api.admin.application.services.AdminUserService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "APIs for admin to manage users")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users", description = "Retrieves all users with pagination and optional filtering")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by email (partial match)") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by name (partial match on first/last name)") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by user type") @RequestParam(required = false) String userType
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AdminUserResponse> users;
        if (email != null || name != null || status != null || userType != null) {
            users = adminUserService.searchUsers(email, name, status, userType, pageable);
        } else {
            users = adminUserService.getAllUsers(pageable);
        }

        return ResponseEntity.ok(ApiResponse.<Page<AdminUserResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their unique identifier")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable UUID id
    ) {
        AdminUserResponse user = adminUserService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.<AdminUserResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get user by email", description = "Retrieves a specific user by their email address")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserByEmail(
            @Parameter(description = "User email", required = true) @RequestParam String email
    ) {
        AdminUserResponse user = adminUserService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.<AdminUserResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping("/by-phone")
    @Operation(summary = "Get user by phone", description = "Retrieves a specific user by their phone number")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserByPhone(
            @Parameter(description = "User phone number", required = true) @RequestParam String phone
    ) {
        AdminUserResponse user = adminUserService.getUserByPhone(phone);
        return ResponseEntity.ok(ApiResponse.<AdminUserResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Creates a new user account (admin action)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or email already exists")
    })
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
            @Valid @RequestBody AdminUserCreateRequest request
    ) {
        AdminUserResponse user = adminUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AdminUserResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User created successfully")
                .data(user)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user account (admin action)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody AdminUserUpdateRequest request
    ) {
        AdminUserResponse user = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.<AdminUserResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User updated successfully")
                .data(user)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Soft deletes a user account (admin action)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody AdminUserDeleteRequest request
    ) {
        adminUserService.deleteUser(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User deleted successfully")
                .build());
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get user statistics", description = "Retrieves user statistics (counts by status and type)")
    public ResponseEntity<ApiResponse<AdminUserService.UserStatistics>> getUserStatistics() {
        AdminUserService.UserStatistics stats = adminUserService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.<AdminUserService.UserStatistics>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User statistics retrieved successfully")
                .data(stats)
                .build());
    }
}
