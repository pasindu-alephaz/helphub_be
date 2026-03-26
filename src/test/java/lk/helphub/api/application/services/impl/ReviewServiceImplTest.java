package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.ReviewRequest;
import lk.helphub.api.application.dto.ReviewResponse;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.JobReview;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.JobReviewRepository;
import lk.helphub.api.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private JobReviewRepository jobReviewRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User poster;
    private User provider;
    private Job job;
    private ReviewRequest reviewRequest;
    private UUID jobId;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
        poster = User.builder().id(UUID.randomUUID()).email("poster@example.com").fullName("Poster").build();
        provider = User.builder().id(UUID.randomUUID()).email("provider@example.com").fullName("Provider").build();
        job = Job.builder()
                .id(jobId)
                .title("Test Job")
                .postedBy(poster)
                .acceptedBy(provider)
                .status("COMPLETED")
                .build();
        reviewRequest = ReviewRequest.builder()
                .rating(5)
                .comment("Great service")
                .build();
    }

    @Test
    void submitProviderReview_Success() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(poster.getEmail())).thenReturn(Optional.of(poster));
        when(jobReviewRepository.existsByJobIdAndReviewerIdAndReviewType(any(), any(), any())).thenReturn(false);
        when(jobReviewRepository.save(any(JobReview.class))).thenAnswer(invocation -> {
            JobReview saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        ReviewResponse response = reviewService.submitProviderReview(jobId, poster.getEmail(), reviewRequest);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Great service", response.getComment());
        assertEquals(provider.getId(), response.getReviewedUserId());
        verify(jobReviewRepository, times(1)).save(any(JobReview.class));
    }

    @Test
    void submitProviderReview_Failure_JobNotCompleted() {
        job.setStatus("IN_PROGRESS");
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(poster.getEmail())).thenReturn(Optional.of(poster));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            reviewService.submitProviderReview(jobId, poster.getEmail(), reviewRequest));
        
        assertEquals("Can only review a completed job", exception.getMessage());
    }

    @Test
    void submitProviderReview_Failure_NotPoster() {
        User otherUser = User.builder().id(UUID.randomUUID()).email("other@example.com").build();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            reviewService.submitProviderReview(jobId, otherUser.getEmail(), reviewRequest));
        
        assertEquals("Only the job poster can submit a provider review", exception.getMessage());
    }

    @Test
    void submitProviderReview_Failure_Duplicate() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(poster.getEmail())).thenReturn(Optional.of(poster));
        when(jobReviewRepository.existsByJobIdAndReviewerIdAndReviewType(any(), any(), any())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            reviewService.submitProviderReview(jobId, poster.getEmail(), reviewRequest));
        
        assertEquals("You have already submitted a provider review for this job", exception.getMessage());
    }

    @Test
    void submitUserReview_Success() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(provider.getEmail())).thenReturn(Optional.of(provider));
        when(jobReviewRepository.existsByJobIdAndReviewerIdAndReviewType(any(), any(), any())).thenReturn(false);
        when(jobReviewRepository.save(any(JobReview.class))).thenAnswer(invocation -> {
            JobReview saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        ReviewResponse response = reviewService.submitUserReview(jobId, provider.getEmail(), reviewRequest);

        assertNotNull(response);
        assertEquals(poster.getId(), response.getReviewedUserId());
        assertEquals("USER", response.getReviewType());
        verify(jobReviewRepository, times(1)).save(any(JobReview.class));
    }

    @Test
    void submitUserReview_Failure_NotAssignedProvider() {
        User otherProvider = User.builder().id(UUID.randomUUID()).email("other-p@example.com").build();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(otherProvider.getEmail())).thenReturn(Optional.of(otherProvider));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            reviewService.submitUserReview(jobId, otherProvider.getEmail(), reviewRequest));
        
        assertEquals("Only the assigned provider can submit a user review", exception.getMessage());
    }
}
