package lk.helphub.api.application.services;

import lk.helphub.api.domain.entity.Permission;

import java.util.List;

public interface PermissionService {
    Permission getById(Integer id);
    List<Permission> getAll();
}
