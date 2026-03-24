package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lk.helphub.api.domain.enums.LanguageProficiency;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "language_code", length = 10)
    private String languageCode;

    @Column(name = "language_name", length = 100, nullable = false)
    private String languageName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private LanguageProficiency proficiency = LanguageProficiency.CONVERSATIONAL;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
