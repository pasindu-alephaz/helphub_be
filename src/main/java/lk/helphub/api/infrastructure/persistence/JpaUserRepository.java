package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByAppleId(String appleId);
    boolean existsByEmail(String email);
}
