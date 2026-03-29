package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderProfile;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface ProviderProfileRepository {
    List<ProviderProfile> findAll();
    ProviderProfile save(ProviderProfile profile);
    Optional<ProviderProfile> findById(UUID id);
    Optional<ProviderProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    void deleteById(UUID id);
    List<ProviderProfile> findNearby(double longitude, double latitude, double radiusMeters);
}
