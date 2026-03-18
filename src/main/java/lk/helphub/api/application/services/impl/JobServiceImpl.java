package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.JobCreateRequest;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.JobTemplateCreateRequest;
import lk.helphub.api.application.dto.JobTemplateResponse;
import lk.helphub.api.application.services.JobService;
import lk.helphub.api.domain.entity.Image;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.JobTemplate;
import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.ImageRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.JobTemplateRepository;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobTemplateRepository jobTemplateRepository;
    private final UserRepository userRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ImageRepository imageRepository;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public JobResponse createJob(String userEmail, JobCreateRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        ServiceCategory subcategory = null;
        if (request.getSubcategoryId() != null) {
            subcategory = serviceCategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubcategoryId()));
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subcategory(subcategory)
                .locationAddress(request.getLocationAddress())
                .locationCoordinates(request.getLocationCoordinates())
                .price(request.getPrice())
                .scheduledAt(request.getScheduledAt())
                .urgencyFlag(request.getUrgencyFlag())
                .postedBy(user)
                .status("OPEN")
                .build();

        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public void uploadJobImages(UUID jobId, String userEmail, MultipartFile[] images) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to upload images for this job");
        }

        if (images == null || images.length == 0) {
            return;
        }

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }

        for (MultipartFile file : images) {
            if (file.isEmpty()) {
                continue;
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds 5MB limit.");
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new IllegalArgumentException("Only JPG and PNG images are supported.");
            }

            String fileName = StringUtils.cleanPath(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());

            try {
                if (fileName.contains("..")) {
                    throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
                }

                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                String fileDownloadUri = "/uploads/" + fileName;

                Image imageInfo = Image.builder()
                        .user(job.getPostedBy())
                        .url(fileDownloadUri)
                        .imageType("JOB_IMAGE")
                        .build();

                Image savedImage = imageRepository.save(imageInfo);
                job.getImages().add(savedImage);

            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
            }
        }

        jobRepository.save(job);
    }

    @Override
    public JobTemplateResponse createJobTemplate(String userEmail, JobTemplateCreateRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        ServiceCategory subcategory = null;
        if (request.getSubcategoryId() != null) {
            subcategory = serviceCategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubcategoryId()));
        }

        JobTemplate template = JobTemplate.builder()
                .templateName(request.getTemplateName())
                .title(request.getTitle())
                .description(request.getDescription())
                .subcategory(subcategory)
                .locationAddress(request.getLocationAddress())
                .locationCoordinates(request.getLocationCoordinates())
                .price(request.getPrice())
                .urgencyFlag(request.getUrgencyFlag())
                .user(user)
                .build();

        JobTemplate savedTemplate = jobTemplateRepository.save(template);
        return mapToJobTemplateResponse(savedTemplate);
    }

    private JobResponse mapToJobResponse(Job job) {
        List<String> imageUrls = new ArrayList<>();
        if (job.getImages() != null) {
            imageUrls = job.getImages().stream().map(Image::getUrl).collect(Collectors.toList());
        }

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .subcategoryId(job.getSubcategory() != null ? job.getSubcategory().getId() : null)
                .locationAddress(job.getLocationAddress())
                .locationCoordinates(job.getLocationCoordinates())
                .price(job.getPrice())
                .scheduledAt(job.getScheduledAt())
                .urgencyFlag(job.getUrgencyFlag())
                .status(job.getStatus())
                .postedBy(job.getPostedBy() != null ? job.getPostedBy().getId() : null)
                .acceptedBy(job.getAcceptedBy() != null ? job.getAcceptedBy().getId() : null)
                .imageUrls(imageUrls)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private JobTemplateResponse mapToJobTemplateResponse(JobTemplate template) {
        return JobTemplateResponse.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .title(template.getTitle())
                .description(template.getDescription())
                .subcategoryId(template.getSubcategory() != null ? template.getSubcategory().getId() : null)
                .locationAddress(template.getLocationAddress())
                .locationCoordinates(template.getLocationCoordinates())
                .price(template.getPrice())
                .urgencyFlag(template.getUrgencyFlag())
                .userId(template.getUser() != null ? template.getUser().getId() : null)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
