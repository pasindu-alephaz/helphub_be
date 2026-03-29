package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.ProviderIdentityDocument;
import lk.helphub.api.domain.repository.ProviderIdentityDocumentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProviderIdentityDocumentRepository extends JpaRepository<ProviderIdentityDocument, UUID>, ProviderIdentityDocumentRepository {
    List<ProviderIdentityDocument> findByProviderProfileId(UUID providerProfileId);
}
