package lk.helphub.api.admin.application.services;

import lk.helphub.api.admin.application.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminUserService {

    /**
     * Create a new user (admin action)
     */
    AdminUserResponse createUser(AdminUserCreateRequest request);

    /**
     * Update an existing user (admin action)
     */
    AdminUserResponse updateUser(UUID userId, AdminUserUpdateRequest request);

    /**
     * Get a user by ID
     */
    AdminUserResponse getUserById(UUID userId);

    /**
     * Get a user by email
     */
    AdminUserResponse getUserByEmail(String email);

    /**
     * Get a user by phone number
     */
    AdminUserResponse getUserByPhone(String phoneNumber);

    /**
     * List all users with pagination
     */
    Page<AdminUserResponse> getAllUsers(Pageable pageable);

    /**
     * Search users by multiple criteria
     */
    Page<AdminUserResponse> searchUsers(String email, String name, String status, String userType, Pageable pageable);

    /**
     * Soft delete a user
     */
    void deleteUser(UUID userId, AdminUserDeleteRequest request);

    /**
     * Get user statistics
     */
    UserStatistics getUserStatistics();
    
    /**
     * User statistics data class
     */
    record UserStatistics(
            long totalUsers,
            long activeUsers,
            long inactiveUsers,
            long suspendedUsers,
            long customerCount,
            long providerCount
    ) {}
}
