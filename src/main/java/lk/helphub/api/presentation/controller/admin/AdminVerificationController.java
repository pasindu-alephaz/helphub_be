package lk.helphub.api.presentation.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.admin.application.dto.AdminVerificationRequest;
import lk.helphub.api.admin.application.services.AdminVerificationService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Verification", description = "Admin-only endpoints for verifying providers and documents")
public class AdminVerificationController {

    private final AdminVerificationService adminVerificationService;

    @PatchMapping("/providers/{id}/verify")
    @Operation(summary = "Approve or Reject a provider profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> verifyProvider(@PathVariable UUID id, @Valid @RequestBody AdminVerificationRequest request) {
        adminVerificationService.verifyProvider(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Provider verification status updated")
                .build());
    }

    @PatchMapping("/certificates/{id}/verify")
    @Operation(summary = "Verify a specific certificate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> verifyCertificate(@PathVariable UUID id) {
        adminVerificationService.verifyCertificate(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Certificate verified successfully")
                .build());
    }
}
