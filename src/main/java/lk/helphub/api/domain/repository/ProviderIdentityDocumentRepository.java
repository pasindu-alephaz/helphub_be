package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderIdentityDocument;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface ProviderIdentityDocumentRepository {
    ProviderIdentityDocument save(ProviderIdentityDocument document);
    Optional<ProviderIdentityDocument> findById(UUID id);
    List<ProviderIdentityDocument> findByProviderProfileId(UUID providerProfileId);
    void deleteById(UUID id);
    void delete(ProviderIdentityDocument document);
}
