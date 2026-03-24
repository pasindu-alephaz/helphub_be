package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderCertificateRepository extends JpaRepository<ProviderCertificate, UUID> {
    List<ProviderCertificate> findByProviderProfileId(UUID providerProfileId);
}
