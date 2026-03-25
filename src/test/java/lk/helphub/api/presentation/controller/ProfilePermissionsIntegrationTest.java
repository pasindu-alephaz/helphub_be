package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.dto.ProfileResponse;
import lk.helphub.api.application.dto.UpdateProfileRequest;
import lk.helphub.api.application.services.ProfileService;
import lk.helphub.api.infrastructure.security.JwtAuthenticationFilter;
import lk.helphub.api.infrastructure.security.JwtUtil;
import lk.helphub.api.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@Import(SecurityConfig.class)
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class ProfilePermissionsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                .andExpect(status().isForbidden());
    }
}
