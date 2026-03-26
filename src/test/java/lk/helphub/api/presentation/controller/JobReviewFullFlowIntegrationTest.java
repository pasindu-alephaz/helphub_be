package lk.helphub.api.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lk.helphub.api.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class JobReviewFullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    private User poster;
    private User provider;
    private ServiceCategory category;

    @BeforeEach
    void setUp() {
        // Clean up existing data if necessary (transactional should handle this, but for users we might need manual cleanup if they are shared)
        
        poster = userRepository.save(User.builder()
                .email("poster-flow@example.com")
                .fullName("Poster Flow")
                .phoneNumber("0771234561")
                .status("ACTIVE")
                .build());

        provider = userRepository.save(User.builder()
                .email("provider-flow@example.com")
                .fullName("Provider Flow")
                .phoneNumber("0771234562")
                .status("ACTIVE")
                .build());

        Map<String, String> categoryName = new HashMap<>();
        categoryName.put("en", "Test Category");
        category = serviceCategoryRepository.save(ServiceCategory.builder()
                .name(categoryName)
                .status("active")
                .build());
    }

    @Test
    void testFullJobLifecycleAndReviewFlow() throws Exception {
        // Step 1: User A creates a job
        JobCreateRequest createRequest = JobCreateRequest.builder()
                .title("Flow Job")
                .description("Testing full flow")
                .subcategoryId(category.getId())
                .price(new BigDecimal("1000"))
                .scheduledAt(LocalDateTime.now().plusDays(1))
                .locationAddress("123 Test St")
                .locationCoordinates("POINT(79.0 6.0)")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
                .principal(() -> "poster-flow@example.com"))
                .andExpect(status().isCreated())
                .andReturn();

        UUID jobId = UUID.fromString(objectMapper.readTree(createResult.getResponse().getContentAsString()).get("data").get("id").asText());

        // Step 2: User B submits a bid
        BidRequest bidRequest = BidRequest.builder()
                .amount(new BigDecimal("900"))
                .proposal("I can do it")
                .build();

        MvcResult bidResult = mockMvc.perform(post("/api/v1/jobs/{id}/bids", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidRequest))
                .principal(() -> "provider-flow@example.com"))
                .andExpect(status().isCreated())
                .andReturn();

        UUID bidId = UUID.fromString(objectMapper.readTree(bidResult.getResponse().getContentAsString()).get("data").get("id").asText());

        // Step 3: User A accepts User B's bid
        mockMvc.perform(post("/api/v1/jobs/{id}/bids/{bidId}/accept", jobId, bidId)
                .principal(() -> "poster-flow@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        // Step 4: User B marks job as complete
        mockMvc.perform(post("/api/v1/jobs/{id}/provider-complete", jobId)
                .principal(() -> "provider-flow@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING_CONFIRMATION"));

        // Step 5: User A confirms job completion
        mockMvc.perform(post("/api/v1/jobs/{id}/complete", jobId)
                .principal(() -> "poster-flow@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        // Step 6: User A reviews User B
        ReviewRequest posterReview = ReviewRequest.builder().rating(5).comment("Excellent provider").build();
        mockMvc.perform(post("/api/v1/jobs/{id}/reviews/provider", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(posterReview))
                .principal(() -> "poster-flow@example.com"))
                .andExpect(status().isCreated());

        // Step 7: User B reviews User A
        ReviewRequest providerReview = ReviewRequest.builder().rating(4).comment("Good client").build();
        mockMvc.perform(post("/api/v1/jobs/{id}/reviews/user", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(providerReview))
                .principal(() -> "provider-flow@example.com"))
                .andExpect(status().isCreated());

        // Step 8: Verify both reviews are visible
        mockMvc.perform(get("/api/v1/jobs/{id}/reviews", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
