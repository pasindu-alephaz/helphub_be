package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findById(UUID id);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByGoogleId(String googleId);
    
    Optional<User> findByAppleId(String appleId);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Find all non-deleted users with pagination
    Page<User> findByDeletedAtIsNull(Pageable pageable);
    
    // Find users by status (non-deleted)
    Page<User> findByStatusAndDeletedAtIsNull(String status, Pageable pageable);
    
    // Find users by email containing (case-insensitive)
    Page<User> findByEmailContainingIgnoreCaseAndDeletedAtIsNull(String email, Pageable pageable);
    
    // Find users by first name or last name containing (case-insensitive)
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "u.deletedAt IS NULL")
    Page<User> findByNameContainingAndDeletedAtIsNull(@Param("name") String name, Pageable pageable);
    
    // Find users by user type (non-deleted)
    Page<User> findByUserTypeAndDeletedAtIsNull(String userType, Pageable pageable);
    
    // Search users by multiple criteria
    @Query("SELECT u FROM User u WHERE " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:name IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:userType IS NULL OR u.userType = :userType) AND " +
           "u.deletedAt IS NULL")
    Page<User> searchUsers(
            @Param("email") String email,
            @Param("name") String name,
            @Param("status") String status,
            @Param("userType") String userType,
            Pageable pageable
    );
    
    // Count users by status (non-deleted)
    long countByStatusAndDeletedAtIsNull(String status);
    
    // Count total non-deleted users
    long countByDeletedAtIsNull();
    
    // Count users by user type (non-deleted)
    long countByUserTypeAndDeletedAtIsNull(String userType);
}
