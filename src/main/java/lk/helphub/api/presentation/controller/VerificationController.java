package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.SendVerificationRequest;
import lk.helphub.api.application.dto.SendVerificationResponse;
import lk.helphub.api.application.dto.VerifyOtpRequest;
import lk.helphub.api.application.services.VerificationService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/verification")
@RequiredArgsConstructor
@Tag(name = "Verification", description = "Email and phone number verification APIs")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/send")
    @Operation(summary = "Send verification OTP", description = "Sends a verification OTP to the provided email and/or phone number. Returns token(s) to use when verifying.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verification OTP sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request — at least one of email or phone number is required")
    })
    public ResponseEntity<ApiResponse<SendVerificationResponse>> sendVerificationOtp(
            @RequestBody SendVerificationRequest request
    ) {
        SendVerificationResponse response = verificationService.sendVerificationOtp(request);
        return ResponseEntity.ok(ApiResponse.<SendVerificationResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Verification OTP sent successfully")
                .data(response)
                .build());
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verifies an OTP code using the token received from the send endpoint")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verification successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid, expired, or already used OTP")
    })
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        verificationService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Verification successful")
                .build());
    }
}
