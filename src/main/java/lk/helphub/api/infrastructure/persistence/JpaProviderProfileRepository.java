package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.ProviderProfile;
import lk.helphub.api.domain.repository.ProviderProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProviderProfileRepository extends JpaRepository<ProviderProfile, UUID>, ProviderProfileRepository {
    Optional<ProviderProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);

    @org.springframework.data.jpa.repository.Query(value = 
        "SELECT p.* FROM provider_profiles p " +
        "JOIN users u ON p.user_id = u.id " +
        "JOIN user_addresses ua ON u.id = ua.user_id " +
        "WHERE ST_DWithin(ua.location::geography, " +
        "ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radiusMeters)", 
        nativeQuery = true)
    java.util.List<ProviderProfile> findNearby(
        @org.springframework.data.repository.query.Param("longitude") double longitude, 
        @org.springframework.data.repository.query.Param("latitude") double latitude, 
        @org.springframework.data.repository.query.Param("radiusMeters") double radiusMeters);
}
