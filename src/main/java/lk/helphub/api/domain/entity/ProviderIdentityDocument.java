package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lk.helphub.api.domain.enums.IdentityType;
import lk.helphub.api.domain.enums.VerificationStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "provider_identity_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderIdentityDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_profile_id", nullable = false)
    private ProviderProfile providerProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", nullable = false, length = 20)
    private IdentityType idType;

    @Column(name = "id_number", nullable = false, length = 100)
    private String idNumber;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<ProviderIdentityImage> images = new java.util.ArrayList<>();

    @Column(name = "selfie_image_url", columnDefinition = "TEXT")
    private String selfieImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
