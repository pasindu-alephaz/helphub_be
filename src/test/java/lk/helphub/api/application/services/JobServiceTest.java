package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.impl.JobServiceImpl;
import lk.helphub.api.domain.entity.Image;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.ServiceCategory;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    void testGetJobById_WithImages() {
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Job with Images");

        Image image1 = Image.builder().id(UUID.randomUUID()).url("/uploads/img1.jpg").build();
        Image image2 = Image.builder().id(UUID.randomUUID()).url("/uploads/img2.jpg").build();
        Set<Image> imageSet = new HashSet<>(List.of(image1, image2));
        job.setImages(imageSet);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobResponse result = jobService.getJobById(jobId);

        assertNotNull(result);
        assertEquals("Job with Images", result.getTitle());
        assertNotNull(result.getImageUrls());
        assertEquals(2, result.getImageUrls().size());
        assertTrue(result.getImageUrls().contains("/uploads/img1.jpg"));
        assertTrue(result.getImageUrls().contains("/uploads/img2.jpg"));
        assertNotNull(result.getImages());
        assertEquals(2, result.getImages().size());
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

    @Test
    void testAcceptJob_Success() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "provider@example.com";
        User provider = new User();
        provider.setEmail(userEmail);

        User poster = new User();
        poster.setEmail("poster@example.com");

        Job job = new Job();
        job.setId(jobId);
        job.setStatus("OPEN");
        job.setPostedBy(poster);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(provider));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse result = jobService.acceptJob(jobId, userEmail);

        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    void testAcceptJob_Failure_AlreadyAccepted() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "provider2@example.com";
        Job job = new Job();
        job.setId(jobId);
        job.setStatus("IN_PROGRESS");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> jobService.acceptJob(jobId, userEmail));
    }

    @Test
    void testProviderCompleteJob_Success() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "provider@example.com";
        User provider = new User();
        provider.setEmail(userEmail);

        Job job = new Job();
        job.setId(jobId);
        job.setStatus("IN_PROGRESS");
        job.setAcceptedBy(provider);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse result = jobService.providerCompleteJob(jobId, userEmail, null);

        assertEquals("PENDING_CONFIRMATION", result.getStatus());
    }

    @Test
    void testCompleteJob_Success() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "poster@example.com";
        User poster = new User();
        poster.setEmail(userEmail);

        Job job = new Job();
        job.setId(jobId);
        job.setStatus("PENDING_CONFIRMATION");
        job.setPostedBy(poster);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse result = jobService.completeJob(jobId, userEmail);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void testDisputeJob_Success() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "poster@example.com";
        User poster = new User();
        poster.setEmail(userEmail);

        Job job = new Job();
        job.setId(jobId);
        job.setStatus("IN_PROGRESS");
        job.setPostedBy(poster);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse result = jobService.disputeJob(jobId, userEmail, null);

        assertEquals("DISPUTED", result.getStatus());
    }

    @Test
    void testCancelJob_Success() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "poster@example.com";
        User poster = new User();
        poster.setEmail(userEmail);

        Job job = new Job();
        job.setId(jobId);
        job.setStatus("OPEN");
        job.setPostedBy(poster);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse result = jobService.cancelJob(jobId, userEmail, null);

        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void testRejectJob_Success() {
        UUID jobId = UUID.randomUUID();
        String userEmail = "provider@example.com";
        User provider = new User();
        provider.setEmail(userEmail);

        Job job = new Job();
        job.setId(jobId);
        job.setStatus("IN_PROGRESS");
        job.setAcceptedBy(provider);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse result = jobService.rejectJob(jobId, userEmail, null);

        assertEquals("OPEN", result.getStatus());
        assertNull(job.getAcceptedBy());
    }
}
