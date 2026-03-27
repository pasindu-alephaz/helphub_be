package lk.helphub.api.admin.application.services;

import lk.helphub.api.domain.entity.Role;

import java.util.List;

public interface RoleService {
    Role create(Role dto);
    Role getById(Integer id);
    List<Role> getAll();
    Role update(Integer id, Role dto);
    void delete(Integer id);
}
