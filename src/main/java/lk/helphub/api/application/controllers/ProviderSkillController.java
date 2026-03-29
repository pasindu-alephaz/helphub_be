package lk.helphub.api.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.ProviderSkillProofRequest;
import lk.helphub.api.application.dto.ProviderSkillProofResponse;
import lk.helphub.api.application.dto.ProviderSkillRequest;
import lk.helphub.api.application.dto.ProviderSkillResponse;
import lk.helphub.api.application.services.ProviderSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers/{providerId}/skills")
@RequiredArgsConstructor
@Tag(name = "Provider Skills", description = "Endpoints for mapping providers to subcategories and managing skill proofs")
public class ProviderSkillController {

    private final ProviderSkillService skillService;

    // --- Skills Mapping ---

    @PostMapping
    @Operation(summary = "Assign Skill", description = "Assign a skill subcategory to the provider with an established skill level")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<ProviderSkillResponse> assignSkill(
            @PathVariable UUID providerId,
            @Valid @RequestBody ProviderSkillRequest request) {
        ProviderSkillResponse response = skillService.assignSkill(providerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get Assigned Skills", description = "Retrieve all mapped subcategories/skills for the provider")
    public ResponseEntity<List<ProviderSkillResponse>> getSkills(@PathVariable UUID providerId) {
        return ResponseEntity.ok(skillService.getSkills(providerId));
    }

    @DeleteMapping("/{skillId}")
    @Operation(summary = "Remove Assigned Skill")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<Void> removeSkill(@PathVariable UUID providerId, @PathVariable UUID skillId) {
        skillService.removeSkill(providerId, skillId);
        return ResponseEntity.noContent().build();
    }

    // --- Skill Proofs (Images) ---

    @PostMapping(value = "/proofs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Proof of Skill", description = "Upload image proof representing a skill or subcategory")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<ProviderSkillProofResponse> addSkillProof(
            @PathVariable UUID providerId,
            @Valid @ModelAttribute ProviderSkillProofRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        ProviderSkillProofResponse response = skillService.addSkillProof(providerId, request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/proofs")
    @Operation(summary = "Get Skill Proofs", description = "Retrieve all uploaded skill proofs for the provider")
    public ResponseEntity<List<ProviderSkillProofResponse>> getSkillProofs(@PathVariable UUID providerId) {
        return ResponseEntity.ok(skillService.getSkillProofs(providerId));
    }

    @DeleteMapping("/proofs/{proofId}")
    @Operation(summary = "Delete Skill Proof")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<Void> deleteSkillProof(@PathVariable UUID providerId, @PathVariable UUID proofId) {
        skillService.deleteSkillProof(providerId, proofId);
        return ResponseEntity.noContent().build();
    }
}
