package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.ProviderSkillProof;
import lk.helphub.api.domain.repository.ProviderSkillProofRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProviderSkillProofRepository extends JpaRepository<ProviderSkillProof, UUID>, ProviderSkillProofRepository {
    List<ProviderSkillProof> findByProviderProfileId(UUID providerProfileId);
}
