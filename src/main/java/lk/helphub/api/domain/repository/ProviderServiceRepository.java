package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderServiceRepository extends JpaRepository<ProviderService, UUID> {
    List<ProviderService> findByProviderProfileId(UUID providerProfileId);
}
