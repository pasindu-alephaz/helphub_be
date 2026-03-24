package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.ProviderOnboardingService;
import lk.helphub.api.application.services.ImageUploadService;
import lk.helphub.api.domain.entity.*;
import lk.helphub.api.domain.enums.VerificationStatus;
import lk.helphub.api.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderOnboardingServiceImpl implements ProviderOnboardingService {

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderIdentityDocumentRepository identityDocumentRepository;
    private final ProviderCertificateRepository certificateRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderAvailabilityRepository availabilityRepository;
    private final ProviderPortfolioItemRepository portfolioItemRepository;
    private final UserRepository userRepository;
    private final ServiceCategoryRepository categoryRepository;

    private final ImageUploadService imageUploadService;

    @Override
    @Transactional
    public void submitIdentity(String userEmail, lk.helphub.api.domain.enums.IdentityType idType, String idNumber, 
                                MultipartFile[] documentImages, MultipartFile selfieImage) {
        ProviderProfile profile = getOrCreateProfile(userEmail);
        User user = profile.getUser();
        
        try {
            String selfieUrl = imageUploadService.uploadImage(user, selfieImage, "selfie", "verification");

            ProviderIdentityDocument doc = identityDocumentRepository.findByProviderProfileId(profile.getId())
                    .orElse(ProviderIdentityDocument.builder()
                            .providerProfile(profile)
                            .build());
            
            doc.setIdType(idType);
            doc.setIdNumber(idNumber);
            doc.setSelfieImageUrl(selfieUrl);
            doc.setStatus(VerificationStatus.PENDING);
            
            // Clear existing images and add new ones (Simplified logic)
            doc.getImages().clear();
            identityDocumentRepository.save(doc); // Save first to get ID if new

            for (int i = 0; i < documentImages.length; i++) {
                String url = imageUploadService.uploadImage(user, documentImages[i], "id_doc_" + i, "verification");
                doc.getImages().add(ProviderIdentityImage.builder()
                        .document(doc)
                        .url(url)
                        .build());
            }
            
            identityDocumentRepository.save(doc);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to upload identity images", e);
        }
    }

    @Override
    @Transactional
    public void addCertificate(String userEmail, String name, java.time.LocalDate issuedDate, MultipartFile[] files) {
        ProviderProfile profile = getOrCreateProfile(userEmail);
        User user = profile.getUser();
        
        try {
            ProviderCertificate certificate = ProviderCertificate.builder()
                    .providerProfile(profile)
                    .name(name)
                    .issuedDate(issuedDate)
                    .build();
            
            // Save to get ID
            certificate = certificateRepository.save(certificate);

            for (int i = 0; i < files.length; i++) {
                String url = imageUploadService.uploadImage(user, files[i], "cert_" + i, "certificates");
                certificate.getImages().add(ProviderCertificateImage.builder()
                        .certificate(certificate)
                        .url(url)
                        .build());
            }
            
            certificateRepository.save(certificate);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to upload certificates", e);
        }
    }

    @Override
    @Transactional
    public void setServices(String userEmail, List<ProviderServiceRequest> requests) {
        ProviderProfile profile = getOrCreateProfile(userEmail);
        
        List<ProviderService> existing = providerServiceRepository.findByProviderProfileId(profile.getId());
        providerServiceRepository.deleteAll(existing);
        
        List<ProviderService> newServices = requests.stream().map(req -> {
            ServiceCategory category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + req.getCategoryId()));
            
            ServiceCategory subcategory = null;
            if (req.getSubCategoryId() != null) {
                subcategory = categoryRepository.findById(req.getSubCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid subcategory ID: " + req.getSubCategoryId()));
            }

            return ProviderService.builder()
                    .providerProfile(profile)
                    .serviceCategory(category)
                    .subcategory(subcategory)
                    .skillLevel(req.getSkillLevel())
                    .isAvailable(req.isAvailable())
                    .startDateTime(req.getStartDateTime())
                    .endDateTime(req.getEndDateTime())
                    .build();
        }).collect(Collectors.toList());
        
        providerServiceRepository.saveAll(newServices);
    }

    @Override
    @Transactional
    public void setAvailability(String userEmail, List<ProviderAvailabilityRequest> requests, boolean isAvailable) {
        ProviderProfile profile = getOrCreateProfile(userEmail);
        
        profile.setAvailable(isAvailable);
        providerProfileRepository.save(profile);

        List<ProviderAvailability> existing = availabilityRepository.findByProviderProfileId(profile.getId());
        availabilityRepository.deleteAll(existing);
        
        List<ProviderAvailability> newAvailabilities = requests.stream().map(req -> 
            ProviderAvailability.builder()
                    .providerProfile(profile)
                    .dayOfWeek(req.getDayOfWeek())
                    .startTime(req.getStartTime())
                    .endTime(req.getEndTime())
                    .build()
        ).collect(Collectors.toList());
        
        availabilityRepository.saveAll(newAvailabilities);
    }

    @Override
    @Transactional
    public void addPortfolioItem(String userEmail, String title, String description, MultipartFile[] files) {
        ProviderProfile profile = getOrCreateProfile(userEmail);
        User user = profile.getUser();
        
        try {
            ProviderPortfolioItem item = ProviderPortfolioItem.builder()
                    .providerProfile(profile)
                    .title(title)
                    .description(description)
                    .build();
            
            // Save to get ID
            item = portfolioItemRepository.save(item);

            for (int i = 0; i < files.length; i++) {
                String url = imageUploadService.uploadImage(user, files[i], "item_" + i, "portfolio");
                item.getImages().add(ProviderPortfolioImage.builder()
                        .portfolioItem(item)
                        .url(url)
                        .build());
            }
            
            portfolioItemRepository.save(item);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to upload portfolio images", e);
        }
    }

    @Override
    public ProviderProfileResponse getProviderProfile(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        return mapToResponse(profile);
    }

    @Override
    public ProviderProfileResponse getMyProviderProfile(String userEmail) {
        User user = findUser(userEmail);
        ProviderProfile profile = providerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Provider profile not found for user"));
        return mapToResponse(profile);
    }

    private ProviderProfile getOrCreateProfile(String userEmail) {
        User user = findUser(userEmail);
        return providerProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ProviderProfile newProfile = ProviderProfile.builder()
                            .user(user)
                            .verificationStatus(VerificationStatus.PENDING)
                            .build();
                    // Update user type to provider
                    user.setUserType("provider");
                    userRepository.save(user);
                    return providerProfileRepository.save(newProfile);
                });
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .or(() -> userRepository.findByPhoneNumber(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ProviderProfileResponse mapToResponse(ProviderProfile profile) {
        User user = profile.getUser();
        List<String> categories = profile.getServices().stream()
                .map(ps -> ps.getServiceCategory().getName().toString())
                .collect(Collectors.toList());
        
        List<ProviderPortfolioRequest> portfolio = profile.getPortfolioItems().stream()
                .map(item -> ProviderPortfolioRequest.builder()
                        .imageUrls(item.getImages().stream().map(ProviderPortfolioImage::getUrl).collect(Collectors.toList()))
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .build())
                .collect(Collectors.toList());

        ProviderIdentityDocument idDoc = identityDocumentRepository.findByProviderProfileId(profile.getId()).orElse(null);
        List<String> identityImages = idDoc != null ? 
                idDoc.getImages().stream().map(ProviderIdentityImage::getUrl).collect(Collectors.toList()) : List.of();

        return ProviderProfileResponse.builder()
                .id(profile.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .businessName(profile.getBusinessName())
                .profileImageUrl(user.getProfileImageUrl())
                .verificationStatus(profile.getVerificationStatus())
                .isVerifiedBadge(profile.isVerifiedBadge())
                .isAvailable(profile.isAvailable())
                .averageRating(profile.getAverageRating())
                .reviewCount(profile.getReviewCount())
                .serviceCategories(categories)
                .identityDocumentImageUrls(identityImages)
                .portfolio(portfolio)
                .build();
    }
}
