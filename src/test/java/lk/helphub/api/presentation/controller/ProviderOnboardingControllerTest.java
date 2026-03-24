package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.services.ProviderOnboardingService;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProviderOnboardingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProviderOnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderOnboardingService onboardingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    private UsernamePasswordAuthenticationToken principal;

    @BeforeEach
    void setUp() {
        principal = new UsernamePasswordAuthenticationToken("test@helphub.lk", null);
    }

    @Test
    @WithMockUser(username = "test@helphub.lk")
    void testSubmitIdentity() throws Exception {
        MockMultipartFile doc1 = new MockMultipartFile("documentImages", "front.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile doc2 = new MockMultipartFile("documentImages", "back.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile selfieImage = new MockMultipartFile("selfieImage", "selfie.jpg", "image/jpeg", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/providers/onboarding/identity")
                .file(doc1)
                .file(doc2)
                .file(selfieImage)
                .param("idType", "NIC")
                .param("idNumber", "123456789V")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Identity documents submitted successfully"));
    }

    @Test
    @WithMockUser(username = "test@helphub.lk")
    void testAddCertificate() throws Exception {
        MockMultipartFile cert1 = new MockMultipartFile("files", "cert1.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile cert2 = new MockMultipartFile("files", "cert2.jpg", "image/jpeg", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/providers/onboarding/certificates")
                .file(cert1)
                .file(cert2)
                .param("name", "Expert Plumber")
                .param("issuedDate", "2023-01-01")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Certificate added successfully"));
    }

    @Test
    @WithMockUser(username = "test@helphub.lk")
    void testAddPortfolioItem() throws Exception {
        MockMultipartFile item1 = new MockMultipartFile("files", "item1.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile item2 = new MockMultipartFile("files", "item2.jpg", "image/jpeg", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/providers/onboarding/portfolio")
                .file(item1)
                .file(item2)
                .param("title", "My Project")
                .param("description", "A cool project")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Portfolio item added successfully"));
    }
}
