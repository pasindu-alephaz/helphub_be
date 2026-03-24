package lk.helphub.api.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.helphub.api.application.dto.BidRequest;
import lk.helphub.api.application.dto.BidResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.BidService;
import lk.helphub.api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BidController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BidService bidService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "job_bid")
    void testSubmitBid() throws Exception {
        UUID jobId = UUID.randomUUID();
        BidRequest request = BidRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .proposal("Test Proposal")
                .build();
        BidResponse response = BidResponse.builder().id(UUID.randomUUID()).amount(new BigDecimal("5000.00")).build();

        when(bidService.submitBid(eq(jobId), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/{id}/bids", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "provider@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.amount").value(5000.00));
    }

    @Test
    @WithMockUser(authorities = "job_read")
    void testGetJobBids() throws Exception {
        UUID jobId = UUID.randomUUID();
        List<BidResponse> bids = List.of(BidResponse.builder().id(UUID.randomUUID()).amount(new BigDecimal("4500.00")).build());

        when(bidService.getJobBids(eq(jobId), any())).thenReturn(bids);

        mockMvc.perform(get("/api/v1/jobs/{id}/bids", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(() -> "poster@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data[0].amount").value(4500.00));
    }

    @Test
    @WithMockUser(authorities = "job_accept")
    void testAcceptBid() throws Exception {
        UUID jobId = UUID.randomUUID();
        UUID bidId = UUID.randomUUID();
        JobResponse response = JobResponse.builder().id(jobId).status("IN_PROGRESS").build();

        when(bidService.acceptBid(eq(jobId), eq(bidId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/{id}/bids/{bidId}/accept", jobId, bidId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(() -> "poster@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }
}
