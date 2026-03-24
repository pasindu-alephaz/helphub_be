package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    @Query("SELECT j FROM Job j WHERE " +
           "j.latitude BETWEEN :minLat AND :maxLat AND " +
           "j.longitude BETWEEN :minLon AND :maxLon " +
           "AND (:subcategoryId IS NULL OR j.subcategory.id = :subcategoryId) " +
           "AND j.status = 'OPEN' AND j.deletedAt IS NULL")
    List<Job> findNearbyJobs(@Param("minLat") BigDecimal minLat,
                             @Param("maxLat") BigDecimal maxLat,
                             @Param("minLon") BigDecimal minLon,
                             @Param("maxLon") BigDecimal maxLon,
                             @Param("subcategoryId") UUID subcategoryId);
}
