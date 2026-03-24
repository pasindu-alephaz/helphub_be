package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.UserAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository {
    List<UserAddress> findByUserId(UUID userId);
    Optional<UserAddress> findByIdAndUserId(UUID id, UUID userId);
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(UUID userId);
    UserAddress save(UserAddress address);
    void delete(UserAddress address);
}
