package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderIdentityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderIdentityImageRepository extends JpaRepository<ProviderIdentityImage, UUID> {
}
