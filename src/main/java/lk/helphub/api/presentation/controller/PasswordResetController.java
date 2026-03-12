package lk.helphub.api.presentation.controller;

import jakarta.validation.Valid;
import lk.helphub.api.application.PasswordResetService;
import lk.helphub.api.application.dto.ForgotPasswordRequest;
import lk.helphub.api.application.dto.ResetPasswordRequest;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and password management endpoints")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password", description = "Initiates a password reset by sending a reset OTP to the user's email")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset OTP sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or user not found")
    })
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Password reset OTP has been sent to your email")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Resets the user's password using the OTP received in email")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP, or invalid new password")
    })
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Password has been reset successfully")
                .build());
    }
}
