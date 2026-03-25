package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "suggested_price", precision = 10, scale = 2)
    private BigDecimal suggestedPrice;

    @Column(name = "suggested_scheduled_at")
    private LocalDateTime suggestedScheduledAt;

    @Column(name = "suggestion_status", length = 20)
    private String suggestionStatus; // PENDING, ACCEPTED, REJECTED

    @Column(name = "suggested_availability_duration", length = 50)
    private String suggestedAvailabilityDuration;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
