package lk.helphub.api.application.services;

import lk.helphub.api.domain.entity.Image;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.infrastructure.persistence.JpaImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private static final int MAX_DIMENSION = 800;
    private static final float JPEG_QUALITY = 0.75f;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final UserRepository userRepository;
    private final JpaImageRepository imageRepository;

    @Value("${app.upload.dir:./uploads/profile-pictures}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public String uploadProfilePicture(String email, MultipartFile file) throws IOException {
        User user = findUser(email);
        String imageUrl = uploadImage(user, file, "profile", "profile-pictures");

        // Update user's profile image URL
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);

        log.info("Profile picture uploaded for user {}: {}", user.getId(), imageUrl);
        return imageUrl;
    }

    @Transactional
    public String uploadImage(User user, MultipartFile file, String imageType, String subDir) throws IOException {
        validateFile(file);

        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir).getParent().resolve(subDir);
        Files.createDirectories(uploadPath);

        // Read and process image
        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            throw new IllegalArgumentException("Cannot read uploaded image. Ensure the file is a valid JPEG, PNG, or WebP image.");
        }

        BufferedImage resized = resizeImage(original);

        // Save compressed JPEG
        String filename = UUID.randomUUID() + ".jpg";
        File outputFile = uploadPath.resolve(filename).toFile();
        saveAsJpeg(resized, outputFile);

        String imageUrl = baseUrl + "/uploads/" + subDir + "/" + filename;

        // Persist image metadata
        Image imageRecord = Image.builder()
                .user(user)
                .url(imageUrl)
                .imageType(imageType)
                .fileSize(outputFile.length())
                .width(resized.getWidth())
                .height(resized.getHeight())
                .build();
        imageRepository.save(imageRecord);

        // Update user's profile image
        user.setProfilePicture(imageRecord);
        userRepository.save(user);

        log.info("Profile picture uploaded for user {}: {}", user.getId(), imageUrl);
        return imageUrl;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .or(() -> userRepository.findByPhoneNumber(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type. Only JPEG, PNG, and WebP are allowed.");
        }
        // 10MB max
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed limit of 10MB.");
        }
    }

    private BufferedImage resizeImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width <= MAX_DIMENSION && height <= MAX_DIMENSION) {
            return toRgbImage(original);
        }

        double scaleFactor = (double) MAX_DIMENSION / Math.max(width, height);
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resized;
    }

    /** Converts any image (e.g. with alpha) to plain RGB so JPEG can encode it. */
    private BufferedImage toRgbImage(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) {
            return src;
        }
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return rgb;
    }

    private void saveAsJpeg(BufferedImage image, File outputFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG ImageWriter found");
        }
        ImageWriter writer = writers.next();
        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(JPEG_QUALITY);

        try (FileImageOutputStream fios = new FileImageOutputStream(outputFile)) {
            writer.setOutput(fios);
            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }
    }
}
