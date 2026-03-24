package lk.helphub.api.presentation.controller;

import jakarta.validation.Valid;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.ProfileService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "User profile management APIs")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get user profile", description = "Retrieves the full profile of the currently authenticated user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"status\":false,\"status_code\":\"UNAUTHORIZED\",\"message\":\"Full authentication is required to access this resource\"}")))
    })
    @PreAuthorize("hasAuthority('profile_read')")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Principal principal) {
        ProfileResponse profile = profileService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Profile retrieved successfully")
                .data(profile)
                .build());
    }

    @PutMapping
    @Operation(summary = "Update user profile", description = "Updates profile fields including identity type/value, dateOfBirth, and language preference")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            Principal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        ProfileResponse updatedProfile = profileService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Profile updated successfully")
                .data(updatedProfile)
                .build());
    }

    @PutMapping("/language-preference")
    @Operation(summary = "Set language preference",
            description = "Sets the app-wide language preference (SINHALA, ENGLISH, or TAMIL). Triggers localized content across the app.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Language preference updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid language value")
    })
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<ProfileResponse>> setLanguagePreference(
            Principal principal,
            @Valid @RequestBody LanguagePreferenceRequest request
    ) {
        ProfileResponse response = profileService.setLanguagePreference(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<ProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Language preference updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping
    @Operation(summary = "Delete user profile",
            description = "Permanently (soft) deletes the user profile. Requires a valid OTP sent to the user's registered phone number. The delete reason is saved for compliance.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"status\":false,\"status_code\":\"BAD_REQUEST\",\"message\":\"Invalid or expired OTP\"}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAuthority('profile_delete')")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            Principal principal,
            @Valid @RequestBody DeleteProfileRequest request
    ) {
        profileService.deleteProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Profile deleted successfully")
                .build());
    }
}
