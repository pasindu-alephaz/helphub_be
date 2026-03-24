package lk.helphub.api.admin.application.services.impl;

import lk.helphub.api.admin.application.services.RoleService;
import lk.helphub.api.domain.entity.Permission;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.repository.PermissionRepository;
import lk.helphub.api.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public Role create(Role dto) {
        if (roleRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Role already exists with name: " + dto.getName());
        }

        Role role = Role.builder()
                .name(dto.getName())
                .permissions(new HashSet<>())
                .build();

        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role update(Integer id, Role dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        roleRepository.findByName(dto.getName()).ifPresent(existingRole -> {
            if (!existingRole.getId().equals(id)) {
                throw new RuntimeException("Role already exists with name: " + dto.getName());
            }
        });

        role.setName(dto.getName());
        return roleRepository.save(role);
    }

    @Override
    public void delete(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    @Override
    public Role assignPermissionsToRole(Integer roleId, Set<Integer> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Set<Permission> permissions = new HashSet<>();
        for (Integer permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));
            permissions.add(permission);
        }

        role.setPermissions(permissions);
        return roleRepository.save(role);
    }
}
