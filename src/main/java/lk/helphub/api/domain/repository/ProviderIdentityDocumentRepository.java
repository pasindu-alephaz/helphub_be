package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderIdentityDocument;
import lk.helphub.api.domain.entity.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProviderIdentityDocumentRepository extends JpaRepository<ProviderIdentityDocument, UUID> {
    List<ProviderIdentityDocument> findByProviderProfile(ProviderProfile providerProfile);
    void deleteByProviderProfile(ProviderProfile providerProfile);
}
