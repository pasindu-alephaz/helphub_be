package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.ProviderSkill;
import lk.helphub.api.domain.repository.ProviderSkillRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProviderSkillRepository extends JpaRepository<ProviderSkill, UUID>, ProviderSkillRepository {
    List<ProviderSkill> findByProviderProfileId(UUID providerProfileId);
}
