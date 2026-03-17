package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.services.AuthService;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.LoginRequest;
import lk.helphub.api.application.dto.RegisterRequest;
import lk.helphub.api.application.dto.VerifyOtpRequest;
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
import lk.helphub.api.application.services.SocialAuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {

    private final AuthService authService;
    private final SocialAuthService socialAuthService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user in the system")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\",\n  \"errors\": {\n    \"email\": [\"must be a well-formed email address\"]\n  }\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"BAD_REQUEST\",\n  \"message\": \"User with this email already exists\"\n}")))
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("User registered successfully")
                .data(response)
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User logged in successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"UNAUTHORIZED\",\n  \"message\": \"Invalid email or password\"\n}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\",\n  \"errors\": {\n    \"email\": [\"must be a well-formed email address\"]\n  }\n}")))
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        String message = response.isTwoFactorRequired()
                ? "OTP sent to your email. Please verify to continue."
                : "User logged in successfully";

        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(response.isTwoFactorRequired() ? ResponseStatusCode.TWO_FA_REQUIRED : ResponseStatusCode.SUCCESS)
                .message(message)
                .data(response)
                .build());
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<ApiResponse<AuthResponse>> verify2fa(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        AuthResponse response = authService.verify2fa(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("2FA verified successfully")
                .data(response)
                .build());
    }

    @PostMapping("/google")
    @Operation(summary = "Login with Google", description = "Authenticates a user using a Google ID token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User logged in successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid token or request body",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"BAD_REQUEST\",\n  \"message\": \"Invalid token\"\n}")))
    })
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithGoogle(
            @Valid @RequestBody lk.helphub.api.application.dto.SocialAuthRequest request
    ) {
        AuthResponse response = socialAuthService.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Google login successful")
                .data(response)
                .build());
    }

    @PostMapping("/apple")
    @Operation(summary = "Login with Apple", description = "Authenticates a user using an Apple ID token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User logged in successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid token or request body",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"BAD_REQUEST\",\n  \"message\": \"Invalid token\"\n}")))
    })
    public ResponseEntity<ApiResponse<AuthResponse>> loginWithApple(
            @Valid @RequestBody lk.helphub.api.application.dto.SocialAuthRequest request
    ) {
        AuthResponse response = socialAuthService.loginWithApple(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Apple login successful")
                .data(response)
                .build());
    }
}
