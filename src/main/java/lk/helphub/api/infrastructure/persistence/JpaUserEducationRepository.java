package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.UserEducation;
import lk.helphub.api.domain.repository.UserEducationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaUserEducationRepository extends JpaRepository<UserEducation, UUID>, UserEducationRepository {
}
