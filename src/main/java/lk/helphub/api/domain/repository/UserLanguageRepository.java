package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.UserLanguage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserLanguageRepository {
    List<UserLanguage> findByUserId(UUID userId);
    Optional<UserLanguage> findByIdAndUserId(UUID id, UUID userId);
    UserLanguage save(UserLanguage language);
    void delete(UserLanguage language);
}
