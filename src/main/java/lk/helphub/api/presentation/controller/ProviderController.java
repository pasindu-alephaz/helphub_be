package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.ProviderProfileResponse;
import lk.helphub.api.application.dto.ProviderRegistrationRequest;
import lk.helphub.api.application.services.ProviderService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Tag(name = "Providers", description = "Provider management and onboarding APIs")
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/register")
    @Operation(summary = "Register as a provider", description = "Onboards a user as a provider by providing personal details, skills, identity documents, and portfolio.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Provider registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> register(
            Principal principal,
            @Valid @RequestBody ProviderRegistrationRequest request) {
        
        ProviderProfileResponse response = providerService.registerProvider(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProviderProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Provider registered successfully")
                .data(response)
                .build());
    }

    @GetMapping("/profile")
    @Operation(summary = "Get provider profile", description = "Retrieves the provider-specific profile details for the authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Provider profile not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAuthority('profile_read')")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getProfile(Principal principal) {
        ProviderProfileResponse response = providerService.getProviderProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.<ProviderProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Provider profile retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update provider profile", description = "Updates the provider profile details.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Provider profile not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> updateProfile(
            Principal principal,
            @Valid @RequestBody ProviderRegistrationRequest request) {
        
        ProviderProfileResponse response = providerService.registerProvider(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<ProviderProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Provider profile updated successfully")
                .data(response)
                .build());
    }
}
