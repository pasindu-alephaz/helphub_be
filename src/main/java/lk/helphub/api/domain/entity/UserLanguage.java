package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lk.helphub.api.domain.enums.LanguageProficiency;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_languages")
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
    private LanguageProficiency proficiency = LanguageProficiency.CONVERSATIONAL;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserLanguage() {}

    public UserLanguage(UUID id, User user, String languageCode, String languageName, LanguageProficiency proficiency, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.proficiency = proficiency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }

    public LanguageProficiency getProficiency() { return proficiency; }
    public void setProficiency(LanguageProficiency proficiency) { this.proficiency = proficiency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static UserLanguageBuilder builder() {
        return new UserLanguageBuilder();
    }

    public static class UserLanguageBuilder {
        private UUID id;
        private User user;
        private String languageCode;
        private String languageName;
        private LanguageProficiency proficiency = LanguageProficiency.CONVERSATIONAL;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserLanguageBuilder id(UUID id) { this.id = id; return this; }
        public UserLanguageBuilder user(User user) { this.user = user; return this; }
        public UserLanguageBuilder languageCode(String languageCode) { this.languageCode = languageCode; return this; }
        public UserLanguageBuilder languageName(String languageName) { this.languageName = languageName; return this; }
        public UserLanguageBuilder proficiency(LanguageProficiency proficiency) { this.proficiency = proficiency; return this; }
        public UserLanguageBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserLanguageBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserLanguage build() {
            return new UserLanguage(id, user, languageCode, languageName, proficiency, createdAt, updatedAt);
        }
    }
}
