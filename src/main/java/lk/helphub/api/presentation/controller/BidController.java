package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.BidRequest;
import lk.helphub.api.application.dto.BidResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.BidService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs/{id}/bids")
@RequiredArgsConstructor
@Tag(name = "Job Bidding", description = "APIs for job bidding and quotes")
public class BidController {

    private final BidService bidService;

    @PostMapping
    @Operation(summary = "Submit a bid", description = "Provider submits a bid for a job")
    @PreAuthorize("hasAuthority('job_bid')")
    public ResponseEntity<ApiResponse<BidResponse>> submitBid(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody BidRequest request
    ) {
        BidResponse response = bidService.submitBid(id, principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BidResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Bid submitted successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{bidId}")
    @Operation(summary = "Adjust a bid", description = "Provider adjusts their submitted bid")
    @PreAuthorize("hasAuthority('job_bid')")
    public ResponseEntity<ApiResponse<BidResponse>> adjustBid(
            Principal principal,
            @PathVariable UUID id,
            @PathVariable UUID bidId,
            @Valid @RequestBody BidRequest request
    ) {
        BidResponse response = bidService.adjustBid(id, bidId, principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.<BidResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Bid adjusted successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @Operation(summary = "Get job bids", description = "User views all bids for their job")
    @PreAuthorize("hasAuthority('job_read')")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getJobBids(
            Principal principal,
            @PathVariable UUID id
    ) {
        List<BidResponse> response = bidService.getJobBids(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.<List<BidResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Bids retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{bidId}/accept")
    @Operation(summary = "Accept a bid", description = "User accepts a specific bid for the job")
    @PreAuthorize("hasAuthority('job_accept')")
    public ResponseEntity<ApiResponse<JobResponse>> acceptBid(
            Principal principal,
            @PathVariable UUID id,
            @PathVariable UUID bidId
    ) {
        JobResponse response = bidService.acceptBid(id, bidId, principal.getName());
        return ResponseEntity.ok(ApiResponse.<JobResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Bid accepted successfully. Job is now in progress.")
                .data(response)
                .build());
    }
}
