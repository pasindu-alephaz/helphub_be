package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.ProviderOnboardingService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/providers/onboarding")
@RequiredArgsConstructor
@Tag(name = "Provider Onboarding", description = "Endpoints for provider registration and verification steps")
public class ProviderOnboardingController {

    private final ProviderOnboardingService onboardingService;

    @PostMapping(value = "/identity", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Step 1: Submit Identity Documents")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> submitIdentity(
            Principal principal,
            @RequestParam("idType") lk.helphub.api.domain.enums.IdentityType idType,
            @RequestParam("idNumber") String idNumber,
            @RequestPart("documentImages") org.springframework.web.multipart.MultipartFile[] documentImages,
            @RequestPart("selfieImage") org.springframework.web.multipart.MultipartFile selfieImage) {
        onboardingService.submitIdentity(principal.getName(), idType, idNumber, documentImages, selfieImage);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Identity documents submitted successfully")
                .build());
    }

    @PostMapping(value = "/certificates", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Step 2: Add Professional Certificates")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> addCertificate(
            Principal principal,
            @RequestParam("name") String name,
            @RequestParam("issuedDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate issuedDate,
            @RequestPart("files") org.springframework.web.multipart.MultipartFile[] files) {
        onboardingService.addCertificate(principal.getName(), name, issuedDate, files);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Certificate added successfully")
                .build());
    }

    @PostMapping("/services")
    @Operation(summary = "Step 3: Select Service Categories")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> setServices(Principal principal, @Valid @RequestBody List<ProviderServiceRequest> requests) {
        onboardingService.setServices(principal.getName(), requests);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Services updated successfully")
                .build());
    }

    @PostMapping("/availability")
    @Operation(summary = "Step 4: Set Availability Schedule")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> setAvailability(
            Principal principal, 
            @RequestParam(value = "isAvailable", defaultValue = "true") boolean isAvailable,
            @Valid @RequestBody List<ProviderAvailabilityRequest> requests) {
        onboardingService.setAvailability(principal.getName(), requests, isAvailable);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Availability schedule updated successfully")
                .build());
    }

    @PostMapping(value = "/portfolio", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Optional: Add Portfolio Media")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> addPortfolioItem(
            Principal principal,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestPart("files") org.springframework.web.multipart.MultipartFile[] files) {
        onboardingService.addPortfolioItem(principal.getName(), title, description, files);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Portfolio item added successfully")
                .build());
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve current provider profile status")
    @PreAuthorize("hasAuthority('profile_view')")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getMyProfile(Principal principal) {
        ProviderProfileResponse response = onboardingService.getMyProviderProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.<ProviderProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .data(response)
                .build());
    }
}
