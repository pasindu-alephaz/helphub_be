package lk.helphub.api.admin.application.services.impl;

import lk.helphub.api.admin.application.dto.*;
import lk.helphub.api.admin.application.services.AdminUserService;
import lk.helphub.api.application.services.MailService;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.RoleRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    @Transactional
    public AdminUserResponse createUser(AdminUserCreateRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Validate phone uniqueness if provided
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
            }
        }

        // Build user entity
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFirstName() != null && request.getLastName() != null 
                    ? request.getFirstName() + " " + request.getLastName() 
                    : request.getFirstName() != null ? request.getFirstName() : request.getLastName())
                .displayName(request.getFirstName())
                .phoneNumber(request.getPhoneNumber())
                .birthday(request.getDateOfBirth())
                .identityType(request.getIdentityType())
                .identityValue(request.getIdentityValue())
                .languagePreference(request.getLanguagePreference() != null ? request.getLanguagePreference() : "SINHALA")
                .about(request.getBio())
                .userType(request.getUserType() != null ? request.getUserType() : "customer")
                .status(request.getStatus() != null ? request.getStatus() : "active")
                .build();

        // Assign roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        log.info("Admin created new user: {} ({})", savedUser.getEmail(), savedUser.getId());

        // Send welcome email if requested
        if (request.isSendWelcomeEmail()) {
            try {
                mailService.sendMail(
                        savedUser.getEmail(),
                        "Welcome to HelpHub",
                        "Your account has been created successfully. You can now login to HelpHub using your email and password."
                );
            } catch (Exception e) {
                log.warn("Failed to send welcome email to {}: {}", savedUser.getEmail(), e.getMessage());
            }
        }

        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(UUID userId, AdminUserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // Update basic fields
        if (request.getFirstName() != null || request.getLastName() != null) {
            String currentFullName = user.getFullName() != null ? user.getFullName() : "";
            String[] nameParts = currentFullName.split(" ", 2);
            String firstName = request.getFirstName() != null ? request.getFirstName() : nameParts[0];
            String lastName = request.getLastName() != null ? request.getLastName() : (nameParts.length > 1 ? nameParts[1] : "");
            user.setFullName(firstName + " " + lastName);
            user.setDisplayName(firstName);
        }
        if (request.getPhoneNumber() != null) {
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) && 
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            user.setBirthday(request.getDateOfBirth());
        }
        if (request.getIdentityType() != null) {
            user.setIdentityType(request.getIdentityType());
        }
        if (request.getIdentityValue() != null) {
            user.setIdentityValue(request.getIdentityValue());
        }
        if (request.getLanguagePreference() != null) {
            user.setLanguagePreference(request.getLanguagePreference());
        }
        if (request.getBio() != null) {
            user.setAbout(request.getBio());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getUserType() != null) {
            user.setUserType(request.getUserType());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getIs2faEnabled() != null) {
            user.setTwoFactorEnabled(request.getIs2faEnabled());
        }

        // Update roles if provided
        if (request.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        log.info("Admin updated user: {} ({})", updatedUser.getEmail(), updatedUser.getId());

        return mapToAdminUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserByPhone(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phoneNumber));
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByDeletedAtIsNull(pageable)
                .map(this::mapToAdminUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> searchUsers(String email, String name, String status, String userType, Pageable pageable) {
        return userRepository.searchUsers(email, name, status, userType, pageable)
                .map(this::mapToAdminUserResponse);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId, AdminUserDeleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already deleted
        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("User is already deleted");
        }

        // Soft delete
        user.setDeleteReason(request.getDeleteReason());
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus("deleted");
        userRepository.save(user);

        log.info("Admin soft-deleted user: {} ({}). Reason: {}", user.getEmail(), user.getId(), request.getDeleteReason());

        // Send notification email if requested
        if (request.isNotifyUser()) {
            try {
                mailService.sendMail(
                        user.getEmail(),
                        "Your HelpHub Account Has Been Deleted",
                        "Your account has been deleted. Reason: " + request.getDeleteReason() +
                        "\n\nIf you believe this was a mistake, please contact support."
                );
            } catch (Exception e) {
                log.warn("Failed to send deletion notification to {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        return new UserStatistics(
                userRepository.countByDeletedAtIsNull(),
                userRepository.countByStatusAndDeletedAtIsNull("active"),
                userRepository.countByStatusAndDeletedAtIsNull("inactive"),
                userRepository.countByStatusAndDeletedAtIsNull("suspended"),
                userRepository.countByUserTypeAndDeletedAtIsNull("customer"),
                userRepository.countByUserTypeAndDeletedAtIsNull("provider")
        );
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
                : List.of();

        String firstName = "";
        String lastName = "";
        if (user.getFullName() != null) {
            String[] nameParts = user.getFullName().split(" ", 2);
            firstName = nameParts[0];
            lastName = nameParts.length > 1 ? nameParts[1] : "";
        }
        
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getBirthday())
                .identityType(user.getIdentityType())
                .identityValue(user.getIdentityValue())
                .languagePreference(user.getLanguagePreference())
                .bio(user.getAbout())
                .profileImageUrl(user.getProfileImageUrl())
                .userType(user.getUserType())
                .status(user.getStatus())
                .roles(roleNames)
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .verifiedAt(user.getVerifiedAt())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .phoneVerifiedAt(user.getPhoneVerifiedAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .deleteReason(user.getDeleteReason())
                .build();
    }
}
