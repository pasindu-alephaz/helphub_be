package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lk.helphub.api.domain.enums.Gender;
import lk.helphub.api.domain.enums.IdentityType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", length = 20)
    private IdentityType identityType;

    @Column(name = "identity_value", length = 100)
    private String identityValue;

    @Builder.Default
    @Column(name = "language_preference", length = 20)
    private String languagePreference = "SINHALA";

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(name = "delete_reason", columnDefinition = "TEXT")
    private String deleteReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_picture_id")
    private Image profilePicture;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_verification_id")
    private Image identityVerification;

    @Builder.Default
    @Column(name = "user_type", length = 20)
    private String userType = "customer";

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Builder.Default
    @Column(length = 20)
    private String status = "active";

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(name = "apple_id", length = 255)
    private String appleId;

    @Builder.Default
    @Column(name = "is_2fa_enabled")
    private boolean twoFactorEnabled = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserAddress> addresses = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserLanguage> userLanguages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserEducation> educationList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfessionalDetail professionalDetail;

    // Added explicitly as some code uses it
    public List<UserLanguage> getLanguages() {
        return userLanguages;
    }

    public String getProfileImageUrl() {
        return this.profilePicture != null ? this.profilePicture.getUrl() : null;
    }

    public void setProfileImageUrl(String url) {
        if (this.profilePicture == null) {
            this.profilePicture = new Image();
            this.profilePicture.setUser(this);
            this.profilePicture.setImageType("PROFILE_PICTURE");
        }
        this.profilePicture.setUrl(url);
    }
}
