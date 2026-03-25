package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String url;

    @Column(name = "image_type")
    private String imageType;

    @Column(name = "file_size")
    private Long fileSize;

    private Integer width;
    private Integer height;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Image() {}

    public Image(UUID id, User user, String url, String imageType, Long fileSize, Integer width, Integer height, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.url = url;
        this.imageType = imageType;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ImageBuilder builder() {
        return new ImageBuilder();
    }

    public static class ImageBuilder {
        private UUID id;
        private User user;
        private String url;
        private String imageType;
        private Long fileSize;
        private Integer width;
        private Integer height;
        private LocalDateTime createdAt;

        public ImageBuilder id(UUID id) { this.id = id; return this; }
        public ImageBuilder user(User user) { this.user = user; return this; }
        public ImageBuilder url(String url) { this.url = url; return this; }
        public ImageBuilder imageType(String imageType) { this.imageType = imageType; return this; }
        public ImageBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public ImageBuilder width(Integer width) { this.width = width; return this; }
        public ImageBuilder height(Integer height) { this.height = height; return this; }
        public ImageBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Image build() {
            return new Image(id, user, url, imageType, fileSize, width, height, createdAt);
        }
    }
}
