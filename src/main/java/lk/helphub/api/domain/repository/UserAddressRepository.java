package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserAddressRepository {
    List<UserAddress> findByUserId(@Param("userId") UUID userId);
    Optional<UserAddress> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(@Param("userId") UUID userId);
    void deleteByUser(@Param("user") User user);
    void delete(UserAddress entity);
    <S extends UserAddress> S save(S entity);
    <S extends UserAddress> List<S> saveAll(Iterable<S> entities);
}
