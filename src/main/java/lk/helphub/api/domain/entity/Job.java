package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private ServiceCategory subcategory;

    @Column(name = "location_address", nullable = false, columnDefinition = "TEXT")
    private String locationAddress;

    @Column(name = "location_coordinates", columnDefinition = "geometry(Point,4326)")
    private Point locationCoordinates;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "job_type", length = 20)
    private String jobType;

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
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by")
    private User acceptedBy;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_images",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private Set<Image> images = new HashSet<>();

    @Builder.Default
    @Column(length = 20, nullable = false)
    private String status = "OPEN";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
