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
@Data
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

    @Column(name = "id_type", length = 20, nullable = false)
    private String idType; // NIC, PASSPORT, LICENSE

    @Column(name = "id_number", length = 100, nullable = false)
    private String idNumber;

    @Column(name = "issuing_country", length = 100)
    private String issuingCountry;

    @Column(name = "issuing_country_code", length = 10)
    private String issuingCountryCode;

    @Builder.Default
    @Column(length = 20, nullable = false)
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProviderIdentityImage> images = new ArrayList<>();
}
