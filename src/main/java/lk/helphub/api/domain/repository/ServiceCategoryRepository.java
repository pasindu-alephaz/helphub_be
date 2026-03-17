package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.ServiceCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceCategoryRepository {
    Optional<ServiceCategory> findById(UUID id);
    List<ServiceCategory> findAllByParentIsNullAndDeletedAtIsNull();
    List<ServiceCategory> findAllByDeletedAtIsNull();
    ServiceCategory save(ServiceCategory category);
    void deleteById(UUID id);
}
