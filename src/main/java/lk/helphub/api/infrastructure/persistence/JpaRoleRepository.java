package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRoleRepository extends JpaRepository<Role, Integer>, RoleRepository {
}
