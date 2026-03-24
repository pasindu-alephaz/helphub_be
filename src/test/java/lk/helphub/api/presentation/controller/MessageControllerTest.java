package lk.helphub.api.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.helphub.api.application.dto.MessageRequest;
import lk.helphub.api.application.dto.MessageResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.MessageService;
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

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "job_message")
    void testSendMessage() throws Exception {
        UUID jobId = UUID.randomUUID();
        MessageRequest request = MessageRequest.builder()
                .content("Hello")
                .suggestedPrice(new BigDecimal("4000.00"))
                .build();
        MessageResponse response = MessageResponse.builder().id(UUID.randomUUID()).content("Hello").suggestionStatus("PENDING").build();

        when(messageService.sendMessage(eq(jobId), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/{id}/messages", jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(() -> "user@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content").value("Hello"));
    }

    @Test
    @WithMockUser(authorities = "job_update")
    void testAcceptSuggestion() throws Exception {
        UUID jobId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        JobResponse response = JobResponse.builder().id(jobId).price(new BigDecimal("4000.00")).build();

        when(messageService.acceptSuggestion(eq(jobId), eq(messageId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/{id}/messages/{messageId}/accept", jobId, messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(() -> "poster@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.price").value(4000.00));
    }
}
