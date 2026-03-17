package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaServiceCategoryRepository extends JpaRepository<ServiceCategory, UUID>, ServiceCategoryRepository {
}
