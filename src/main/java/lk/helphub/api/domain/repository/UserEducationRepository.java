package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.UserEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserEducationRepository extends JpaRepository<UserEducation, UUID> {
}
