package lk.helphub.api.admin.presentation.controller;

import lk.helphub.api.application.dto.NotificationResponse;
import lk.helphub.api.application.services.NotificationService;
import lk.helphub.api.application.services.SseNotificationService;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminNotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private SseNotificationService sseNotificationService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UsernamePasswordAuthenticationToken principal;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@example.com")
                .build();
        principal = new UsernamePasswordAuthenticationToken("admin@example.com", null);
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testGetNotifications() throws Exception {
        Page<NotificationResponse> page = new PageImpl<>(List.of(
            NotificationResponse.builder()
                .id(UUID.randomUUID())
                .title("Admin Notification")
                .message("Test Message")
                .build()
        ));
        when(notificationService.getNotifications(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/notifications")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Admin Notification"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testMarkAsRead() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/admin/notifications/mark-as-read/{id}", id)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Notification marked as read"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testMarkAllAsRead() throws Exception {
        mockMvc.perform(put("/api/v1/admin/notifications/mark-as-read")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("All notifications marked as read"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testDeleteNotification() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/admin/notifications/{id}", id)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Notification deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testStreamNotifications() throws Exception {
        when(sseNotificationService.subscribe(any())).thenReturn(new SseEmitter());

        mockMvc.perform(get("/api/v1/admin/notifications/stream")
                .principal(principal)
                .contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());
    }
}
