package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.VerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationOtpRepository extends JpaRepository<VerificationOtp, UUID> {
    Optional<VerificationOtp> findByTokenAndOtp(String token, String otp);
    List<VerificationOtp> findByTargetAndTypeAndUsedAtIsNull(String target, String type);
}
