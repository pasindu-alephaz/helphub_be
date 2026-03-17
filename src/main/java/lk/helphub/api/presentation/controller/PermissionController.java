package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.helphub.api.application.services.PermissionService;
import lk.helphub.api.domain.entity.Permission;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management APIs (Read-Only)")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Retrieves a single permission by its ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Permission not found\"\n}")))
    })
    @PreAuthorize("hasAuthority('permission_read')")
    public ResponseEntity<ApiResponse<Permission>> getById(
            @Parameter(description = "ID of the permission to retrieve")
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.<Permission>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Permission retrieved successfully")
                .data(permissionService.getById(id))
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieves a list of all permissions")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of permissions retrieved")
    @PreAuthorize("hasAuthority('permission_read')")
    public ResponseEntity<ApiResponse<List<Permission>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Permission>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Permissions retrieved successfully")
                .data(permissionService.getAll())
                .build());
    }
}
