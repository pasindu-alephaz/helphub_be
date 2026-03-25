package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderPortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderPortfolioItemRepository extends JpaRepository<ProviderPortfolioItem, UUID> {
    List<ProviderPortfolioItem> findByProviderProfileId(UUID providerProfileId);
}
