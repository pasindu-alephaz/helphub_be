package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProviderCoreService {
    List<ProviderProfileResponse> getAllProviders();
    ProviderProfileResponse getProviderProfile(UUID providerId);
    ProviderProfileResponse getProviderProfileByUsername(String username);
    void deleteProvider(UUID providerId);

    // Personal Details
    PersonalDetailsRequest getPersonalDetails(UUID providerId);
    void createPersonalDetails(UUID providerId, PersonalDetailsRequest request);
    void updatePersonalDetails(UUID providerId, PersonalDetailsRequest request);
    void patchPersonalDetails(UUID providerId, PersonalDetailsRequest request);
    void updateProfilePicture(UUID providerId, MultipartFile file);

    // Address Details
    AddressDetailsRequest getAddressDetails(UUID providerId);
    void createAddressDetails(UUID providerId, AddressDetailsRequest request);
    void updateAddressDetails(UUID providerId, AddressDetailsRequest request);
    void patchAddressDetails(UUID providerId, AddressDetailsRequest request);
    void deleteAddressDetails(UUID providerId);

    // Professional Bio
    String getProfessionalBio(UUID providerId);
    void createProfessionalBio(UUID providerId, String bio);
    void updateProfessionalBio(UUID providerId, String bio);

    // Search
    List<ProviderProfileResponse> findNearbyProviders(double latitude, double longitude, double radiusMeters);
}
