package lk.helphub.api.presentation.controller;

import jakarta.validation.Valid;
import lk.helphub.api.application.dto.UserLanguageRequest;
import lk.helphub.api.application.dto.UserLanguageResponse;
import lk.helphub.api.application.services.UserLanguageService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/profile/languages")
@RequiredArgsConstructor
@Tag(name = "Profile - Communication Preferences", description = "User communication language preference management APIs")
public class UserLanguageController {

    private final UserLanguageService userLanguageService;

    @GetMapping
    @Operation(summary = "List communication language preferences",
               description = "Returns all saved communication language entries for the authenticated user. Supports Sinhala (සිංහල), Tamil (தமிழ்), English, and custom entries.")
    @PreAuthorize("hasAuthority('profile_read')")
    public ResponseEntity<ApiResponse<List<UserLanguageResponse>>> getLanguages(Principal principal) {
        List<UserLanguageResponse> languages = userLanguageService.getLanguages(principal.getName());
        return ResponseEntity.ok(ApiResponse.<List<UserLanguageResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Language preferences retrieved successfully")
                .data(languages)
                .build());
    }

    @PostMapping
    @Operation(summary = "Add communication language",
               description = "Adds a language preference entry. Use 'Other' as languageName for a custom entry. Proficiency: BASIC | CONVERSATIONAL | FLUENT | NATIVE.")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<UserLanguageResponse>> addLanguage(
            Principal principal,
            @Valid @RequestBody UserLanguageRequest request
    ) {
        UserLanguageResponse response = userLanguageService.addLanguage(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<UserLanguageResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Language preference added successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update communication language", description = "Updates an existing language preference entry by ID")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<UserLanguageResponse>> updateLanguage(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UserLanguageRequest request
    ) {
        UserLanguageResponse response = userLanguageService.updateLanguage(principal.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.<UserLanguageResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Language preference updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove communication language", description = "Removes a language preference entry by ID")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(
            Principal principal,
            @PathVariable UUID id
    ) {
        userLanguageService.deleteLanguage(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Language preference removed successfully")
                .build());
    }
}
