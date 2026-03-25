package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.domain.entity.*;
import lk.helphub.api.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final PhoneOtpRepository phoneOtpRepository;
    private final ImageRepository imageRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final UserEducationRepository educationRepository;
    private final UserProfessionalDetailRepository professionalDetailRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        User user = findUserByEmailOrPhone(email);
        return mapToProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request,
            MultipartFile profilePicture,
            MultipartFile identityVerification,
            List<MultipartFile> educationCertificates) {
        User user = findUserByEmailOrPhone(email);

        // Conditional Validation for Seller
        boolean isSeller = user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("SELLER"))
                || (user.getUserType() != null && user.getUserType().equalsIgnoreCase("seller"));

        if (isSeller && identityVerification == null && user.getIdentityVerification() == null) {
            throw new IllegalArgumentException("Identity verification is mandatory for Sellers");
        }

        // Basic Info
        if (request.getFullName() != null)
            user.setFullName(request.getFullName());
        if (request.getDisplayName() != null)
            user.setDisplayName(request.getDisplayName());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null)
            user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAbout() != null)
            user.setAbout(request.getAbout());
        if (request.getBirthday() != null)
            user.setBirthday(request.getBirthday());
        if (request.getIdentityType() != null)
            user.setIdentityType(request.getIdentityType());
        if (request.getIdentityValue() != null)
            user.setIdentityValue(request.getIdentityValue());
        if (request.getLanguagePreference() != null)
            user.setLanguagePreference(request.getLanguagePreference());

        // Handle Files
        if (profilePicture != null && !profilePicture.isEmpty()) {
            user.setProfilePicture(saveImage(profilePicture, user, "PROFILE_IMAGE"));
        }
        if (identityVerification != null && !identityVerification.isEmpty()) {
            user.setIdentityVerification(saveImage(identityVerification, user, "IDENTITY_VERIFICATION"));
        }

        // Education
        if (request.getEducationList() != null) {
            user.getEducationList().clear();
            for (int i = 0; i < request.getEducationList().size(); i++) {
                EducationRequest eduReq = request.getEducationList().get(i);
                UserEducation education = UserEducation.builder()
                        .user(user)
                        .educationalLevel(eduReq.getEducationalLevel())
                        .build();

                if (educationCertificates != null && i < educationCertificates.size()) {
                    MultipartFile certFile = educationCertificates.get(i);
                    if (!certFile.isEmpty()) {
                        education.setCertificate(saveImage(certFile, user, "EDUCATION_CERTIFICATE"));
                    }
                }
                user.getEducationList().add(education);
            }
        }

        // Professional Details
        if (request.getProfessionalDetail() != null) {
            ProfessionalDetailRequest profReq = request.getProfessionalDetail();
            UserProfessionalDetail profDetail = professionalDetailRepository.findByUserId(user.getId())
                    .orElse(UserProfessionalDetail.builder().user(user).build());

            profDetail.setSkills(profReq.getSkills());
            profDetail.setExperience(profReq.getExperience());

            if (profReq.getCategoryIds() != null) {
                Set<ServiceCategory> categories = profReq.getCategoryIds().stream()
                        .map(id -> categoryRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id)))
                        .collect(Collectors.toSet());
                profDetail.setCategories(categories);
            }
            user.setProfessionalDetail(profDetail);
        }

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    private Image saveImage(MultipartFile file, User user, String type) {
        // Placeholder for actual file storage logic. Here we just create the entity.
        // In a real app, this would involve uploading to S3/Cloudinary and getting a
        // URL.
        String placeholderUrl = "https://storage.helphub.lk/" + type.toLowerCase() + "/" + UUID.randomUUID() + ".png";

        Image image = Image.builder()
                .user(user)
                .url(placeholderUrl)
                .imageType(type)
                .fileSize(file.getSize())
                .build();
        return imageRepository.save(image);
    }

    @Transactional
    public ProfileResponse setLanguagePreference(String email, LanguagePreferenceRequest request) {
        User user = findUserByEmailOrPhone(email);
        user.setLanguagePreference(request.getLanguagePreference());
        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    @Transactional
    public void deleteProfile(String email, DeleteProfileRequest request) {
        User user = findUserByEmailOrPhone(email);
        String phoneNumber = user.getPhoneNumber();

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("No phone number associated with this account for OTP verification");
        }

        // Validate the OTP
        PhoneOtp phoneOtp = phoneOtpRepository
                .findByPhoneNumberAndOtpAndUsedAtIsNull(phoneNumber, request.getOtp())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

        if (phoneOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        // Mark OTP as used
        phoneOtp.setUsedAt(LocalDateTime.now());
        phoneOtpRepository.save(phoneOtp);

        // Soft delete
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus("deleted");
        userRepository.save(user);

        log.info("User profile soft-deleted for phone: {}. Reason: {}", phoneNumber, request.getDeleteReason());
    }

    public ProfileResponse mapToProfileResponse(User user) {
        List<UserAddressResponse> addressResponses = (user.getAddresses() != null)
                ? user.getAddresses().stream().map(this::mapToAddressResponse).collect(Collectors.toList())
                : List.of();

        List<UserLanguageResponse> languageResponses = (user.getUserLanguages() != null)
                ? user.getUserLanguages().stream().map(this::mapToLanguageResponse).collect(Collectors.toList())
                : List.of();

        List<UserEducationResponse> educationResponses = (user.getEducationList() != null)
                ? user.getEducationList().stream().map(this::mapToEducationResponse).collect(Collectors.toList())
                : List.of();

        UserProfessionalDetailResponse profResponse = null;
        if (user.getProfessionalDetail() != null) {
            profResponse = mapToProfessionalDetailResponse(user.getProfessionalDetail());
        }

        return ProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthday(user.getBirthday())
                .identityType(user.getIdentityType())
                .identityValue(user.getIdentityValue())
                .languagePreference(user.getLanguagePreference())
                .about(user.getAbout())
                .profilePictureUrl(user.getProfilePicture() != null ? user.getProfilePicture().getUrl() : null)
                .identityVerificationUrl(
                        user.getIdentityVerification() != null ? user.getIdentityVerification().getUrl() : null)
                .userType(user.getUserType())
                .status(user.getStatus())
                .lastVerifiedAt(user.getLastVerifiedAt())
                .addresses(addressResponses)
                .languages(languageResponses)
                .educationList(educationResponses)
                .professionalDetail(profResponse)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private UserAddressResponse mapToAddressResponse(UserAddress a) {
        return UserAddressResponse.builder()
                .id(a.getId())
                .label(a.getLabel())
                .province(a.getProvince())
                .district(a.getDistrict())
                .city(a.getCity())
                .country(a.getCountry())
                .postalCode(a.getPostalCode())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .isDefault(a.isDefault())
                .build();
    }

    private UserLanguageResponse mapToLanguageResponse(UserLanguage l) {
        return UserLanguageResponse.builder()
                .id(l.getId())
                .languageCode(l.getLanguageCode())
                .languageName(l.getLanguageName())
                .proficiency(l.getProficiency())
                .build();
    }

    private UserEducationResponse mapToEducationResponse(UserEducation e) {
        return UserEducationResponse.builder()
                .id(e.getId())
                .educationalLevel(e.getEducationalLevel())
                .certificateUrl(e.getCertificate() != null ? e.getCertificate().getUrl() : null)
                .build();
    }

    private UserProfessionalDetailResponse mapToProfessionalDetailResponse(UserProfessionalDetail pd) {
        List<CategoryResponse> categoryResponses = pd.getCategories().stream()
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toList());

        return UserProfessionalDetailResponse.builder()
                .id(pd.getId())
                .skills(pd.getSkills())
                .experience(pd.getExperience())
                .categories(categoryResponses)
                .build();
    }

    private User findUserByEmailOrPhone(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
