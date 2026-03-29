package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.ProviderSkillProofRequest;
import lk.helphub.api.application.dto.ProviderSkillProofResponse;
import lk.helphub.api.application.dto.ProviderSkillRequest;
import lk.helphub.api.application.dto.ProviderSkillResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProviderSkillService {
    ProviderSkillResponse assignSkill(UUID providerId, ProviderSkillRequest request);
    List<ProviderSkillResponse> getSkills(UUID providerId);
    void removeSkill(UUID providerId, UUID skillId);

    ProviderSkillProofResponse addSkillProof(UUID providerId, ProviderSkillProofRequest request, List<MultipartFile> images);
    List<ProviderSkillProofResponse> getSkillProofs(UUID providerId);
    void deleteSkillProof(UUID providerId, UUID proofId);
}
