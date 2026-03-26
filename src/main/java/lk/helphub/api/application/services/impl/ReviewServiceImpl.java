package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.ReviewRequest;
import lk.helphub.api.application.dto.ReviewResponse;
import lk.helphub.api.application.services.ReviewService;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.JobReview;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.JobReviewRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ReviewServiceImpl implements ReviewService {

    private final JobReviewRepository jobReviewRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse submitProviderReview(UUID jobId, String userEmail, ReviewRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User reviewer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // Validate job is completed
        if (!"COMPLETED".equals(job.getStatus())) {
            throw new RuntimeException("Can only review a completed job");
        }

        // Validate the reviewer is the job poster
        if (!job.getPostedBy().getId().equals(reviewer.getId())) {
            throw new RuntimeException("Only the job poster can submit a provider review");
        }

        // Validate the reviewed user is the accepted provider
        if (job.getAcceptedBy() == null) {
            throw new RuntimeException("No provider was assigned to this job");
        }

        // Check for duplicate review
        if (jobReviewRepository.existsByJobIdAndReviewerIdAndReviewType(
                jobId, reviewer.getId(), JobReview.ReviewType.PROVIDER)) {
            throw new RuntimeException("You have already submitted a provider review for this job");
        }

        JobReview review = JobReview.builder()
                .job(job)
                .reviewer(reviewer)
                .reviewedUser(job.getAcceptedBy())
                .rating(request.getRating())
                .comment(request.getComment())
                .mediaUrls(request.getMediaUrls() != null ? request.getMediaUrls() : new ArrayList<>())
                .reviewType(JobReview.ReviewType.PROVIDER)
                .build();

        JobReview savedReview = jobReviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }

    @Override
    public ReviewResponse submitUserReview(UUID jobId, String providerEmail, ReviewRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with email: " + providerEmail));

        // Validate job is completed
        if (!"COMPLETED".equals(job.getStatus())) {
            throw new RuntimeException("Can only review a completed job");
        }

        // Validate the provider is the one who was assigned to the job
        if (job.getAcceptedBy() == null || !job.getAcceptedBy().getId().equals(provider.getId())) {
            throw new RuntimeException("Only the assigned provider can submit a user review");
        }

        // Check for duplicate review
        if (jobReviewRepository.existsByJobIdAndReviewerIdAndReviewType(
                jobId, provider.getId(), JobReview.ReviewType.USER)) {
            throw new RuntimeException("You have already submitted a user review for this job");
        }

        JobReview review = JobReview.builder()
                .job(job)
                .reviewer(provider)
                .reviewedUser(job.getPostedBy())
                .rating(request.getRating())
                .comment(request.getComment())
                .mediaUrls(request.getMediaUrls() != null ? request.getMediaUrls() : new ArrayList<>())
                .reviewType(JobReview.ReviewType.USER)
                .build();

        JobReview savedReview = jobReviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getJobReviews(UUID jobId) {
        // Verify job exists
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        return jobReviewRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProviderReviews(UUID providerId, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Provider not found with id: " + providerId);
        }

        return jobReviewRepository.findByReviewedUserId(providerId, pageable)
                .map(this::mapToReviewResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(UUID userId, Pageable pageable) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return jobReviewRepository.findByReviewedUserId(userId, pageable)
                .map(this::mapToReviewResponse);
    }

    private ReviewResponse mapToReviewResponse(JobReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .jobId(review.getJob().getId())
                .jobTitle(review.getJob().getTitle())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getFullName())
                .reviewerProfileImage(review.getReviewer().getProfileImageUrl())
                .reviewedUserId(review.getReviewedUser().getId())
                .reviewedUserName(review.getReviewedUser().getFullName())
                .reviewedUserProfileImage(review.getReviewedUser().getProfileImageUrl())
                .rating(review.getRating())
                .comment(review.getComment())
                .mediaUrls(review.getMediaUrls())
                .reviewType(review.getReviewType().name())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
