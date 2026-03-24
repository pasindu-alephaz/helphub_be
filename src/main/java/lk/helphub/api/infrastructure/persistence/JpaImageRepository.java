package lk.helphub.api.infrastructure.persistence;

import lk.helphub.api.domain.entity.Image;
import lk.helphub.api.domain.repository.ImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaImageRepository extends JpaRepository<Image, UUID>, ImageRepository {
    @Override
    Image save(Image image);
}
