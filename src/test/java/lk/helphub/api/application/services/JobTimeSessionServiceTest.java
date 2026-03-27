package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.VerifyJobOtpRequest;
import lk.helphub.api.application.services.impl.JobTimeSessionServiceImpl;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.JobOtpRequest;
import lk.helphub.api.domain.entity.JobTimeSession;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.JobOtpRequestRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.JobTimeSessionRepository;
import lk.helphub.api.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobTimeSessionServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobTimeSessionRepository sessionRepository;
    @Mock
    private JobOtpRequestRepository otpRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobTimeSessionServiceImpl jobTimeSessionService;

    private UUID jobId;
    private String providerEmail = "provider@example.com";
    private String customerEmail = "customer@example.com";
    private User provider;
    private User customer;
    private Job job;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
        provider = User.builder().id(UUID.randomUUID()).email(providerEmail).build();
        customer = User.builder().id(UUID.randomUUID()).email(customerEmail).build();
        job = Job.builder()
                .id(jobId)
                .postedBy(customer)
                .acceptedBy(provider)
                .status("IN_PROGRESS")
                .build();
    }

    @Test
    void testRequestStartOtp() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(providerEmail)).thenReturn(Optional.of(provider));

        jobTimeSessionService.requestStartOtp(jobId, providerEmail);

        assertEquals("OTP_PENDING_START", job.getStatus());
        verify(otpRepository, times(1)).save(any(JobOtpRequest.class));
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    void testVerifyStartOtp_Success() {
        JobOtpRequest otpRequest = JobOtpRequest.builder()
                .job(job)
                .otpCode("123456")
                .purpose("START")
                .status("PENDING")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(customerEmail)).thenReturn(Optional.of(customer));
        when(otpRepository.findFirstByJobIdAndPurposeAndStatusOrderByCreatedAtDesc(jobId, "START", "PENDING"))
                .thenReturn(Optional.of(otpRequest));
        when(sessionRepository.findByJobIdOrderBySessionNumberAsc(jobId)).thenReturn(Collections.emptyList());
        when(sessionRepository.save(any(JobTimeSession.class))).thenAnswer(i -> i.getArgument(0));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));

        VerifyJobOtpRequest verifyRequest = new VerifyJobOtpRequest("123456");
        JobResponse response = jobTimeSessionService.verifyStartOtp(jobId, customerEmail, verifyRequest);

        assertEquals("IN_PROGRESS", response.getStatus());
        assertEquals("VERIFIED", otpRequest.getStatus());
        assertNotNull(job.getCurrentSession());
        verify(sessionRepository, times(1)).save(any(JobTimeSession.class));
    }

    @Test
    void testPauseJob_Success() {
        JobTimeSession currentSession = JobTimeSession.builder()
                .id(UUID.randomUUID())
                .job(job)
                .startedAt(LocalDateTime.now().minusMinutes(30))
                .build();
        job.setCurrentSession(currentSession);
        job.setStatus("IN_PROGRESS");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(providerEmail)).thenReturn(Optional.of(provider));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));

        jobTimeSessionService.pauseJob(jobId, providerEmail, null);

        assertEquals("PAUSED", job.getStatus());
        assertNull(job.getCurrentSession());
        assertEquals(30, job.getTotalWorkMinutes());
        assertNotNull(currentSession.getEndedAt());
        assertEquals(30, currentSession.getTotalMinutes());
        verify(sessionRepository, times(1)).save(currentSession);
        verify(jobRepository, times(1)).save(job);
    }
}
