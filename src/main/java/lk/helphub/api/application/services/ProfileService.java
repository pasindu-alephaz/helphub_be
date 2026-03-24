package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.domain.entity.PhoneOtp;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserAddress;
import lk.helphub.api.domain.entity.UserLanguage;
import lk.helphub.api.domain.repository.PhoneOtpRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final PhoneOtpRepository phoneOtpRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        User user = findUserByEmailOrPhone(email);
        return mapToProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findUserByEmailOrPhone(email);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getIdentityType() != null) {
            user.setIdentityType(request.getIdentityType());
        }
        if (request.getIdentityValue() != null) {
            user.setIdentityValue(request.getIdentityValue());
        }
        if (request.getLanguagePreference() != null) {
            user.setLanguagePreference(request.getLanguagePreference());
        }

        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
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

        // Soft delete: record the reason and set deletedAt
        user.setDeleteReason(request.getDeleteReason());
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

        return ProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .identityType(user.getIdentityType())
                .identityValue(user.getIdentityValue())
                .languagePreference(user.getLanguagePreference())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .userType(user.getUserType())
                .status(user.getStatus())
                .addresses(addressResponses)
                .languages(languageResponses)
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

    private User findUserByEmailOrPhone(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
