package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_educations")
public class UserEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "educational_level", length = 100, nullable = false)
    private String educationalLevel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificates_id")
    private Image certificate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserEducation() {}

    public UserEducation(UUID id, User user, String educationalLevel, Image certificate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.educationalLevel = educationalLevel;
        this.certificate = certificate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEducationalLevel() { return educationalLevel; }
    public void setEducationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; }

    public Image getCertificate() { return certificate; }
    public void setCertificate(Image certificate) { this.certificate = certificate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static UserEducationBuilder builder() {
        return new UserEducationBuilder();
    }

    public static class UserEducationBuilder {
        private UUID id;
        private User user;
        private String educationalLevel;
        private Image certificate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserEducationBuilder id(UUID id) { this.id = id; return this; }
        public UserEducationBuilder user(User user) { this.user = user; return this; }
        public UserEducationBuilder educationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; return this; }
        public UserEducationBuilder certificate(Image certificate) { this.certificate = certificate; return this; }
        public UserEducationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserEducationBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserEducation build() {
            return new UserEducation(id, user, educationalLevel, certificate, createdAt, updatedAt);
        }
    }
}
