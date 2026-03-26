package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.JobOtpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobOtpRequestRepository extends JpaRepository<JobOtpRequest, UUID> {
    Optional<JobOtpRequest> findByJobIdAndOtpCodeAndStatusAndExpiresAtAfter(
            UUID jobId, String otpCode, String status, LocalDateTime now);
    
    Optional<JobOtpRequest> findFirstByJobIdAndPurposeAndStatusOrderByCreatedAtDesc(
            UUID jobId, String purpose, String status);
}
