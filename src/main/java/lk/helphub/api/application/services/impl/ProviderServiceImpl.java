package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.ProviderService;
import lk.helphub.api.domain.entity.*;
import lk.helphub.api.domain.repository.*;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderIdentityDocumentRepository identityDocumentRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderPortfolioItemRepository portfolioItemRepository;
    private final UserAddressRepository addressRepository;
    private final UserEducationRepository educationRepository;
    private final ServiceCategoryRepository categoryRepository;

    public ProviderServiceImpl(
            @Qualifier("jpaUserRepository") UserRepository userRepository,
            ProviderProfileRepository providerProfileRepository,
            ProviderIdentityDocumentRepository identityDocumentRepository,
            ProviderServiceRepository providerServiceRepository,
            ProviderPortfolioItemRepository portfolioItemRepository,
            @Qualifier("jpaUserAddressRepository") UserAddressRepository addressRepository,
            @Qualifier("jpaUserEducationRepository") UserEducationRepository educationRepository,
            ServiceCategoryRepository categoryRepository
    ) {
        this.userRepository = userRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.identityDocumentRepository = identityDocumentRepository;
        this.providerServiceRepository = providerServiceRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.addressRepository = addressRepository;
        this.educationRepository = educationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProviderProfileResponse registerProvider(String email, ProviderRegistrationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Update basic user info
        user.setFullName(request.getFullName());
        user.setDisplayName(request.getDisplayName());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());
        user.setAbout(request.getAbout());
        user.setUserType("provider");
        userRepository.save(user);

        // Handle Addresses
        if (request.getAddresses() != null) {
            addressRepository.deleteByUser(user);
            List<UserAddress> addresses = request.getAddresses().stream()
                    .map(addrReq -> UserAddress.builder()
                            .user(user)
                            .label(addrReq.getLabel())
                            .streetAddress(addrReq.getStreetAddress())
                            .city(addrReq.getCity())
                            .province(addrReq.getProvince())
                            .district(addrReq.getDistrict())
                            .postalCode(addrReq.getZipCode())
                            .country(addrReq.getCountry())
                            .latitude(addrReq.getLatitude() != 0 ? BigDecimal.valueOf(addrReq.getLatitude()) : null)
                            .longitude(addrReq.getLongitude() != 0 ? BigDecimal.valueOf(addrReq.getLongitude()) : null)
                            .build())
                    .collect(Collectors.<UserAddress>toList());
            addressRepository.saveAll(addresses);
        }

        // Handle Academic Qualifications
        if (request.getQualifications() != null) {
            educationRepository.deleteByUser(user);
            List<UserEducation> educations = request.getQualifications().stream()
                    .map(qualReq -> UserEducation.builder()
                            .user(user)
                            .educationalLevel(qualReq.getEducationalLevel() != null ? qualReq.getEducationalLevel().name() : "OTHER")
                            .certificateName(qualReq.getCertificateName())
                            .university(qualReq.getUniversity())
                            .build())
                    .collect(Collectors.<UserEducation>toList());
            educationRepository.saveAll(educations);
        }

        // Provider Profile
        ProviderProfile profile = providerProfileRepository.findByUserId(user.getId())
                .orElse(ProviderProfile.builder().user(user).build());

        profile.setBusinessName(request.getDisplayName());
        profile.setBio(request.getAbout());
        
        // Handle Draft Status
        if (request.isDraft()) {
            profile.setVerificationStatus("DRAFT");
        } else if ("DRAFT".equals(profile.getVerificationStatus())) {
            profile.setVerificationStatus("PENDING");
        }
        
        providerProfileRepository.save(profile);

        // Identity Documents
        if (request.getIdentityDocuments() != null) {
            identityDocumentRepository.deleteByProviderProfile(profile);
            List<ProviderIdentityDocument> docs = request.getIdentityDocuments().stream()
                    .map(docReq -> {
                        ProviderIdentityDocument doc = ProviderIdentityDocument.builder()
                                .providerProfile(profile)
                                .idType(docReq.getDocumentType())
                                .issuingCountry(docReq.getIssuingCountry())
                                .issuingCountryCode(docReq.getIssuingCountryCode())
                                .idNumber("PENDING")
                                .status("PENDING")
                                .build();
                        
                        if (docReq.getImageUrls() != null) {
                            List<ProviderIdentityImage> images = docReq.getImageUrls().stream()
                                    .map(url -> ProviderIdentityImage.builder()
                                            .document(doc)
                                            .url(url)
                                            .build())
                                    .collect(Collectors.toList());
                            doc.setImages(images);
                        }
                        return doc;
                    })
                    .collect(Collectors.toList());
            identityDocumentRepository.saveAll(docs);
        }

        // Provider Portfolio Items
        if (request.getPortfolioItems() != null) {
            portfolioItemRepository.deleteByProviderProfile(profile);
            List<ProviderPortfolioItem> items = request.getPortfolioItems().stream()
                    .map(itemReq -> {
                        ProviderPortfolioItem item = ProviderPortfolioItem.builder()
                                .providerProfile(profile)
                                .title(itemReq.getTitle())
                                .description(itemReq.getDescription())
                                .build();
                        
                        if (itemReq.getImageUrls() != null) {
                            List<ProviderPortfolioImage> images = itemReq.getImageUrls().stream()
                                    .map(url -> ProviderPortfolioImage.builder()
                                            .portfolioItem(item)
                                            .url(url)
                                            .build())
                                    .collect(Collectors.toList());
                            item.setImages(images);
                        }
                        return item;
                    })
                    .collect(Collectors.toList());
            portfolioItemRepository.saveAll(items);
        }

        // Provider Services (Skills)
        if (request.getSkills() != null) {
            providerServiceRepository.deleteByProviderProfile(profile);
            List<lk.helphub.api.domain.entity.ProviderService> services = request.getSkills().stream()
                    .map(skillReq -> {
                        ServiceCategory category = categoryRepository.findById(skillReq.getCategoryId())
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + skillReq.getCategoryId()));
                        
                        ServiceCategory subcategory = null;
                        if (skillReq.getSubcategoryId() != null) {
                            subcategory = categoryRepository.findById(skillReq.getSubcategoryId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found: " + skillReq.getSubcategoryId()));
                        }

                        return lk.helphub.api.domain.entity.ProviderService.builder()
                                .providerProfile(profile)
                                .category(category)
                                .subcategory(subcategory)
                                .relationship(skillReq.getRelationship())
                                .skillLevel(skillReq.getSkillLevel() != null ? skillReq.getSkillLevel() : "INTERMEDIATE")
                                .build();
                    })
                    .collect(Collectors.<lk.helphub.api.domain.entity.ProviderService>toList());
            providerServiceRepository.saveAll(services);
        }

        return getProviderProfile(email);
    }

    @Override
    public ProviderProfileResponse getProviderProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        ProviderProfile profile = providerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found for user: " + email));

        List<ProviderIdentityDocument> identityDocs = identityDocumentRepository.findByProviderProfile(profile);
        List<lk.helphub.api.domain.entity.ProviderService> services = providerServiceRepository.findByProviderProfile(profile);
        List<ProviderPortfolioItem> portfolioItems = portfolioItemRepository.findByProviderProfile(profile);

        return ProviderProfileResponse.builder()
                .userId(user.getId())
                .businessName(profile.getBusinessName())
                .bio(profile.getBio())
                .identityDocuments(identityDocs.stream().map(this::mapToIdentityResponse).collect(Collectors.toList()))
                .services(services.stream().map(this::mapToServiceResponse).collect(Collectors.toList()))
                .portfolio(portfolioItems.stream().map(this::mapToPortfolioResponse).collect(Collectors.toList()))
                .build();
    }

    private ProviderIdentityResponse mapToIdentityResponse(ProviderIdentityDocument doc) {
        return ProviderIdentityResponse.builder()
                .id(doc.getId())
                .documentType(doc.getIdType())
                .issuingCountry(doc.getIssuingCountry())
                .issuingCountryCode(doc.getIssuingCountryCode())
                .status(doc.getStatus())
                .imageUrls(doc.getImages() != null ? doc.getImages().stream().map(ProviderIdentityImage::getUrl).collect(Collectors.toList()) : null)
                .build();
    }

    private ProviderServiceResponse mapToServiceResponse(lk.helphub.api.domain.entity.ProviderService service) {
        return ProviderServiceResponse.builder()
                .id(service.getId())
                .categoryId(service.getCategory().getId())
                .subcategoryId(service.getSubcategory() != null ? service.getSubcategory().getId() : null)
                .relationship(service.getRelationship())
                .build();
    }

    private ProviderPortfolioResponse mapToPortfolioResponse(ProviderPortfolioItem item) {
        return ProviderPortfolioResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrls(item.getImages() != null ? item.getImages().stream().map(ProviderPortfolioImage::getUrl).collect(Collectors.toList()) : null)
                .build();
    }
}
