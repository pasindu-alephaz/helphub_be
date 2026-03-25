package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.helphub.api.application.dto.ProviderProfileResponse;
import lk.helphub.api.application.services.ProviderOnboardingService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Tag(name = "Provider Profiles", description = "Public-facing provider profile endpoints")
public class ProviderProfileController {

    private final ProviderOnboardingService onboardingService;

    @GetMapping("/{id}/profile")
    @Operation(summary = "Retrieve public profile of a provider")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getProviderProfile(@PathVariable UUID id) {
        ProviderProfileResponse response = onboardingService.getProviderProfile(id);
        return ResponseEntity.ok(ApiResponse.<ProviderProfileResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .data(response)
                .build());
    }
}
