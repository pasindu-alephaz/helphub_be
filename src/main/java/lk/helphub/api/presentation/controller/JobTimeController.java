package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.JobTimeSessionService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Time Tracking", description = "Endpoints for OTP-verified job start/stop and time tracking")
public class JobTimeController {

    private final JobTimeSessionService jobTimeSessionService;

    @PostMapping("/{id}/start-otp")
    @Operation(summary = "Request OTP to start job", description = "Service provider requests an OTP to be sent to the customer to start the job")
    @PreAuthorize("hasAuthority('job_update')") // Provider role check usually handled by business logic but slug check helps
    public ResponseEntity<ApiResponse<Void>> requestStartOtp(
            Principal principal,
            @PathVariable UUID id
    ) {
        jobTimeSessionService.requestStartOtp(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Start OTP requested successfully. Please share the code with the customer.")
                .build());
    }

    @PostMapping("/{id}/verify-start")
    @Operation(summary = "Verify OTP and start job", description = "Customer verifies the OTP provided by the service provider to start the job session")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> verifyStartOtp(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody VerifyJobOtpRequest request
    ) {
        JobResponse response = jobTimeSessionService.verifyStartOtp(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job session started successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "Pause job", description = "Service provider pauses the current job session")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> pauseJob(
            Principal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) PauseJobRequest request
    ) {
        JobResponse response = jobTimeSessionService.pauseJob(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job session paused successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{id}/resume-otp")
    @Operation(summary = "Request OTP to resume job", description = "Service provider requests an OTP to resume a paused job")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<Void>> requestResumeOtp(
            Principal principal,
            @PathVariable UUID id
    ) {
        jobTimeSessionService.requestResumeOtp(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Resume OTP requested successfully. Please share the code with the customer.")
                .build());
    }

    @PostMapping("/{id}/verify-resume")
    @Operation(summary = "Verify OTP and resume job", description = "Customer verifies the OTP to resume the job session")
    @PreAuthorize("hasAuthority('job_update')")
    public ResponseEntity<ApiResponse<JobResponse>> verifyResumeOtp(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody VerifyJobOtpRequest request
    ) {
        JobResponse response = jobTimeSessionService.verifyResumeOtp(id, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Job session resumed successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}/time-summary")
    @Operation(summary = "Get job time summary", description = "Retrieve a summary of all work sessions and total time for a job")
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<TimeSummaryResponse>> getTimeSummary(@PathVariable UUID id) {
        TimeSummaryResponse response = jobTimeSessionService.getTimeSummary(id);
        return ResponseEntity.ok(ApiResponse.<TimeSummaryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Time summary retrieved successfully")
                .data(response)
                .build());
    }
}
