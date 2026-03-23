package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByJobIdOrderByAmountAsc(UUID jobId);
    Optional<Bid> findByJobIdAndProviderId(UUID jobId, UUID providerId);
    Optional<Bid> findByJobIdAndProviderEmail(UUID jobId, String email);
}
