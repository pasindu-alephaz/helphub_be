package lk.helphub.api.admin.presentation.controller;

import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.LoginRequest;
import lk.helphub.api.application.dto.RefreshTokenRequest;
import lk.helphub.api.application.dto.VerifyOtpRequest;
import lk.helphub.api.admin.application.services.AdminAuthService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
@Tag(name = "Admin - Authentication", description = "Admin login APIs")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Authenticates an admin and returns a JWT token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admin logged in successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\",\n  \"errors\": {\n    \"email\": [\"must be a well-formed email address\"]\n  }\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"UNAUTHORIZED\",\n  \"message\": \"Invalid email or password\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "User is not an admin",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"FORBIDDEN\",\n  \"message\": \"Access Denied: Admin privileges required\"\n}")))
    })
    public ResponseEntity<ApiResponse<AuthResponse>> loginAdmin(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = adminAuthService.loginAdmin(request);
        String message = response.isTwoFactorRequired()
                ? "OTP sent to your email. Please verify to continue."
                : "Admin logged in successfully";

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(response.isTwoFactorRequired() ? ResponseStatusCode.TWO_FA_REQUIRED : ResponseStatusCode.SUCCESS)
                .message(message)
                .data(response)
                .build());
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "Verify admin 2FA", description = "Verifies the 2FA OTP for admin login")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "2FA verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"BAD_REQUEST\",\n  \"message\": \"Invalid or expired OTP\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "User is not an admin",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"FORBIDDEN\",\n  \"message\": \"Access Denied: Admin privileges required\"\n}")))
    })
    public ResponseEntity<ApiResponse<AuthResponse>> verify2fa(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        AuthResponse response = adminAuthService.verify2fa(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Admin 2FA verified successfully")
                .data(response)
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh admin access token", description = "Returns a new short-lived access token given a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = adminAuthService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Token refreshed successfully")
                .data(response)
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Admin logout", description = "Revokes the provided refresh token, logging the admin out from that session")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        adminAuthService.logout(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Logged out successfully")
                .build());
    }
}
