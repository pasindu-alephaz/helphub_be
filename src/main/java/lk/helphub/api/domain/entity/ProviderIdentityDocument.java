package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "document_type", length = 50, nullable = false)
    private String documentType; // e.g., NIC, PASSPORT, DRIVING_LICENSE

    @Column(name = "issuing_country", length = 100)
    private String issuingCountry;

    @Column(name = "document_code", length = 100)
    private String documentCode;

    @Builder.Default
    @OneToMany(mappedBy = "identityDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderIdentityImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
