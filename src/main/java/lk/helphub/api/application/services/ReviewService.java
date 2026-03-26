package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.ReviewRequest;
import lk.helphub.api.application.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    /**
     * Submit a review for a provider (user rates the provider after job completion)
     *
     * @param jobId       ID of the completed job
     * @param userEmail   Email of the user submitting the review
     * @param request     Review request containing rating and comment
     * @return Created review response
     */
    ReviewResponse submitProviderReview(UUID jobId, String userEmail, ReviewRequest request);

    /**
     * Submit a review for a user (provider rates the user after job completion)
     *
     * @param jobId        ID of the completed job
     * @param providerEmail Email of the provider submitting the review
     * @param request      Review request containing rating and comment
     * @return Created review response
     */
    ReviewResponse submitUserReview(UUID jobId, String providerEmail, ReviewRequest request);

    /**
     * Get all reviews for a specific job
     *
     * @param jobId ID of the job
     * @return List of all reviews for the job
     */
    List<ReviewResponse> getJobReviews(UUID jobId);

    /**
     * Get all reviews for a specific provider with pagination
     *
     * @param providerId ID of the provider
     * @param pageable   Pagination information
     * @return Paginated list of reviews for the provider
     */
    Page<ReviewResponse> getProviderReviews(UUID providerId, Pageable pageable);

    /**
     * Get all reviews written by a specific user with pagination
     *
     * @param userId   ID of the user
     * @param pageable Pagination information
     * @return Paginated list of reviews by the user
     */
    Page<ReviewResponse> getUserReviews(UUID userId, Pageable pageable);
}
