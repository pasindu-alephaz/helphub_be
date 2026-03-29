package lk.helphub.api.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.ProviderCoreService;
import lk.helphub.api.infrastructure.security.JwtUtil;
import lk.helphub.api.domain.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest(ProviderProfileController.class)
public class ProviderProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProviderCoreService providerService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void testGetAggregatedProfile() throws Exception {
        UUID providerId = UUID.randomUUID();
        ProviderProfileResponse response = ProviderProfileResponse.builder()
                .id(providerId)
                .professionalBio("Old Bio")
                .build();

        when(providerService.getProviderProfile(providerId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/providers/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(providerId.toString()));
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testUpdatePersonalDetails() throws Exception {
        UUID providerId = UUID.randomUUID();
        PersonalDetailsRequest request = PersonalDetailsRequest.builder()
                .fullName("New Name")
                .displayName("newname")
                .email("new@example.com")
                .phone("1234567890")
                .dob(LocalDate.now())
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(put("/api/v1/providers/{providerId}/personal-details", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(providerService).updatePersonalDetails(eq(providerId), any(PersonalDetailsRequest.class));
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testUploadProfilePicture() throws Exception {
        UUID providerId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("profilePicture", "test.jpg", "image/jpeg", "test image".getBytes());

        mockMvc.perform(multipart("/api/v1/providers/{providerId}/personal-details/profile-picture", providerId)
                .file(file)
                .with(csrf()))
                .andExpect(status().isCreated());

        verify(providerService).updateProfilePicture(eq(providerId), any());
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testUpdateAddressDetails() throws Exception {
        UUID providerId = UUID.randomUUID();
        AddressDetailsRequest request = AddressDetailsRequest.builder()
                .streetAddress("123 Street")
                .city("City")
                .province("Province")
                .zipCode("12345")
                .country("Country")
                .latitude(BigDecimal.valueOf(6.9271))
                .longitude(BigDecimal.valueOf(79.8612))
                .build();

        mockMvc.perform(put("/api/v1/providers/{providerId}/address-details", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(providerService).updateAddressDetails(eq(providerId), any(AddressDetailsRequest.class));
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testUpdateProfessionalBio() throws Exception {
        UUID providerId = UUID.randomUUID();
        ProfessionalBioRequest request = ProfessionalBioRequest.builder().bio("New Bio").build();

        mockMvc.perform(put("/api/v1/providers/{providerId}/professional-bio", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(providerService).updateProfessionalBio(eq(providerId), eq("New Bio"));
    }
    @Test
    @WithMockUser(authorities = "provider_access")
    void testGetNearbyProviders() throws Exception {
        String point = "6.9271,79.8612";
        double radius = 5000;
        when(providerService.findNearbyProviders(6.9271, 79.8612, radius)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/providers/nearby")
                .param("point", point)
                .param("radius", String.valueOf(radius)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testCreatePersonalDetails() throws Exception {
        UUID providerId = UUID.randomUUID();
        PersonalDetailsRequest request = PersonalDetailsRequest.builder()
                .fullName("Initial Name")
                .displayName("Initial")
                .email("initial@example.com")
                .phone("0712345678")
                .dob(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(post("/api/v1/providers/{providerId}/personal-details", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testCreateAddressDetails() throws Exception {
        UUID providerId = UUID.randomUUID();
        AddressDetailsRequest request = AddressDetailsRequest.builder()
                .streetAddress("123 Street")
                .city("City")
                .province("Province")
                .zipCode("12345")
                .country("Country")
                .latitude(BigDecimal.valueOf(6.9))
                .longitude(BigDecimal.valueOf(79.8))
                .build();

        mockMvc.perform(post("/api/v1/providers/{providerId}/address-details", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "provider_access")
    void testCreateProfessionalBio() throws Exception {
        UUID providerId = UUID.randomUUID();
        ProfessionalBioRequest request = ProfessionalBioRequest.builder().bio("New Bio").build();

        mockMvc.perform(post("/api/v1/providers/{providerId}/professional-bio", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
