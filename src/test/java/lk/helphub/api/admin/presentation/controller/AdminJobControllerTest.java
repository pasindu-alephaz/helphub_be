package lk.helphub.api.admin.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.helphub.api.admin.application.services.AdminJobService;
import lk.helphub.api.admin.application.dto.AdminJobStatusUpdateRequest;
import lk.helphub.api.admin.application.dto.CategoryStatsResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.admin.application.dto.JobStatsResponse;
import lk.helphub.api.application.dto.JobUpdateRequest;
import lk.helphub.api.admin.application.dto.FlagJobRequest;
import lk.helphub.api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminJobController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
public class AdminJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminJobService adminJobService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllJobs_returnsPage() throws Exception {
        Page<JobResponse> page = new PageImpl<>(List.of(JobResponse.builder().id(UUID.randomUUID()).title("Admin Job").build()));
        when(adminJobService.getAllJobs(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/jobs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Admin Job"));
    }

    @Test
    void testGetJobById_returnsJob() throws Exception {
        UUID id = UUID.randomUUID();
        when(adminJobService.getJobById(id)).thenReturn(JobResponse.builder().id(id).title("Detail Job").build());

        mockMvc.perform(get("/api/v1/admin/jobs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("Detail Job"));
    }

    @Test
    void testUpdateJob_returnsUpdatedJob() throws Exception {
        UUID id = UUID.randomUUID();
        JobUpdateRequest request = JobUpdateRequest.builder().jobType("FIXED").build();
        when(adminJobService.updateJob(eq(id), any())).thenReturn(JobResponse.builder().id(id).jobType("FIXED").build());

        mockMvc.perform(put("/api/v1/admin/jobs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.jobType").value("FIXED"));
    }

    @Test
    void testDeleteJob_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(adminJobService).deleteJob(id);

        mockMvc.perform(delete("/api/v1/admin/jobs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Job deleted successfully"));
    }

    @Test
    void testUpdateJobStatus_returnsJob() throws Exception {
        UUID id = UUID.randomUUID();
        AdminJobStatusUpdateRequest request = AdminJobStatusUpdateRequest.builder().status("COMPLETED").build();
        when(adminJobService.updateJobStatus(eq(id), eq("COMPLETED"))).thenReturn(JobResponse.builder().id(id).status("COMPLETED").build());

        mockMvc.perform(put("/api/v1/admin/jobs/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void testGetReportedJobs_returnsPage() throws Exception {
        Page<JobResponse> page = new PageImpl<>(List.of(JobResponse.builder().id(UUID.randomUUID()).flagged(true).build()));
        when(adminJobService.getReportedJobs(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/jobs/reports")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].flagged").value(true));
    }

    @Test
    void testFlagJob_returnsUpdatedJob() throws Exception {
        UUID id = UUID.randomUUID();
        FlagJobRequest request = FlagJobRequest.builder().reason("Spam").build();
        when(adminJobService.flagJob(eq(id), eq("Spam"))).thenReturn(JobResponse.builder().id(id).flagged(true).flagReason("Spam").build());

        mockMvc.perform(post("/api/v1/admin/jobs/{id}/flag", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.flagReason").value("Spam"));
    }

    @Test
    void testUnflagJob_returnsUpdatedJob() throws Exception {
        UUID id = UUID.randomUUID();
        when(adminJobService.unflagJob(id)).thenReturn(JobResponse.builder().id(id).flagged(false).build());

        mockMvc.perform(post("/api/v1/admin/jobs/{id}/unflag", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.flagged").value(false));
    }

    @Test
    void testArchiveJob_returnsUpdatedJob() throws Exception {
        UUID id = UUID.randomUUID();
        when(adminJobService.archiveJob(id)).thenReturn(JobResponse.builder().id(id).status("ARCHIVED").build());

        mockMvc.perform(post("/api/v1/admin/jobs/{id}/archive", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Job archived successfully"));
    }

    @Test
    void testGetJobStats_returnsStats() throws Exception {
        JobStatsResponse stats = JobStatsResponse.builder().totalJobs(10).build();
        when(adminJobService.getJobStats(any(), any())).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/jobs/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.totalJobs").value(10));
    }

    @Test
    void testGetPopularCategories_returnsList() throws Exception {
        List<CategoryStatsResponse> stats = List.of(CategoryStatsResponse.builder().jobCount(5).build());
        when(adminJobService.getPopularCategories(anyInt())).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/jobs/analytics/popular-categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data[0].jobCount").value(5));
    }
}
