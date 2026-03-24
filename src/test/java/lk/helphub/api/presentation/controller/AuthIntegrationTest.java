package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.PhoneInitRequest;
import lk.helphub.api.application.services.AuthService;
import lk.helphub.api.application.services.RefreshTokenService;
import lk.helphub.api.application.services.SocialAuthService;
import lk.helphub.api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import lk.helphub.api.infrastructure.security.SecurityConfig;
import lk.helphub.api.infrastructure.security.JwtAuthenticationFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private SocialAuthService socialAuthService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void shouldNotReturnForbiddenForPhoneInit() throws Exception {
        when(authService.sendPhoneOtp(any(PhoneInitRequest.class)))
                .thenReturn(AuthResponse.builder().phoneVerificationRequired(true).build());

        mockMvc.perform(post("/api/v1/auth/phone/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\": \"+94771234567\"}"))
                .andExpect(status().isOk());
    }
}
