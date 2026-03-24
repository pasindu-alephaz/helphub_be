package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Notification;
import lk.helphub.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user, Pageable pageable);

    Optional<Notification> findByIdAndUserAndDeletedAtIsNull(UUID id, User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false AND n.deletedAt IS NULL")
    void markAllAsReadForUser(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.deletedAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.user = :user")
    void softDeleteByIdAndUser(UUID id, User user);
}
