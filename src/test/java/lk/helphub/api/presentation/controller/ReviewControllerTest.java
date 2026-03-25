package lk.helphub.api.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.helphub.api.application.dto.ReviewRequest;
import lk.helphub.api.application.dto.ReviewResponse;
import lk.helphub.api.application.services.ReviewService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "user@example.com")
    void submitProviderReview_Success() throws Exception {
        UUID jobId = UUID.randomUUID();
        ReviewRequest request = ReviewRequest.builder().rating(5).comment("Great").build();
        ReviewResponse response = ReviewResponse.builder().id(UUID.randomUUID()).rating(5).build();

        when(reviewService.submitProviderReview(eq(jobId), eq("user@example.com"), any(ReviewRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/{id}/reviews/provider", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "user@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Provider review submitted successfully"));
    }

    @Test
    @WithMockUser
    void submitProviderReview_Invalid_Rating() throws Exception {
        UUID jobId = UUID.randomUUID();
        ReviewRequest request = ReviewRequest.builder().rating(6).comment("Great").build();

        mockMvc.perform(post("/api/v1/jobs/{id}/reviews/provider", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "provider@example.com")
    void submitUserReview_Success() throws Exception {
        UUID jobId = UUID.randomUUID();
        ReviewRequest request = ReviewRequest.builder().rating(5).comment("Great").build();
        ReviewResponse response = ReviewResponse.builder().id(UUID.randomUUID()).rating(5).build();

        when(reviewService.submitUserReview(eq(jobId), eq("provider@example.com"), any(ReviewRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/{id}/reviews/user", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "provider@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("User review submitted successfully"));
    }

    @Test
    @WithMockUser
    void getJobReviews_Success() throws Exception {
        UUID jobId = UUID.randomUUID();
        ReviewResponse r1 = ReviewResponse.builder().id(UUID.randomUUID()).rating(5).build();
        when(reviewService.getJobReviews(jobId)).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/v1/jobs/{id}/reviews", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].rating").value(5));
    }

    @Test
    @WithMockUser
    void getProviderReviews_Success() throws Exception {
        UUID providerId = UUID.randomUUID();
        Page<ReviewResponse> page = new PageImpl<>(List.of(ReviewResponse.builder().id(UUID.randomUUID()).rating(5).build()));
        when(reviewService.getProviderReviews(eq(providerId), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/providers/{id}/reviews", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}
