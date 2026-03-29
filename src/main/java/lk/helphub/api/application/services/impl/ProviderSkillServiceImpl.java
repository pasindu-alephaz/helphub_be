package lk.helphub.api.application.services.impl;

import jakarta.transaction.Transactional;
import lk.helphub.api.application.dto.ProviderSkillProofRequest;
import lk.helphub.api.application.dto.ProviderSkillProofResponse;
import lk.helphub.api.application.dto.ProviderSkillRequest;
import lk.helphub.api.application.dto.ProviderSkillResponse;
import lk.helphub.api.application.services.ProviderSkillService;
import lk.helphub.api.domain.entity.ProviderProfile;
import lk.helphub.api.domain.entity.ProviderSkill;
import lk.helphub.api.domain.entity.ProviderSkillProof;
import lk.helphub.api.domain.entity.ProviderSkillProofImage;
import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.repository.ProviderProfileRepository;
import lk.helphub.api.domain.repository.ProviderSkillProofRepository;
import lk.helphub.api.domain.repository.ProviderSkillRepository;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lk.helphub.api.application.services.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderSkillServiceImpl implements ProviderSkillService {

    private final ProviderSkillRepository skillRepository;
    private final ProviderSkillProofRepository skillProofRepository;
    private final ProviderProfileRepository profileRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ImageUploadService storageService;

    @Override
    @Transactional
    public ProviderSkillResponse assignSkill(UUID providerId, ProviderSkillRequest request) {
        ProviderProfile profile = profileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        ServiceCategory subcategory = categoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        ProviderSkill skill = new ProviderSkill();
        skill.setProviderProfile(profile);
        skill.setSubcategory(subcategory);
        skill.setSkillLevel(request.getSkillLevel());

        skill = skillRepository.save(skill);
        return mapSkillToResponse(skill);
    }

    @Override
    public List<ProviderSkillResponse> getSkills(UUID providerId) {
        return skillRepository.findByProviderProfileId(providerId)
                .stream()
                .map(this::mapSkillToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeSkill(UUID providerId, UUID skillId) {
        ProviderSkill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        if (!skill.getProviderProfile().getId().equals(providerId)) {
            throw new IllegalArgumentException("Skill does not belong to provider");
        }
        skillRepository.delete(skill);
    }

    @Override
    @Transactional
    public ProviderSkillProofResponse addSkillProof(UUID providerId, ProviderSkillProofRequest request, List<MultipartFile> images) {
        ProviderProfile profile = profileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        ProviderSkillProof proof = new ProviderSkillProof();
        proof.setProviderProfile(profile);
        proof.setTitle(request.getTitle());
        proof.setDescription(request.getDescription());

        if (request.getSubcategoryId() != null) {
            ServiceCategory cat = categoryRepository.findById(request.getSubcategoryId())
                    .orElse(null);
            proof.setSubcategory(cat);
        }

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String url = storageService.uploadGenericImage(image, "skill-proof", "provider-proofs");
                        ProviderSkillProofImage proofImage = new ProviderSkillProofImage();
                        proofImage.setSkillProof(proof);
                        proofImage.setFileUrl(url);
                        proof.getImages().add(proofImage);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload skill proof image", e);
                    }
                }
            }
        }

        proof = skillProofRepository.save(proof);
        return mapProofToResponse(proof);
    }

    @Override
    public List<ProviderSkillProofResponse> getSkillProofs(UUID providerId) {
        return skillProofRepository.findByProviderProfileId(providerId)
                .stream()
                .map(this::mapProofToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSkillProof(UUID providerId, UUID proofId) {
        ProviderSkillProof proof = skillProofRepository.findById(proofId)
                .orElseThrow(() -> new IllegalArgumentException("Proof not found"));

        if (!proof.getProviderProfile().getId().equals(providerId)) {
            throw new IllegalArgumentException("Proof does not belong to provider");
        }
        skillProofRepository.delete(proof);
    }

    private ProviderSkillResponse mapSkillToResponse(ProviderSkill skill) {
        return ProviderSkillResponse.builder()
                .id(skill.getId())
                .subcategoryId(skill.getSubcategory().getId())
                .subcategoryName(skill.getSubcategory().getName())
                .skillLevel(skill.getSkillLevel())
                .createdAt(skill.getCreatedAt())
                .build();
    }

    private ProviderSkillProofResponse mapProofToResponse(ProviderSkillProof proof) {
        List<String> imageUrls = proof.getImages().stream()
                .map(ProviderSkillProofImage::getFileUrl)
                .collect(Collectors.toList());

        return ProviderSkillProofResponse.builder()
                .id(proof.getId())
                .subcategoryId(proof.getSubcategory() != null ? proof.getSubcategory().getId() : null)
                .title(proof.getTitle())
                .description(proof.getDescription())
                .imageUrls(imageUrls)
                .createdAt(proof.getCreatedAt())
                .build();
    }
}
