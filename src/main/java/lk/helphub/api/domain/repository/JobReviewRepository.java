package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.JobReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobReviewRepository extends JpaRepository<JobReview, UUID> {

    List<JobReview> findByJobId(UUID jobId);

    Page<JobReview> findByReviewedUserId(UUID reviewedUserId, Pageable pageable);

    List<JobReview> findByReviewerId(UUID reviewerId);

    boolean existsByJobIdAndReviewerIdAndReviewType(UUID jobId, UUID reviewerId, JobReview.ReviewType reviewType);
}
