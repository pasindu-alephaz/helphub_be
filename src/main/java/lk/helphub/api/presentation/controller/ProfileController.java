package lk.helphub.api.presentation.controller;

import jakarta.validation.Valid;
import lk.helphub.api.application.ProfileService;
import lk.helphub.api.application.dto.ProfileResponse;
import lk.helphub.api.application.dto.UpdateProfileRequest;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
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
}
