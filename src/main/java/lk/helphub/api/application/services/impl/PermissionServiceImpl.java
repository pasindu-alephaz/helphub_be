package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.services.PermissionService;
import lk.helphub.api.domain.entity.Permission;
import lk.helphub.api.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Permission getById(Integer id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
    }

    @Override
    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }
}
