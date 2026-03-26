package lk.helphub.api.admin.application.services.impl;

import jakarta.persistence.criteria.Predicate;
import lk.helphub.api.admin.application.services.AdminJobService;
import lk.helphub.api.admin.application.dto.CategoryStatsResponse;
import lk.helphub.api.application.dto.ImageResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.admin.application.dto.JobStatsResponse;
import lk.helphub.api.application.dto.JobUpdateRequest;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminJobServiceImpl implements AdminJobService {

    private final JobRepository jobRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getAllJobs(
            Pageable pageable,
            UUID userId,
            UUID providerId,
            String status,
            UUID subcategoryId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("postedBy").get("id"), userId));
            }
            if (providerId != null) {
                predicates.add(cb.equal(root.get("acceptedBy").get("id"), providerId));
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (subcategoryId != null) {
                predicates.add(cb.equal(root.get("subcategory").get("id"), subcategoryId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jobRepository.findAll(spec, pageable).map(this::mapToJobResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return mapToJobResponse(job);
    }

    @Override
    public JobResponse updateJob(UUID id, JobUpdateRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        // Admin override - update fields without status restriction
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

        return mapToJobResponse(jobRepository.save(job));
    }

    @Override
    public void deleteJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        // Soft delete
        job.setDeletedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    @Override
    public JobResponse updateJobStatus(UUID id, String status) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        // Force update status
        job.setStatus(status);
        return mapToJobResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getReportedJobs(Pageable pageable) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("flagged"), true));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return jobRepository.findAll(spec, pageable).map(this::mapToJobResponse);
    }

    @Override
    public JobResponse flagJob(UUID id, String reason) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        job.setFlagged(true);
        job.setFlagReason(reason);
        return mapToJobResponse(jobRepository.save(job));
    }

    @Override
    public JobResponse unflagJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        job.setFlagged(false);
        job.setFlagReason(null);
        return mapToJobResponse(jobRepository.save(job));
    }

    @Override
    public JobResponse archiveJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        job.setArchivedAt(LocalDateTime.now());
        return mapToJobResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public JobStatsResponse getJobStats(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime from = fromDate != null ? fromDate : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime to = toDate != null ? toDate : LocalDateTime.now().plusYears(100);

        return JobStatsResponse.builder()
                .totalJobs(jobRepository.countByDeletedAtIsNullAndCreatedAtBetween(from, to))
                .openJobs(jobRepository.countByStatusAndDeletedAtIsNullAndCreatedAtBetween("OPEN", from, to))
                .inProgressJobs(jobRepository.countByStatusAndDeletedAtIsNullAndCreatedAtBetween("IN_PROGRESS", from, to))
                .completedJobs(jobRepository.countByStatusAndDeletedAtIsNullAndCreatedAtBetween("COMPLETED", from, to))
                .cancelledJobs(jobRepository.countByStatusAndDeletedAtIsNullAndCreatedAtBetween("CANCELLED", from, to))
                .urgentJobs(jobRepository.countByUrgencyFlagAndDeletedAtIsNullAndCreatedAtBetween("Urgent", from, to))
                .averagePrice(jobRepository.findAveragePriceBetween(from, to))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryStatsResponse> getPopularCategories(int limit) {
        List<Object[]> results = jobRepository.findTopSubcategories(PageRequest.of(0, limit));

        return results.stream().map(result -> {
            UUID subcategoryId = (UUID) result[0];
            long count = (long) result[1];
            ServiceCategory category = serviceCategoryRepository.findById(subcategoryId).orElse(null);

            return CategoryStatsResponse.builder()
                    .subcategoryId(subcategoryId)
                    .name(category != null ? category.getName() : null)
                    .jobCount(count)
                    .build();
        }).collect(Collectors.toList());
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
                .locationCoordinates(job.getLocationCoordinates() != null
                        ? job.getLocationCoordinates().toText()
                        : null)
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
                .flagged(job.isFlagged())
                .flagReason(job.getFlagReason())
                .archivedAt(job.getArchivedAt())
                .build();
    }
}
