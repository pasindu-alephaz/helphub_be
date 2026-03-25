package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private ServiceCategory subcategory;

    @Column(name = "location_address", nullable = false, columnDefinition = "TEXT")
    private String locationAddress;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "job_type", length = 20)
    private String jobType;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "preferred_price", precision = 10, scale = 2)
    private BigDecimal preferredPrice;

    @Column(name = "urgency_flag", length = 20)
    private String urgencyFlag;

    @Column(name = "job_availability_duration", length = 50)
    private String jobAvailabilityDuration;

    @Column(name = "job_plan", length = 100)
    private String jobPlan;

    @Column(name = "preferred_language", length = 50)
    private String preferredLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
