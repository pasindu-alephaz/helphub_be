package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.UserProfessionalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface UserProfessionalDetailRepository extends JpaRepository<UserProfessionalDetail, UUID> {
    Optional<UserProfessionalDetail> findByUserId(UUID userId);
}
