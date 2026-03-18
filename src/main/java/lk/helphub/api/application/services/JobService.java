package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.JobCreateRequest;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.JobTemplateCreateRequest;
import lk.helphub.api.application.dto.JobTemplateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface JobService {
    
    JobResponse createJob(String userEmail, JobCreateRequest request);

    void uploadJobImages(UUID jobId, String userEmail, MultipartFile[] images);

    JobTemplateResponse createJobTemplate(String userEmail, JobTemplateCreateRequest request);
}
