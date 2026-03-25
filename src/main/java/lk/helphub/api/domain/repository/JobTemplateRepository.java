package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.JobTemplate;
import lk.helphub.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobTemplateRepository extends JpaRepository<JobTemplate, UUID> {
    List<JobTemplate> findAllByUser(User user);
    List<JobTemplate> findByUserEmail(String email);
}
