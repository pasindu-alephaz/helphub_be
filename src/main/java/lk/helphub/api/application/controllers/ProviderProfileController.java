package lk.helphub.api.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.ProviderCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Tag(name = "Provider Profile", description = "Endpoints for managing granular provider profiles (Personal Data, Address, Bio)")
public class ProviderProfileController {

    private final ProviderCoreService providerService;

    @GetMapping
    @Operation(summary = "List all providers", description = "Retrieve a list of all registered provider profiles")
    public ResponseEntity<List<ProviderProfileResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby providers", description = "Retrieve providers within a specified radius of a point (location-based discovery)")
    public ResponseEntity<List<ProviderProfileResponse>> getNearbyProviders(
            @Parameter(description = "Coordinates in lat,lon format", example = "6.9271,79.8612") @RequestParam String point,
            @Parameter(description = "Radius in meters", example = "5000") @RequestParam double radius) {
        String[] coords = point.split(",");
        double lat = Double.parseDouble(coords[0].trim());
        double lon = Double.parseDouble(coords[1].trim());
        return ResponseEntity.ok(providerService.findNearbyProviders(lat, lon, radius));
    }

    @GetMapping("/{providerId}")
    @Operation(summary = "Get aggregated provider profile", description = "Retrieve the full profile of a specific provider")
    public ResponseEntity<ProviderProfileResponse> getProfileById(@PathVariable UUID providerId) {
        return ResponseEntity.ok(providerService.getProviderProfile(providerId));
    }

    @DeleteMapping("/{providerId}")
    @Operation(summary = "Full record removal", description = "Permanently remove a provider record")
    @PreAuthorize("hasAuthority('provider_admin')")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID providerId) {
        providerService.deleteProvider(providerId);
        return ResponseEntity.noContent().build();
    }

    // --- Personal Details ---

    @GetMapping("/{providerId}/personal-details")
    @Operation(summary = "Get personal details", description = "Retrieve core personal information for a provider")
    public ResponseEntity<PersonalDetailsRequest> getPersonalDetails(@PathVariable UUID providerId) {
        return ResponseEntity.ok(providerService.getPersonalDetails(providerId));
    }

    @PostMapping("/{providerId}/personal-details")
    @Operation(summary = "Create personal details", description = "Initial data entry for core personal information")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> createPersonalDetails(@PathVariable UUID providerId, @Valid @RequestBody PersonalDetailsRequest request) {
        providerService.createPersonalDetails(providerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{providerId}/personal-details")
    @Operation(summary = "Update personal details (Full)", description = "Replace all core personal information")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> updatePersonalDetails(@PathVariable UUID providerId, @Valid @RequestBody PersonalDetailsRequest request) {
        providerService.updatePersonalDetails(providerId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{providerId}/personal-details")
    @Operation(summary = "Update personal details (Partial)", description = "Modify specific personal information fields")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> patchPersonalDetails(@PathVariable UUID providerId, @RequestBody PersonalDetailsRequest request) {
        providerService.patchPersonalDetails(providerId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{providerId}/personal-details/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile picture", description = "Handle multipart/form-data file uploads for the profile picture")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> uploadProfilePicture(
            @PathVariable UUID providerId,
            @Parameter(description = "Profile Picture Image") @RequestPart("profilePicture") MultipartFile profilePicture) {
        providerService.updateProfilePicture(providerId, profilePicture);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // --- Address Details ---

    @GetMapping("/{providerId}/address-details")
    @Operation(summary = "Get address details", description = "Retrieve physical address and coordinates for a provider")
    public ResponseEntity<AddressDetailsRequest> getAddressDetails(@PathVariable UUID providerId) {
        return ResponseEntity.ok(providerService.getAddressDetails(providerId));
    }

    @PostMapping("/{providerId}/address-details")
    @Operation(summary = "Create address details", description = "Initial data entry for physical address information")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> createAddressDetails(@PathVariable UUID providerId, @Valid @RequestBody AddressDetailsRequest request) {
        providerService.createAddressDetails(providerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{providerId}/address-details")
    @Operation(summary = "Update address details (Full)", description = "Replace all address information")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> updateAddressDetails(@PathVariable UUID providerId, @Valid @RequestBody AddressDetailsRequest request) {
        providerService.updateAddressDetails(providerId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{providerId}/address-details")
    @Operation(summary = "Update address details (Partial)", description = "Modify specific address fields")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> patchAddressDetails(@PathVariable UUID providerId, @RequestBody AddressDetailsRequest request) {
        providerService.patchAddressDetails(providerId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{providerId}/address-details")
    @Operation(summary = "Remove address record", description = "Clear the address information for a provider")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> deleteAddressDetails(@PathVariable UUID providerId) {
        providerService.deleteAddressDetails(providerId);
        return ResponseEntity.noContent().build();
    }

    // --- Professional Bio ---

    @GetMapping("/{providerId}/professional-bio")
    @Operation(summary = "Get professional bio", description = "Retrieve the long-form bio for a provider")
    public ResponseEntity<String> getProfessionalBio(@PathVariable UUID providerId) {
        return ResponseEntity.ok(providerService.getProfessionalBio(providerId));
    }

    @PostMapping("/{providerId}/professional-bio")
    @Operation(summary = "Create professional bio", description = "Initial data entry for the bio string")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> createProfessionalBio(@PathVariable UUID providerId, @RequestBody ProfessionalBioRequest request) {
        providerService.createProfessionalBio(providerId, request.getBio());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{providerId}/professional-bio")
    @Operation(summary = "Update professional bio", description = "Replace the bio string")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<Void> updateProfessionalBio(@PathVariable UUID providerId, @RequestBody ProfessionalBioRequest request) {
        providerService.updateProfessionalBio(providerId, request.getBio());
        return ResponseEntity.ok().build();
    }
}
