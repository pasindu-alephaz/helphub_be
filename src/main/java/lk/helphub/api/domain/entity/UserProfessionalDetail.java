package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_professional_details")
public class UserProfessionalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_professional_categories",
            joinColumns = @JoinColumn(name = "professional_detail_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<ServiceCategory> categories = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserProfessionalDetail() {}

    public UserProfessionalDetail(UUID id, User user, String skills, String experience, Set<ServiceCategory> categories, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.skills = skills;
        this.experience = experience;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public Set<ServiceCategory> getCategories() {
        if (categories == null) categories = new HashSet<>();
        return categories;
    }
    public void setCategories(Set<ServiceCategory> categories) { this.categories = categories; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static UserProfessionalDetailBuilder builder() {
        return new UserProfessionalDetailBuilder();
    }

    public static class UserProfessionalDetailBuilder {
        private UUID id;
        private User user;
        private String skills;
        private String experience;
        private Set<ServiceCategory> categories = new HashSet<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserProfessionalDetailBuilder id(UUID id) { this.id = id; return this; }
        public UserProfessionalDetailBuilder user(User user) { this.user = user; return this; }
        public UserProfessionalDetailBuilder skills(String skills) { this.skills = skills; return this; }
        public UserProfessionalDetailBuilder experience(String experience) { this.experience = experience; return this; }
        public UserProfessionalDetailBuilder categories(Set<ServiceCategory> categories) { this.categories = categories; return this; }
        public UserProfessionalDetailBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserProfessionalDetailBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserProfessionalDetail build() {
            return new UserProfessionalDetail(id, user, skills, experience, categories, createdAt, updatedAt);
        }
    }
}
