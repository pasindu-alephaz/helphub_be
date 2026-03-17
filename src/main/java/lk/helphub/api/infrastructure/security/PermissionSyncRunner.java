package lk.helphub.api.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import lk.helphub.api.domain.entity.Permission;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.repository.PermissionRepository;
import lk.helphub.api.domain.repository.RoleRepository;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionSyncRunner {

    private final ApplicationContext applicationContext;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    private static final Pattern HAS_AUTHORITY_PATTERN = Pattern.compile("hasAuthority\\('([^']+)'\\)");

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void syncPermissions() {
        log.info("Starting automated permission synchronization...");

        Set<String> requiredPermissions = new HashSet<>();
        
        // 1. Find all RestControllers
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);

        for (Object controller : controllers.values()) {
            Class<?> controllerClass = controller.getClass();
            
            // Note: Spring wraps classes with CGLIB proxies, so we should try to get the real class
            if (org.springframework.aop.support.AopUtils.isCglibProxy(controller)) {
                 controllerClass = org.springframework.aop.support.AopUtils.getTargetClass(controller);
            }

            // Check class-level PreAuthorize
            PreAuthorize classAuth = controllerClass.getAnnotation(PreAuthorize.class);
            if (classAuth != null) {
                extractPermissions(classAuth.value(), requiredPermissions);
            }

            // Check method-level PreAuthorize
            for (Method method : controllerClass.getDeclaredMethods()) {
                PreAuthorize methodAuth = method.getAnnotation(PreAuthorize.class);
                if (methodAuth != null) {
                    extractPermissions(methodAuth.value(), requiredPermissions);
                }
            }
        }

        if (requiredPermissions.isEmpty()) {
            log.info("No @PreAuthorize annotations found.");
            return;
        }

        log.info("Found {} unique permissions declared in code.", requiredPermissions.size());

        // 2. Sync with database
        int addedCount = 0;
        Set<Permission> newlyCreatedPermissions = new HashSet<>();

        for (String slug : requiredPermissions) {
            if (permissionRepository.findBySlug(slug).isEmpty()) {
                Permission newPermission = Permission.builder()
                        .slug(slug)
                        .build();
                permissionRepository.save(newPermission);
                newlyCreatedPermissions.add(newPermission);
                addedCount++;
                log.info("Inserted new permission: {}", slug);
            }
        }

        log.info("Permission sync completed. Inserted {} new permissions.", addedCount);

        // 3. Automatically assign new permissions to ADMIN role
        if (!newlyCreatedPermissions.isEmpty()) {
            roleRepository.findByName("ADMIN").ifPresentOrElse(adminRole -> {
                adminRole.getPermissions().addAll(newlyCreatedPermissions);
                roleRepository.save(adminRole);
                log.info("Automatically granted {} new permissions to the ADMIN role.", newlyCreatedPermissions.size());
            }, () -> log.warn("ADMIN role not found. Could not auto-assign new permissions."));
        }
    }

    private void extractPermissions(String expression, Set<String> permissions) {
        if (expression == null || expression.isBlank()) {
            return;
        }
        
        Matcher matcher = HAS_AUTHORITY_PATTERN.matcher(expression);
        while (matcher.find()) {
            permissions.add(matcher.group(1));
        }
    }
}
