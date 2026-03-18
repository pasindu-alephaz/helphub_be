package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.helphub.api.application.services.AuthService;
import lk.helphub.api.application.services.SocialAuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {

    private final AuthService authService;
    private final SocialAuthService socialAuthService;

    @PostMapping("/phone/init")
    @Operation(summary = "Initiate phone authentication", description = "Sends an OTP to the provided phone number")
    public ResponseEntity<ApiResponse<AuthResponse>> sendPhoneOtp(@Valid @RequestBody PhoneInitRequest request) {
        AuthResponse response = authService.sendPhoneOtp(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("OTP sent successfully")
                .data(response)
                .build());
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "Verify phone OTP", description = "Verifies the OTP and returns a JWT or a registration token")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyPhoneOtp(@Valid @RequestBody PhoneOtpVerifyRequest request) {
        AuthResponse response = authService.verifyPhoneOtp(request);
        ResponseStatusCode statusCode = response.isRegistrationRequired() 
                ? ResponseStatusCode.REGISTRATION_REQUIRED 
                : ResponseStatusCode.SUCCESS;
        
        String message = response.isRegistrationRequired() 
                ? "Registration required" 
                : "Verification successful";

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(statusCode)
                .message(message)
                .data(response)
                .build());
    }

    @PostMapping("/phone/complete-registration")
    @Operation(summary = "Complete user registration", description = "Creates a new user account after phone verification")
    public ResponseEntity<ApiResponse<AuthResponse>> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest request) {
        AuthResponse response = authService.completeRegistration(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Registration completed successfully")
                .data(response)
                .build());
    }

    @PostMapping("/google")
    @Operation(summary = "Login with Google", description = "Authenticates a user using a Google ID token")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(
            @Valid @RequestBody SocialAuthRequest request
    ) {
        AuthResponse response = socialAuthService.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.PHONE_VERIFICATION_REQUIRED)
                .message("Social login successful, phone verification required")
                .data(response)
                .build());
    }

    @PostMapping("/apple")
    @Operation(summary = "Login with Apple", description = "Authenticates a user using an Apple ID token")
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithApple(
            @Valid @RequestBody SocialAuthRequest request
    ) {
        AuthResponse response = socialAuthService.loginWithApple(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.PHONE_VERIFICATION_REQUIRED)
                .message("Social login successful, phone verification required")
                .data(response)
                .build());
    }
}
