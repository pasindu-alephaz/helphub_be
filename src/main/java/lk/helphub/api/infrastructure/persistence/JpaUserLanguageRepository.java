package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.UserLanguage;
import lk.helphub.api.domain.repository.UserLanguageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserLanguageRepository extends JpaRepository<UserLanguage, UUID>, UserLanguageRepository {

    List<UserLanguage> findByUserId(UUID userId);

    Optional<UserLanguage> findByIdAndUserId(UUID id, UUID userId);
}
