package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.JobTimeSessionService;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.JobOtpRequest;
import lk.helphub.api.domain.entity.JobTimeSession;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.JobOtpRequestRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.JobTimeSessionRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobTimeSessionServiceImpl implements JobTimeSessionService {

    private final JobRepository jobRepository;
    private final JobTimeSessionRepository sessionRepository;
    private final JobOtpRequestRepository otpRepository;
    private final UserRepository userRepository;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 3;

    @Override
    public void requestStartOtp(UUID jobId, String userEmail) {
        Job job = getJob(jobId);
        User provider = getUserByEmail(userEmail);

        validateProvider(job, provider);
        
        if (!"IN_PROGRESS".equals(job.getStatus()) && !"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Job must be OPEN or IN_PROGRESS to start. Current status: " + job.getStatus());
        }

        String otpCode = generateOtp();
        
        JobOtpRequest otpRequest = JobOtpRequest.builder()
                .job(job)
                .otpCode(otpCode)
                .purpose("START")
                .status("PENDING")
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .requestedBy(provider)
                .build();

        otpRepository.save(otpRequest);
        job.setStatus("OTP_PENDING_START");
        jobRepository.save(job);

        log.info("Job OTP for job {}: {}", jobId, otpCode);
    }

    @Override
    public JobResponse verifyStartOtp(UUID jobId, String userEmail, VerifyJobOtpRequest request) {
        Job job = getJob(jobId);
        User customer = getUserByEmail(userEmail);

        validateCustomer(job, customer);

        JobOtpRequest otpRequest = otpRepository.findFirstByJobIdAndPurposeAndStatusOrderByCreatedAtDesc(
                jobId, "START", "PENDING")
                .orElseThrow(() -> new RuntimeException("No pending start OTP request found for this job"));

        verifyOtp(otpRequest, request.getOtpCode(), customer);

        // Start new session
        JobTimeSession session = JobTimeSession.builder()
                .job(job)
                .sessionNumber(getNextSessionNumber(jobId))
                .startedAt(LocalDateTime.now())
                .otpVerified(true)
                .build();

        JobTimeSession savedSession = sessionRepository.save(session);
        job.setCurrentSession(savedSession);
        job.setStatus("IN_PROGRESS");
        Job savedJob = jobRepository.save(job);

        return mapToJobResponse(savedJob);
    }

    @Override
    public JobResponse pauseJob(UUID jobId, String userEmail, PauseJobRequest request) {
        Job job = getJob(jobId);
        User provider = getUserByEmail(userEmail);

        validateProvider(job, provider);

        if (!"IN_PROGRESS".equals(job.getStatus())) {
            throw new RuntimeException("Only jobs in progress can be paused");
        }

        JobTimeSession currentSession = job.getCurrentSession();
        if (currentSession == null) {
            throw new RuntimeException("No active session found for this job");
        }

        LocalDateTime now = LocalDateTime.now();
        currentSession.setEndedAt(now);
        currentSession.setPauseReason(request != null ? request.getReason() : null);
        
        int minutes = (int) Duration.between(currentSession.getStartedAt(), now).toMinutes();
        currentSession.setTotalMinutes(minutes);
        sessionRepository.save(currentSession);

        job.setTotalWorkMinutes(job.getTotalWorkMinutes() + minutes);
        job.setCurrentSession(null);
        job.setStatus("PAUSED");
        Job savedJob = jobRepository.save(job);

        return mapToJobResponse(savedJob);
    }

    @Override
    public void requestResumeOtp(UUID jobId, String userEmail) {
        Job job = getJob(jobId);
        User provider = getUserByEmail(userEmail);

        validateProvider(job, provider);

        if (!"PAUSED".equals(job.getStatus())) {
            throw new RuntimeException("Only paused jobs can be resumed");
        }

        String otpCode = generateOtp();

        JobOtpRequest otpRequest = JobOtpRequest.builder()
                .job(job)
                .otpCode(otpCode)
                .purpose("RESUME")
                .status("PENDING")
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .requestedBy(provider)
                .build();

        otpRepository.save(otpRequest);
        job.setStatus("OTP_PENDING_RESUME");
        jobRepository.save(job);

        log.info("Job OTP for job {}: {}", jobId, otpCode);
    }

    @Override
    public JobResponse verifyResumeOtp(UUID jobId, String userEmail, VerifyJobOtpRequest request) {
        Job job = getJob(jobId);
        User customer = getUserByEmail(userEmail);

        validateCustomer(job, customer);

        JobOtpRequest otpRequest = otpRepository.findFirstByJobIdAndPurposeAndStatusOrderByCreatedAtDesc(
                jobId, "RESUME", "PENDING")
                .orElseThrow(() -> new RuntimeException("No pending resume OTP request found for this job"));

        verifyOtp(otpRequest, request.getOtpCode(), customer);

        // Start new session
        JobTimeSession session = JobTimeSession.builder()
                .job(job)
                .sessionNumber(getNextSessionNumber(jobId))
                .startedAt(LocalDateTime.now())
                .otpVerified(true)
                .build();

        JobTimeSession savedSession = sessionRepository.save(session);
        job.setCurrentSession(savedSession);
        job.setStatus("IN_PROGRESS");
        Job savedJob = jobRepository.save(job);

        return mapToJobResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSummaryResponse getTimeSummary(UUID jobId) {
        Job job = getJob(jobId);
        List<JobTimeSession> sessions = sessionRepository.findByJobIdOrderBySessionNumberAsc(jobId);

        return TimeSummaryResponse.builder()
                .jobId(jobId)
                .totalWorkMinutes(job.getTotalWorkMinutes())
                .sessions(sessions.stream().map(s -> TimeSummaryResponse.SessionSummary.builder()
                        .id(s.getId())
                        .sessionNumber(s.getSessionNumber())
                        .startedAt(s.getStartedAt())
                        .endedAt(s.getEndedAt())
                        .durationMinutes(s.getTotalMinutes())
                        .pauseReason(s.getPauseReason())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private Job getJob(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
    }

    private User getUserByEmail(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + identifier));
    }

    private void validateProvider(Job job, User provider) {
        if (job.getAcceptedBy() == null || !job.getAcceptedBy().getId().equals(provider.getId())) {
            throw new RuntimeException("Only the accepted provider can perform this action");
        }
    }

    private void validateCustomer(Job job, User customer) {
        if (!job.getPostedBy().getId().equals(customer.getId())) {
            throw new RuntimeException("Only the job poster can verify the OTP");
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }

    private void verifyOtp(JobOtpRequest otpRequest, String code, User verifiedBy) {
        if (otpRequest.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRequest.setStatus("EXPIRED");
            otpRepository.save(otpRequest);
            throw new RuntimeException("OTP has expired");
        }

        if (!otpRequest.getOtpCode().equals(code)) {
            otpRequest.setAttempts(otpRequest.getAttempts() + 1);
            if (otpRequest.getAttempts() >= MAX_OTP_ATTEMPTS) {
                otpRequest.setStatus("FAILED");
            }
            otpRepository.save(otpRequest);
            throw new RuntimeException("Invalid OTP code");
        }

        otpRequest.setStatus("VERIFIED");
        otpRequest.setVerifiedBy(verifiedBy);
        otpRequest.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otpRequest);
    }

    private int getNextSessionNumber(UUID jobId) {
        List<JobTimeSession> sessions = sessionRepository.findByJobIdOrderBySessionNumberAsc(jobId);
        return sessions.size() + 1;
    }

    // This should ideally use a mapper, but copying logic from JobServiceImpl for now
    private JobResponse mapToJobResponse(Job job) {
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
                .jobAvailabilityDuration(job.getJobAvailabilityDuration())
                .jobPlan(job.getJobPlan())
                .preferredLanguage(job.getPreferredLanguage())
                .urgencyFlag(job.getUrgencyFlag())
                .status(job.getStatus())
                .currentSessionId(job.getCurrentSession() != null ? job.getCurrentSession().getId() : null)
                .totalWorkMinutes(job.getTotalWorkMinutes())
                .postedBy(job.getPostedBy() != null ? job.getPostedBy().getId() : null)
                .acceptedBy(job.getAcceptedBy() != null ? job.getAcceptedBy().getId() : null)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .flagged(job.isFlagged())
                .flagReason(job.getFlagReason())
                .archivedAt(job.getArchivedAt())
                .build();
    }
}
