package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Image;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository {
    Optional<Image> findById(UUID id);
    Optional<Image> findByUrl(String url);
    Image save(Image image);
}
