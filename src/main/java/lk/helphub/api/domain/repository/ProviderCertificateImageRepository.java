package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderCertificateImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderCertificateImageRepository extends JpaRepository<ProviderCertificateImage, UUID> {
}
