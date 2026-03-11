package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    Role save(Role role);
}
