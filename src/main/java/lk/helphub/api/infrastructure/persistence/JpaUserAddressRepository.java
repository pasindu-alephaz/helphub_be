package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.UserAddress;
import lk.helphub.api.domain.repository.UserAddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserAddressRepository extends JpaRepository<UserAddress, UUID>, UserAddressRepository {

}
