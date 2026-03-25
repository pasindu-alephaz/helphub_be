package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "service_categories")
public class ServiceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> description;

    @Column(length = 20)
    private String status = "active";

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ServiceCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ServiceCategory> subcategories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id")
    private Image icon;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ServiceCategory() {}

    public ServiceCategory(UUID id, Map<String, String> name, Map<String, String> description, String status, Integer displayOrder, ServiceCategory parent, List<ServiceCategory> subcategories, Image icon, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.displayOrder = displayOrder;
        this.parent = parent;
        this.subcategories = subcategories;
        this.icon = icon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Map<String, String> getName() { return name; }
    public void setName(Map<String, String> name) { this.name = name; }

    public Map<String, String> getDescription() { return description; }
    public void setDescription(Map<String, String> description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public ServiceCategory getParent() { return parent; }
    public void setParent(ServiceCategory parent) { this.parent = parent; }

    public List<ServiceCategory> getSubcategories() { return subcategories; }
    public void setSubcategories(List<ServiceCategory> subcategories) { this.subcategories = subcategories; }

    public Image getIcon() { return icon; }
    public void setIcon(Image icon) { this.icon = icon; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public static ServiceCategoryBuilder builder() {
        return new ServiceCategoryBuilder();
    }

    public static class ServiceCategoryBuilder {
        private UUID id;
        private Map<String, String> name;
        private Map<String, String> description;
        private String status = "active";
        private Integer displayOrder = 0;
        private ServiceCategory parent;
        private List<ServiceCategory> subcategories;
        private Image icon;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        public ServiceCategoryBuilder id(UUID id) { this.id = id; return this; }
        public ServiceCategoryBuilder name(Map<String, String> name) { this.name = name; return this; }
        public ServiceCategoryBuilder description(Map<String, String> description) { this.description = description; return this; }
        public ServiceCategoryBuilder status(String status) { this.status = status; return this; }
        public ServiceCategoryBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public ServiceCategoryBuilder parent(ServiceCategory parent) { this.parent = parent; return this; }
        public ServiceCategoryBuilder subcategories(List<ServiceCategory> subcategories) { this.subcategories = subcategories; return this; }
        public ServiceCategoryBuilder icon(Image icon) { this.icon = icon; return this; }
        public ServiceCategoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ServiceCategoryBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ServiceCategoryBuilder deletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; return this; }

        public ServiceCategory build() {
            return new ServiceCategory(id, name, description, status, displayOrder, parent, subcategories, icon, createdAt, updatedAt, deletedAt);
        }
    }
}
