package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderProfile;
import lk.helphub.api.domain.entity.ProviderService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, UUID> {
    List<ProviderService> findByProviderProfile(ProviderProfile providerProfile);
    void deleteByProviderProfile(ProviderProfile providerProfile);
}
