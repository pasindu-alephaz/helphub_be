package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.RefreshTokenService;
import lk.helphub.api.domain.entity.RefreshToken;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import lk.helphub.api.infrastructure.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {

    private final AuthService authService;
    private final SocialAuthService socialAuthService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

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
    @Operation(summary = "Verify phone OTP", description = "Verifies the OTP and returns tokens or a registration token")
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

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Returns a new short-lived access token given a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        String identifier = refreshToken.getUser().getEmail() != null && !refreshToken.getUser().getEmail().isBlank()
                ? refreshToken.getUser().getEmail()
                : refreshToken.getUser().getPhoneNumber();
        UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);
        String newAccessToken = jwtUtil.generateToken(userDetails);

        AuthResponse response = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken()) // keep the same refresh token
                .build();

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Token refreshed successfully")
                .data(response)
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revokes the provided refresh token, logging the user out from that session")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Logged out successfully")
                .build());
    }
}
