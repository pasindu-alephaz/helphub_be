package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.JobTimeSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobTimeSessionRepository extends JpaRepository<JobTimeSession, UUID> {
    List<JobTimeSession> findByJobIdOrderBySessionNumberAsc(UUID jobId);
}
