package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderIdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderIdentityDocumentRepository extends JpaRepository<ProviderIdentityDocument, UUID> {
    java.util.Optional<ProviderIdentityDocument> findByProviderProfileId(UUID providerProfileId);
}
