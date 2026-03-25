package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.BidRequest;
import lk.helphub.api.application.dto.BidResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.BidService;
import lk.helphub.api.domain.entity.Bid;
import lk.helphub.api.domain.entity.Image;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.BidRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public BidResponse submitBid(UUID jobId, String userEmail, BidRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User provider = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (!"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Job is not open for bidding");
        }

        if (job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("You cannot bid on your own job");
        }

        // Check if provider already has a bid for this job
        bidRepository.findByJobIdAndProviderId(jobId, provider.getId()).ifPresent(b -> {
            throw new RuntimeException("You have already submitted a bid for this job. Use adjust bid instead.");
        });

        Bid bid = Bid.builder()
                .job(job)
                .provider(provider)
                .amount(request.getAmount())
                .proposal(request.getProposal())
                .jobAvailabilityDuration(request.getJobAvailabilityDuration())
                .status("PENDING")
                .build();

        Bid savedBid = bidRepository.save(bid);
        return mapToBidResponse(savedBid);
    }

    @Override
    public BidResponse adjustBid(UUID jobId, UUID bidId, String userEmail, BidRequest request) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));

        if (!bid.getJob().getId().equals(jobId)) {
            throw new RuntimeException("Bid does not belong to the specified job");
        }

        if (!bid.getProvider().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized to adjust this bid");
        }

        if (!"PENDING".equals(bid.getStatus())) {
            throw new RuntimeException("Only pending bids can be adjusted");
        }

        bid.setAmount(request.getAmount());
        bid.setProposal(request.getProposal());
        bid.setJobAvailabilityDuration(request.getJobAvailabilityDuration());

        Bid savedBid = bidRepository.save(bid);
        return mapToBidResponse(savedBid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidResponse> getJobBids(UUID jobId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Only poster or admin can see all bids? 
        // For now, let's allow only the poster.
        if (!job.getPostedBy().getEmail().equals(userEmail)) {
             throw new RuntimeException("Only the job poster can view all bids");
        }

        return bidRepository.findByJobIdOrderByAmountAsc(jobId)
                .stream()
                .map(this::mapToBidResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse acceptBid(UUID jobId, UUID bidId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Only the job poster can accept bids");
        }

        if (!"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Job is not open or already accepted/in progress");
        }

        Bid winningBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));

        if (!winningBid.getJob().getId().equals(jobId)) {
            throw new RuntimeException("Bid does not belong to this job");
        }

        // Accept this bid
        winningBid.setStatus("ACCEPTED");
        bidRepository.save(winningBid);

        // Reject other pending bids
        List<Bid> otherBids = bidRepository.findByJobIdOrderByAmountAsc(jobId);
        for (Bid other : otherBids) {
            if (!other.getId().equals(bidId) && "PENDING".equals(other.getStatus())) {
                other.setStatus("REJECTED");
                bidRepository.save(other);
            }
        }

        // Update Job
        job.setAcceptedBy(winningBid.getProvider());
        job.setPrice(winningBid.getAmount());
        job.setStatus("IN_PROGRESS");
        Job savedJob = jobRepository.save(job);

        return mapToJobResponse(savedJob);
    }

    private BidResponse mapToBidResponse(Bid bid) {
        return BidResponse.builder()
                .id(bid.getId())
                .jobId(bid.getJob().getId())
                .providerId(bid.getProvider().getId())
                .providerName(bid.getProvider().getFirstName() + " " + bid.getProvider().getLastName())
                .amount(bid.getAmount())
                .proposal(bid.getProposal())
                .jobAvailabilityDuration(bid.getJobAvailabilityDuration())
                .status(bid.getStatus())
                .createdAt(bid.getCreatedAt())
                .updatedAt(bid.getUpdatedAt())
                .build();
    }

    private JobResponse mapToJobResponse(Job job) {
        List<String> imageUrls = new ArrayList<>();
        if (job.getImages() != null) {
            imageUrls = job.getImages().stream().map(Image::getUrl).collect(Collectors.toList());
        }

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .subcategoryId(job.getSubcategory() != null ? job.getSubcategory().getId() : null)
                .locationAddress(job.getLocationAddress())
                .locationCoordinates(job.getLocationCoordinates() != null ? job.getLocationCoordinates().toText() : null)
                .price(job.getPrice())
                .scheduledAt(job.getScheduledAt())
                .jobType(job.getJobType())
                .preferredPrice(job.getPreferredPrice())
                .urgencyFlag(job.getUrgencyFlag())
                .status(job.getStatus())
                .postedBy(job.getPostedBy() != null ? job.getPostedBy().getId() : null)
                .acceptedBy(job.getAcceptedBy() != null ? job.getAcceptedBy().getId() : null)
                .imageUrls(imageUrls)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
