package lk.helphub.api.presentation.controller;

import jakarta.validation.Valid;
import lk.helphub.api.application.AuthService;
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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody RegisterRequest request
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
}
