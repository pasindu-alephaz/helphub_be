package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    User save(User user);
    boolean existsByEmail(String email);
}
