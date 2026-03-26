package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    @Query(value = "SELECT j.* FROM jobs j WHERE " +
            "j.location_coordinates && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326) " +
            "AND (:subcategoryId IS NULL OR j.subcategory_id = :subcategoryId) " +
            "AND j.status = 'OPEN' AND j.deleted_at IS NULL", nativeQuery = true)
    List<Job> findNearbyJobs(@Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLon") BigDecimal minLon,
            @Param("maxLon") BigDecimal maxLon,
            @Param("subcategoryId") UUID subcategoryId);

    // Methods for user's own jobs management
    @EntityGraph(attributePaths = { "images" })
    Page<Job> findByPostedByEmailAndDeletedAtIsNull(String email, Pageable pageable);

    @EntityGraph(attributePaths = { "images" })
    Page<Job> findByPostedByEmailAndStatusAndDeletedAtIsNull(String email, String status, Pageable pageable);

    @EntityGraph(attributePaths = { "images" })
    Page<Job> findByAcceptedByEmailAndDeletedAtIsNull(String email, Pageable pageable);

    @EntityGraph(attributePaths = { "images" })
    Page<Job> findByAcceptedByEmailAndStatusAndDeletedAtIsNull(String email, String status, Pageable pageable);

    @EntityGraph(attributePaths = { "images" })
    Optional<Job> findByIdAndPostedByEmail(UUID id, String email);

    // Analytics queries
    long countByStatusAndDeletedAtIsNullAndCreatedAtBetween(String status, LocalDateTime from, LocalDateTime to);

    long countByUrgencyFlagAndDeletedAtIsNullAndCreatedAtBetween(String urgencyFlag, LocalDateTime from, LocalDateTime to);

    long countByDeletedAtIsNullAndCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT AVG(j.price) FROM Job j WHERE j.deletedAt IS NULL AND j.createdAt BETWEEN :from AND :to")
    BigDecimal findAveragePriceBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT j.subcategory.id, COUNT(j) FROM Job j " +
            "WHERE j.deletedAt IS NULL AND j.subcategory IS NOT NULL " +
            "GROUP BY j.subcategory.id ORDER BY COUNT(j) DESC")
    List<Object[]> findTopSubcategories(Pageable pageable);
}
