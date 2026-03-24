package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.BidRequest;
import lk.helphub.api.application.dto.BidResponse;
import lk.helphub.api.application.dto.JobResponse;

import java.util.List;
import java.util.UUID;

public interface BidService {
    BidResponse submitBid(UUID jobId, String userEmail, BidRequest request);
    BidResponse adjustBid(UUID jobId, UUID bidId, String userEmail, BidRequest request);
    List<BidResponse> getJobBids(UUID jobId, String userEmail);
    JobResponse acceptBid(UUID jobId, UUID bidId, String userEmail);
}
