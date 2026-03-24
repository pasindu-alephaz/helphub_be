package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ProviderPortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderPortfolioImageRepository extends JpaRepository<ProviderPortfolioImage, UUID> {
}
