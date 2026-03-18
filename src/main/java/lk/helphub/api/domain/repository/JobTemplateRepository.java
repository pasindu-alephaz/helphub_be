package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.JobTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobTemplateRepository extends JpaRepository<JobTemplate, UUID> {
}
