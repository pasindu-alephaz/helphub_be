package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.JobCreateRequest;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.JobTemplateCreateRequest;
import lk.helphub.api.application.dto.JobTemplateResponse;
import lk.helphub.api.application.dto.JobUpdateRequest;
import lk.helphub.api.application.dto.JobTemplateUpdateRequest;
import lk.helphub.api.application.dto.JobFromTemplateRequest;
import lk.helphub.api.application.dto.ProviderCompleteRequest;
import lk.helphub.api.application.dto.DisputeJobRequest;
import lk.helphub.api.application.dto.CancelJobRequest;
import lk.helphub.api.application.dto.RejectJobRequest;
import lk.helphub.api.application.dto.ImageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.ParseException;

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
                .locationCoordinates(parseLocation(request.getLocationCoordinates()))
                .price(request.getPrice())
                .scheduledAt(request.getScheduledAt())
                .urgencyFlag(request.getUrgencyFlag())
                .jobType(request.getJobType())
                .preferredPrice(request.getPreferredPrice())
                .jobAvailabilityDuration(request.getJobAvailabilityDuration())
                .jobPlan(request.getJobPlan())
                .preferredLanguage(request.getPreferredLanguage())
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
    @Transactional(readOnly = true)
    public List<ImageResponse> getJobImages(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        return job.getImages().stream()
                .filter(image -> image.getDeletedAt() == null)
                .map(image -> ImageResponse.builder()
                        .id(image.getId())
                        .url(image.getUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteJobImage(UUID jobId, UUID imageId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to delete images for this job");
        }

        Image imageToDelete = job.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId + " for this job"));

        imageToDelete.setDeletedAt(LocalDateTime.now());
        imageRepository.save(imageToDelete);

        // Remove from the set as well
        job.getImages().remove(imageToDelete);
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
                .locationCoordinates(parseLocation(request.getLocationCoordinates()))
                .price(request.getPrice())
                .urgencyFlag(request.getUrgencyFlag())
                .jobType(request.getJobType())
                .preferredPrice(request.getPreferredPrice())
                .jobAvailabilityDuration(request.getJobAvailabilityDuration())
                .jobPlan(request.getJobPlan())
                .preferredLanguage(request.getPreferredLanguage())
                .user(user)
                .build();

        JobTemplate savedTemplate = jobTemplateRepository.save(template);
        return mapToJobTemplateResponse(savedTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobTemplateResponse> getMyTemplates(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        return jobTemplateRepository.findAllByUser(user).stream()
                .map(this::mapToJobTemplateResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobTemplateResponse getTemplateById(UUID templateId, String userEmail) {
        JobTemplate template = jobTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));
        
        if (!template.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to access this template");
        }
        
        return mapToJobTemplateResponse(template);
    }

    @Override
    public JobTemplateResponse updateTemplate(UUID templateId, String userEmail, JobTemplateUpdateRequest request) {
        JobTemplate template = jobTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

        if (!template.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to update this template");
        }

        if (request.getTemplateName() != null) template.setTemplateName(request.getTemplateName());
        if (request.getTitle() != null) template.setTitle(request.getTitle());
        if (request.getDescription() != null) template.setDescription(request.getDescription());
        if (request.getLocationAddress() != null) template.setLocationAddress(request.getLocationAddress());
        if (request.getLocationCoordinates() != null) template.setLocationCoordinates(parseLocation(request.getLocationCoordinates()));
        if (request.getPrice() != null) template.setPrice(request.getPrice());
        if (request.getUrgencyFlag() != null) template.setUrgencyFlag(request.getUrgencyFlag());
        if (request.getJobType() != null) template.setJobType(request.getJobType());
        if (request.getPreferredPrice() != null) template.setPreferredPrice(request.getPreferredPrice());
        if (request.getJobAvailabilityDuration() != null) template.setJobAvailabilityDuration(request.getJobAvailabilityDuration());
        if (request.getJobPlan() != null) template.setJobPlan(request.getJobPlan());
        if (request.getPreferredLanguage() != null) template.setPreferredLanguage(request.getPreferredLanguage());

        if (request.getSubcategoryId() != null) {
            ServiceCategory subcategory = serviceCategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubcategoryId()));
            template.setSubcategory(subcategory);
        }

        JobTemplate savedTemplate = jobTemplateRepository.save(template);
        return mapToJobTemplateResponse(savedTemplate);
    }

    @Override
    public void deleteTemplate(UUID templateId, String userEmail) {
        JobTemplate template = jobTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

        if (!template.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to delete this template");
        }

        jobTemplateRepository.delete(template);
    }

    @Override
    public JobResponse createJobFromTemplate(UUID templateId, String userEmail, JobFromTemplateRequest request) {
        JobTemplate template = jobTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

        if (!template.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to use this template");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Job job = Job.builder()
                .title(request.getTitle() != null ? request.getTitle() : template.getTitle())
                .description(request.getDescription() != null ? request.getDescription() : template.getDescription())
                .subcategory(template.getSubcategory())
                .locationAddress(template.getLocationAddress())
                .locationCoordinates(template.getLocationCoordinates())
                .price(request.getPrice() != null ? request.getPrice() : template.getPrice())
                .scheduledAt(request.getScheduledAt())
                .jobType(template.getJobType())
                .preferredPrice(template.getPreferredPrice())
                .jobAvailabilityDuration(template.getJobAvailabilityDuration())
                .jobPlan(template.getJobPlan())
                .preferredLanguage(template.getPreferredLanguage())
                .urgencyFlag(template.getUrgencyFlag())
                .postedBy(user)
                .status("OPEN")
                .build();

        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getJobs(Pageable pageable, UUID subcategoryId, String status, String urgencyFlag, BigDecimal minPrice, BigDecimal maxPrice, String locationCity, String jobType) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (subcategoryId != null) {
                predicates.add(cb.equal(root.get("subcategory").get("id"), subcategoryId));
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (urgencyFlag != null && !urgencyFlag.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("urgencyFlag"), urgencyFlag));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (locationCity != null && !locationCity.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("locationAddress")), "%" + locationCity.toLowerCase() + "%"));
            }
            if (jobType != null && !jobType.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable sanitizedPageable = sanitizePageable(pageable);
        return jobRepository.findAll(spec, sanitizedPageable).map(this::mapToJobResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        if (job.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        return mapToJobResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getNearbyJobs(String coordinates, double radiusKm, UUID subcategoryId) {
        Point point = parseLocation(coordinates);
        if (point == null) {
            throw new IllegalArgumentException("Coordinates must be provided");
        }
        double radiusInDegrees = radiusKm / 111.32;
        List<Job> nearbyJobs = jobRepository.findNearbyJobs(point, radiusInDegrees, subcategoryId);
        return nearbyJobs.stream().map(this::mapToJobResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getMyPostedJobs(String userEmail, Pageable pageable, String status) {
        Pageable sanitizedPageable = sanitizePageable(pageable);
        Page<Job> jobs;
        if (status != null && !status.trim().isEmpty()) {
            jobs = jobRepository.findByPostedByEmailAndStatusAndDeletedAtIsNull(userEmail, status, sanitizedPageable);
        } else {
            jobs = jobRepository.findByPostedByEmailAndDeletedAtIsNull(userEmail, sanitizedPageable);
        }
        return jobs.map(this::mapToJobResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getAcceptedJobs(String userEmail, Pageable pageable, String status) {
        Pageable sanitizedPageable = sanitizePageable(pageable);
        Page<Job> jobs;
        if (status != null && !status.trim().isEmpty()) {
            jobs = jobRepository.findByAcceptedByEmailAndStatusAndDeletedAtIsNull(userEmail, status, sanitizedPageable);
        } else {
            jobs = jobRepository.findByAcceptedByEmailAndDeletedAtIsNull(userEmail, sanitizedPageable);
        }
        return jobs.map(this::mapToJobResponse);
    }

    @Override
    public JobResponse updateJob(UUID jobId, String userEmail, JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        // Verify the user is the job poster
        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to update this job");
        }

        // Only allow updates when job is still OPEN
        if (!"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Cannot update job that is not in OPEN status");
        }

        // Update fields if provided
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType());
        }
        if (request.getPreferredPrice() != null) {
            job.setPreferredPrice(request.getPreferredPrice());
        }
        if (request.getJobAvailabilityDuration() != null) {
            job.setJobAvailabilityDuration(request.getJobAvailabilityDuration());
        }
        if (request.getJobPlan() != null) {
            job.setJobPlan(request.getJobPlan());
        }
        if (request.getPreferredLanguage() != null) {
            job.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getJobDate() != null || request.getJobTime() != null) {
            LocalDateTime scheduledAt = job.getScheduledAt();
            if (scheduledAt == null) {
                scheduledAt = LocalDateTime.now();
            }
            if (request.getJobDate() != null) {
                scheduledAt = scheduledAt.with(request.getJobDate());
            }
            if (request.getJobTime() != null) {
                scheduledAt = scheduledAt.with(request.getJobTime());
            }
            job.setScheduledAt(scheduledAt);
        }

        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public void deleteJob(UUID jobId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getDeletedAt() != null) {
            throw new RuntimeException("Job is already deleted");
        }

        // Verify the user is the job poster
        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("User is not authorized to delete this job");
        }

        // Soft delete - set deletedAt timestamp
        job.setDeletedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @Override
    public JobResponse acceptJob(UUID jobId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (!"OPEN".equals(job.getStatus())) {
            throw new RuntimeException("Job is not open for acceptance");
        }

        if (job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Poster cannot accept their own job");
        }

        job.setAcceptedBy(user);
        job.setStatus("IN_PROGRESS");
        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public JobResponse providerCompleteJob(UUID jobId, String userEmail, ProviderCompleteRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getAcceptedBy() == null || !job.getAcceptedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Only the accepted provider can mark the job as complete");
        }

        if (!"IN_PROGRESS".equals(job.getStatus())) {
            throw new RuntimeException("Job must be in progress to be marked as complete by provider");
        }

        job.setStatus("PENDING_CONFIRMATION");
        // remarks/images logic can be added here if persistence is required for them
        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public JobResponse completeJob(UUID jobId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Only the job poster can confirm job completion");
        }

        if (!"PENDING_CONFIRMATION".equals(job.getStatus()) && !"IN_PROGRESS".equals(job.getStatus())) {
            throw new RuntimeException("Job is not in a completed/confirmable state");
        }

        job.setStatus("COMPLETED");
        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public JobResponse disputeJob(UUID jobId, String userEmail, DisputeJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        boolean isPoster = job.getPostedBy().getEmail().equals(userEmail);
        boolean isProvider = job.getAcceptedBy() != null && job.getAcceptedBy().getEmail().equals(userEmail);

        if (!isPoster && !isProvider) {
            throw new RuntimeException("Only the poster or accepted provider can initiate a dispute");
        }

        job.setStatus("DISPUTED");
        // persistence for reason/evidence can be added here
        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public JobResponse cancelJob(UUID jobId, String userEmail, CancelJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            // If the provider wants to cancel, it should likely be 'rejectJob'.
            throw new RuntimeException("Only the job poster can cancel the job posting");
        }

        if ("COMPLETED".equals(job.getStatus()) || "CANCELLED".equals(job.getStatus())) {
            throw new RuntimeException("Cannot cancel a completed or already cancelled job");
        }

        job.setStatus("CANCELLED");
        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    @Override
    public JobResponse startJob(UUID jobId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getAcceptedBy() == null || !job.getAcceptedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Only the accepted provider can start the job");
        }

        if (!"IN_PROGRESS".equals(job.getStatus())) {
             throw new RuntimeException("Job must be in progress/accepted to be started");
        }

        // status remains IN_PROGRESS as per spec, but we confirm action
        return mapToJobResponse(job);
    }

    @Override
    public JobResponse rejectJob(UUID jobId, String userEmail, RejectJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getAcceptedBy() == null || !job.getAcceptedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Only the accepted provider can reject/abandon the job");
        }

        if (!"IN_PROGRESS".equals(job.getStatus())) {
            throw new RuntimeException("Cannot reject a job that is not in progress");
        }

        job.setAcceptedBy(null);
        job.setStatus("OPEN");
        Job savedJob = jobRepository.save(job);
        return mapToJobResponse(savedJob);
    }

    private JobResponse mapToJobResponse(Job job) {
        List<String> imageUrls = new ArrayList<>();
        List<ImageResponse> images = new ArrayList<>();
        if (job.getImages() != null) {
            images = job.getImages().stream()
                    .filter(image -> image.getDeletedAt() == null)
                    .map(image -> ImageResponse.builder()
                            .id(image.getId())
                            .url(image.getUrl())
                            .build())
                    .collect(Collectors.toList());
            imageUrls = images.stream().map(ImageResponse::getUrl).collect(Collectors.toList());
        }

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .subcategoryId(job.getSubcategory() != null ? job.getSubcategory().getId() : null)
                .locationAddress(job.getLocationAddress())
                .locationCoordinates(job.getLocationCoordinates() != null ? job.getLocationCoordinates().toText() : null)
                .price(job.getPrice())
                .scheduledAt(job.getScheduledAt())
                .jobType(job.getJobType())
                .preferredPrice(job.getPreferredPrice())
                .jobAvailabilityDuration(job.getJobAvailabilityDuration())
                .jobPlan(job.getJobPlan())
                .preferredLanguage(job.getPreferredLanguage())
                .urgencyFlag(job.getUrgencyFlag())
                .status(job.getStatus())
                .postedBy(job.getPostedBy() != null ? job.getPostedBy().getId() : null)
                .acceptedBy(job.getAcceptedBy() != null ? job.getAcceptedBy().getId() : null)
                .imageUrls(imageUrls)
                .images(images)
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
                .locationCoordinates(template.getLocationCoordinates() != null ? template.getLocationCoordinates().toText() : null)
                .price(template.getPrice())
                .urgencyFlag(template.getUrgencyFlag())
                .jobType(template.getJobType())
                .preferredPrice(template.getPreferredPrice())
                .jobAvailabilityDuration(template.getJobAvailabilityDuration())
                .jobPlan(template.getJobPlan())
                .preferredLanguage(template.getPreferredLanguage())
                .userId(template.getUser() != null ? template.getUser().getId() : null)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }

    private Point parseLocation(String coordinates) {
        if (coordinates == null || coordinates.trim().isEmpty()) return null;
        try {
            Point point = (Point) new WKTReader().read(coordinates);
            point.setSRID(4326);
            return point;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid coordinates format. Expected WKT format like POINT(lon lat)", e);
        }
    }

    private Pageable sanitizePageable(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        boolean hasInvalidSort = pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().contains("[") || order.getProperty().equals("string"));

        if (hasInvalidSort) {
            log.warn("Invalid sort property detected: {}. Falling back to default sort (createdAt DESC).", pageable.getSort());
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return pageable;
    }
}
