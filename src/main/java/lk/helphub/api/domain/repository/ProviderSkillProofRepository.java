package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderSkillProof;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface ProviderSkillProofRepository {
    ProviderSkillProof save(ProviderSkillProof proof);
    Optional<ProviderSkillProof> findById(UUID id);
    List<ProviderSkillProof> findByProviderProfileId(UUID providerProfileId);
    void deleteById(UUID id);
    void delete(ProviderSkillProof proof);
}
