package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderPortfolioItem;
import lk.helphub.api.domain.entity.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProviderPortfolioItemRepository extends JpaRepository<ProviderPortfolioItem, UUID> {
    List<ProviderPortfolioItem> findByProviderProfile(ProviderProfile providerProfile);
    void deleteByProviderProfile(ProviderProfile profile);
}
