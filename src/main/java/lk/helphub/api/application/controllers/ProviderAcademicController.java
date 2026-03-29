package lk.helphub.api.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.UserEducationRequest;
import lk.helphub.api.application.dto.UserEducationResponse;
import lk.helphub.api.application.services.ProviderAcademicService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/academics")
@RequiredArgsConstructor
@Tag(name = "Academic Qualifications", description = "Endpoints for managing provider/user education details")
public class ProviderAcademicController {

    private final ProviderAcademicService academicService;

    @PostMapping
    @Operation(summary = "Add Academic Qualification")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<UserEducationResponse> addAcademicQualification(
            Principal principal,
            @Valid @RequestBody UserEducationRequest request) {
        UserEducationResponse response = academicService.addAcademicQualification(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{educationId}")
    @Operation(summary = "Update Academic Qualification")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<UserEducationResponse> updateAcademicQualification(
            Principal principal,
            @PathVariable UUID educationId,
            @Valid @RequestBody UserEducationRequest request) {
        return ResponseEntity.ok(academicService.updateAcademicQualification(principal.getName(), educationId, request));
    }

    @GetMapping
    @Operation(summary = "Get All Academic Qualifications")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<List<UserEducationResponse>> getAcademicQualifications(Principal principal) {
        return ResponseEntity.ok(academicService.getAcademicQualifications(principal.getName()));
    }

    @DeleteMapping("/{educationId}")
    @Operation(summary = "Delete Academic Qualification")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<Void> deleteAcademicQualification(
            Principal principal,
            @PathVariable UUID educationId) {
        academicService.deleteAcademicQualification(principal.getName(), educationId);
        return ResponseEntity.noContent().build();
    }
}
