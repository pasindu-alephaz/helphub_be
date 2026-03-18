package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.impl.JobServiceImpl;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.ImageRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.JobTemplateRepository;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lk.helphub.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobTemplateRepository jobTemplateRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServiceCategoryRepository serviceCategoryRepository;
    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    void testGetJobs() {
        Pageable pageable = PageRequest.of(0, 10);
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setTitle("Test Job");
        Page<Job> jobPage = new PageImpl<>(List.of(job));

        when(jobRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(jobPage);

        Page<JobResponse> result = jobService.getJobs(pageable, null, "OPEN", null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Job", result.getContent().get(0).getTitle());
    }

    @Test
    void testGetJobById() {
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Specific Job");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobResponse result = jobService.getJobById(jobId);

        assertNotNull(result);
        assertEquals("Specific Job", result.getTitle());
    }

    @Test
    void testGetNearbyJobs() throws Exception {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setTitle("Nearby Job");

        when(jobRepository.findNearbyJobs(any(Point.class), anyDouble(), nullable(UUID.class)))
                .thenReturn(List.of(job));

        List<JobResponse> result = jobService.getNearbyJobs("POINT(79.0 6.0)", 10.0, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nearby Job", result.get(0).getTitle());
    }
}
