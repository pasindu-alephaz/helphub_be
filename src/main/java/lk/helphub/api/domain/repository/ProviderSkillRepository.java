package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderSkill;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface ProviderSkillRepository {
    ProviderSkill save(ProviderSkill skill);
    Optional<ProviderSkill> findById(UUID id);
    List<ProviderSkill> findByProviderProfileId(UUID providerProfileId);
    void deleteById(UUID id);
    void delete(ProviderSkill skill);
}
