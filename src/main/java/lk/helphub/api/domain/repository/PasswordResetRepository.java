package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.PasswordReset;
import lk.helphub.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {
    Optional<PasswordReset> findByOtp(String otp);
    List<PasswordReset> findByUserAndUsedAtIsNull(User user);
}
