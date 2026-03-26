package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.dto.ProfileResponse;
import lk.helphub.api.application.dto.UpdateProfileRequest;
import lk.helphub.api.application.services.ProfileService;
import lk.helphub.api.infrastructure.security.JwtAuthenticationFilter;
import lk.helphub.api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.test.context.SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
public class ProfilePermissionsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(authorities = "profile_update")
    void testUpdateProfileWithProfileUpdateAuthority() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileResponse response = ProfileResponse.builder()
                .id(userId)
                .fullName("Test User")
                .build();

        when(profileService.updateProfile(any(), any(), any(), any(), any())).thenReturn(response);

        MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"fullName\": \"Test User\", \"displayName\": \"testuser\"}".getBytes());

        mockMvc.perform(multipart("/api/v1/profile")
                .file(requestJson)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER") // Lacks 'profile_read'
    void testGetProfileWithoutReadAuthorityShouldFail() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER") // This user LACKS 'profile_update' authority
    void testUpdateProfileWithoutProfileUpdateAuthorityShouldFail() throws Exception {
        MockMultipartFile requestJson = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"fullName\": \"Test User\"}".getBytes());

        mockMvc.perform(multipart("/api/v1/profile")
                .file(requestJson)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }
}
