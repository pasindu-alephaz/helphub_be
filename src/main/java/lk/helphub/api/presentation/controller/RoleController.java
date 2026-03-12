package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.services.RoleService;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", description = "Creates a new role resource")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or existing role")
    })
    public ResponseEntity<ApiResponse<Role>> create(@Valid @RequestBody Role dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Role>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Role created successfully")
                .data(roleService.create(dto))
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieves a single role by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<ApiResponse<Role>> getById(
            @Parameter(description = "ID of the role to retrieve")
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.<Role>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Role retrieved successfully")
                .data(roleService.getById(id))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieves a list of all roles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of roles retrieved")
    public ResponseEntity<ApiResponse<List<Role>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Role>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Roles retrieved successfully")
                .data(roleService.getAll())
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role", description = "Updates an existing role by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or existing role")
    })
    public ResponseEntity<ApiResponse<Role>> update(
            @Parameter(description = "ID of the role to update")
            @PathVariable Integer id,
            @Valid @RequestBody Role dto) {
        return ResponseEntity.ok(ApiResponse.<Role>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Role updated successfully")
                .data(roleService.update(id, dto))
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Deletes a role by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the role to delete")
            @PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "Assign permissions to a role", description = "Replaces the permissions assigned to an existing role")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permissions assigned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role or permission not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<ApiResponse<Role>> assignPermissions(
            @Parameter(description = "ID of the role to update")
            @PathVariable Integer id,
            @Valid @RequestBody Set<Integer> permissionIds) {
        return ResponseEntity.ok(ApiResponse.<Role>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Permissions assigned gracefully")
                .data(roleService.assignPermissionsToRole(id, permissionIds))
                .build());
    }
}
