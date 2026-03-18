package lk.helphub.api.presentation.controller;

import jakarta.validation.Valid;
import lk.helphub.api.application.dto.UserAddressRequest;
import lk.helphub.api.application.dto.UserAddressResponse;
import lk.helphub.api.application.services.UserAddressService;
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
@RequestMapping("/api/v1/profile/addresses")
@RequiredArgsConstructor
@Tag(name = "Profile - Addresses", description = "Multi-address management APIs")
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    @Operation(summary = "List all addresses", description = "Returns all saved addresses for the authenticated user")
    @PreAuthorize("hasAuthority('profile_read')")
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getAddresses(Principal principal) {
        List<UserAddressResponse> addresses = userAddressService.getAddresses(principal.getName());
        return ResponseEntity.ok(ApiResponse.<List<UserAddressResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Addresses retrieved successfully")
                .data(addresses)
                .build());
    }

    @PostMapping
    @Operation(summary = "Add address", description = "Adds a new address with optional GPS coordinates and label (e.g. Home, Work)")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<UserAddressResponse>> addAddress(
            Principal principal,
            @Valid @RequestBody UserAddressRequest request
    ) {
        UserAddressResponse response = userAddressService.addAddress(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Address added successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address", description = "Updates an existing address by ID")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UserAddressRequest request
    ) {
        UserAddressResponse response = userAddressService.updateAddress(principal.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Address updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Deletes an address by ID")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            Principal principal,
            @PathVariable UUID id
    ) {
        userAddressService.deleteAddress(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Address deleted successfully")
                .build());
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Set default address",
               description = "Sets an address as the default for job requests. Clears the previous default automatically.")
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<UserAddressResponse>> setDefault(
            Principal principal,
            @PathVariable UUID id
    ) {
        UserAddressResponse response = userAddressService.setDefault(principal.getName(), id);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Default address updated")
                .data(response)
                .build());
    }
}
