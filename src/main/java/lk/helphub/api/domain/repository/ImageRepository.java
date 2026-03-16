package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Image;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository {
    Optional<Image> findById(UUID id);
    Image save(Image image);
}
