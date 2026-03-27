package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.PhoneOtp;
import lk.helphub.api.domain.repository.PhoneOtpRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPhoneOtpRepository extends JpaRepository<PhoneOtp, UUID>, PhoneOtpRepository {
}
