package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    @Query("SELECT j FROM Job j WHERE dwithin(j.locationCoordinates, :point, :radiusInDegrees) = true " +
           "AND (:subcategoryId IS NULL OR j.subcategory.id = :subcategoryId) " +
           "AND j.status = 'OPEN' AND j.deletedAt IS NULL")
    List<Job> findNearbyJobs(@Param("point") Point point,
                             @Param("radiusInDegrees") double radiusInDegrees,
                             @Param("subcategoryId") UUID subcategoryId);
}
