package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.LoginOtp;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.LoginOtpRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaLoginOtpRepository extends JpaRepository<LoginOtp, UUID>, LoginOtpRepository {
}
