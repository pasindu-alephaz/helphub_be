package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"images"})
    Page<Job> findAll(Specification<Job> spec, @NonNull Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"images"})
    Optional<Job> findById(@NonNull UUID id);

    @EntityGraph(attributePaths = {"images"})
    @Query("SELECT j FROM Job j WHERE dwithin(j.locationCoordinates, :point, :radiusInDegrees) = true " +
           "AND (:subcategoryId IS NULL OR j.subcategory.id = :subcategoryId) " +
           "AND j.status = 'OPEN' AND j.deletedAt IS NULL")
    List<Job> findNearbyJobs(@Param("point") Point point,
                             @Param("radiusInDegrees") double radiusInDegrees,
                             @Param("subcategoryId") UUID subcategoryId);

    // Methods for user's own jobs management
    @EntityGraph(attributePaths = {"images"})
    Page<Job> findByPostedByEmailAndDeletedAtIsNull(String email, Pageable pageable);

    @EntityGraph(attributePaths = {"images"})
    Page<Job> findByPostedByEmailAndStatusAndDeletedAtIsNull(String email, String status, Pageable pageable);

    @EntityGraph(attributePaths = {"images"})
    Page<Job> findByAcceptedByEmailAndDeletedAtIsNull(String email, Pageable pageable);

    @EntityGraph(attributePaths = {"images"})
    Page<Job> findByAcceptedByEmailAndStatusAndDeletedAtIsNull(String email, String status, Pageable pageable);

    @EntityGraph(attributePaths = {"images"})
    Optional<Job> findByIdAndPostedByEmail(UUID id, String email);
}
