package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByAppleId(String appleId);
    User save(User user);
    boolean existsByEmail(String email);
}
