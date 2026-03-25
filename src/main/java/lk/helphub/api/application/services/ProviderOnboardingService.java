package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.ProviderAvailabilityRequest;
import lk.helphub.api.application.dto.ProviderProfileResponse;
import lk.helphub.api.application.dto.ProviderServiceRequest;
import lk.helphub.api.domain.enums.IdentityType;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProviderOnboardingService {
    void submitIdentity(String userEmail, IdentityType idType, String idNumber, 
                        MultipartFile[] documentImages, MultipartFile selfieImage);
    void addCertificate(String userEmail, String name, LocalDate issuedDate, MultipartFile[] files);
    void setServices(String userEmail, List<ProviderServiceRequest> requests);
    void setAvailability(String userEmail, List<ProviderAvailabilityRequest> requests, boolean isAvailable);
    void addPortfolioItem(String userEmail, String title, String description, MultipartFile[] files);
    ProviderProfileResponse getProviderProfile(UUID providerId);
    ProviderProfileResponse getMyProviderProfile(String userEmail);
}
