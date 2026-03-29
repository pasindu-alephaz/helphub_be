package lk.helphub.api.application.services.impl;

import jakarta.transaction.Transactional;
import lk.helphub.api.application.dto.ProviderProfileRequest;
import lk.helphub.api.application.dto.ProviderProfileResponse;
import lk.helphub.api.application.services.ProviderAcademicService;
import lk.helphub.api.application.services.ProviderCoreService;
import lk.helphub.api.application.services.ProviderDocumentService;
import lk.helphub.api.application.services.ProviderSkillService;
import lk.helphub.api.domain.entity.ProviderProfile;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserAddress;
import lk.helphub.api.domain.repository.ProviderProfileRepository;
import lk.helphub.api.domain.repository.UserAddressRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.application.services.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lk.helphub.api.application.dto.*;

@Service
@RequiredArgsConstructor
public class ProviderCoreServiceImpl implements ProviderCoreService {

    private final ProviderProfileRepository providerProfileRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ImageUploadService storageService;
    private final ProviderDocumentService documentService;
    private final ProviderAcademicService academicService;
    private final ProviderSkillService skillService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public List<ProviderProfileResponse> findNearbyProviders(double latitude, double longitude, double radiusMeters) {
        return providerProfileRepository.findNearby(longitude, latitude, radiusMeters).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProviderProfileResponse> getAllProviders() {
        return providerProfileRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProvider(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        providerProfileRepository.deleteById(providerId);
    }

    @Override
    public PersonalDetailsRequest getPersonalDetails(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        return PersonalDetailsRequest.builder()
                .fullName(user.getFullName())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .dob(user.getBirthday())
                .gender(user.getGender())
                .build();
    }

    @Override
    @Transactional
    public void createPersonalDetails(UUID providerId, PersonalDetailsRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        
        // Basic check for initial entry (can be refined based on business rules)
        if (user.getFullName() != null) {
            throw new IllegalStateException("Personal details already exist for this provider");
        }
        
        user.setFullName(request.getFullName());
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setBirthday(request.getDob());
        user.setGender(request.getGender());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePersonalDetails(UUID providerId, PersonalDetailsRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        user.setFullName(request.getFullName());
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setBirthday(request.getDob());
        user.setGender(request.getGender());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void patchPersonalDetails(UUID providerId, PersonalDetailsRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhoneNumber(request.getPhone());
        if (request.getDob() != null) user.setBirthday(request.getDob());
        if (request.getGender() != null) user.setGender(request.getGender());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateProfilePicture(UUID providerId, MultipartFile file) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        if (file != null && !file.isEmpty()) {
            try {
                String imageUrl = storageService.uploadGenericImage(file, "profile", "provider-profiles");
                user.setProfileImageUrl(imageUrl);
                userRepository.save(user);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
    }

    @Override
    public AddressDetailsRequest getAddressDetails(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        UserAddress address = user.getAddresses().isEmpty() ? null : user.getAddresses().get(0);
        if (address == null) return null;
        return AddressDetailsRequest.builder()
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .province(address.getProvince())
                .zipCode(address.getPostalCode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    @Override
    @Transactional
    public void createAddressDetails(UUID providerId, AddressDetailsRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        if (!user.getAddresses().isEmpty()) {
            throw new IllegalStateException("Address details already exist for this provider");
        }
        UserAddress address = new UserAddress();
        address.setUser(user);
        updateAddressFields(address, request);
        userAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void updateAddressDetails(UUID providerId, AddressDetailsRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        UserAddress address = user.getAddresses().isEmpty() ? new UserAddress() : user.getAddresses().get(0);
        address.setUser(user);
        updateAddressFields(address, request);
        userAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void patchAddressDetails(UUID providerId, AddressDetailsRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        UserAddress address = user.getAddresses().isEmpty() ? new UserAddress() : user.getAddresses().get(0);
        address.setUser(user);
        patchAddressFields(address, request);
        userAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddressDetails(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User user = profile.getUser();
        if (!user.getAddresses().isEmpty()) {
            UserAddress address = user.getAddresses().get(0);
            user.getAddresses().remove(0);
            userAddressRepository.delete(address);
        }
    }

    @Override
    public String getProfessionalBio(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        return profile.getBio();
    }

    @Override
    @Transactional
    public void createProfessionalBio(UUID providerId, String bio) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        if (profile.getBio() != null && !profile.getBio().isBlank()) {
            throw new IllegalStateException("Professional bio already exists for this provider");
        }
        profile.setBio(bio);
        providerProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateProfessionalBio(UUID providerId, String bio) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        profile.setBio(bio);
        providerProfileRepository.save(profile);
    }

    private void updateAddressFields(UserAddress address, AddressDetailsRequest request) {
        address.setStreetAddress(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setPostalCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        
        Point location = geometryFactory.createPoint(new Coordinate(
                request.getLongitude().doubleValue(),
                request.getLatitude().doubleValue()
        ));
        location.setSRID(4326);
        address.setLocation(location);
        address.setDefault(true);
    }

    private void patchAddressFields(UserAddress address, AddressDetailsRequest request) {
        if (request.getStreetAddress() != null) address.setStreetAddress(request.getStreetAddress());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getProvince() != null) address.setProvince(request.getProvince());
        if (request.getZipCode() != null) address.setPostalCode(request.getZipCode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getLatitude() != null) address.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) address.setLongitude(request.getLongitude());

        if (request.getLatitude() != null || request.getLongitude() != null) {
            BigDecimal lat = request.getLatitude() != null ? request.getLatitude() : address.getLatitude();
            BigDecimal lon = request.getLongitude() != null ? request.getLongitude() : address.getLongitude();
            Point location = geometryFactory.createPoint(new Coordinate(lon.doubleValue(), lat.doubleValue()));
            location.setSRID(4326);
            address.setLocation(location);
        }
        address.setDefault(true);
    }

    @Override
    public ProviderProfileResponse getProviderProfile(UUID providerId) {
        ProviderProfile profile = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        return mapToResponse(profile);
    }

    @Override
    public ProviderProfileResponse getProviderProfileByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ProviderProfile profile = providerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found for user"));
        return mapToResponse(profile);
    }

    private ProviderProfileResponse mapToResponse(ProviderProfile profile) {
        User user = profile.getUser();
        UserAddress address = user.getAddresses().isEmpty() ? null : user.getAddresses().get(0);

        ProviderProfileResponse.PersonalDetailsResponse personalDetails = ProviderProfileResponse.PersonalDetailsResponse.builder()
                .fullName(user.getFullName())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .dob(user.getBirthday())
                .gender(user.getGender())
                .profilePictureUrl(user.getProfileImageUrl())
                .build();

        ProviderProfileResponse.AddressDetailsResponse addressDetails = null;
        if (address != null) {
            addressDetails = ProviderProfileResponse.AddressDetailsResponse.builder()
                    .streetAddress(address.getStreetAddress())
                    .city(address.getCity())
                    .province(address.getProvince())
                    .zipCode(address.getPostalCode())
                    .country(address.getCountry())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .build();
        }

        return ProviderProfileResponse.builder()
                .id(profile.getId())
                .personalDetails(personalDetails)
                .addressDetails(addressDetails)
                .professionalBio(profile.getBio())
                .identityDocuments(documentService.getIdentityDocuments(profile.getId()))
                .academicQualifications(academicService.getAcademicQualifications(user.getEmail()))
                .skills(skillService.getSkills(profile.getId()))
                .skillProofs(skillService.getSkillProofs(profile.getId()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
