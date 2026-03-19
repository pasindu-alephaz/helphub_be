package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.JobCreateRequest;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.JobTemplateCreateRequest;
import lk.helphub.api.application.dto.JobTemplateResponse;
import lk.helphub.api.application.dto.JobUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface JobService {
    
    JobResponse createJob(String userEmail, JobCreateRequest request);

    void uploadJobImages(UUID jobId, String userEmail, MultipartFile[] images);

    JobTemplateResponse createJobTemplate(String userEmail, JobTemplateCreateRequest request);

    Page<JobResponse> getJobs(Pageable pageable, UUID subcategoryId, String status, String urgencyFlag, BigDecimal minPrice, BigDecimal maxPrice, String locationCity, String jobType);

    JobResponse getJobById(UUID id);

    List<JobResponse> getNearbyJobs(String coordinates, double radiusKm, UUID subcategoryId);

    // User's own jobs management
    Page<JobResponse> getMyPostedJobs(String userEmail, Pageable pageable, String status);

    Page<JobResponse> getAcceptedJobs(String userEmail, Pageable pageable, String status);

    JobResponse updateJob(UUID jobId, String userEmail, JobUpdateRequest request);

    void deleteJob(UUID jobId, String userEmail);
}
