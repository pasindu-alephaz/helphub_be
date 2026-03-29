package lk.helphub.api.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.ProviderIdentityDocumentRequest;
import lk.helphub.api.application.dto.ProviderIdentityDocumentResponse;
import lk.helphub.api.application.services.ProviderDocumentService;
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
@RequestMapping("/api/v1/providers/{providerId}/documents")
@RequiredArgsConstructor
@Tag(name = "Provider Identity Documents", description = "Endpoints for managing provider identification documents")
public class ProviderDocumentController {

    private final ProviderDocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add Identity Document", description = "Upload a new identity document with scanned images")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<ProviderIdentityDocumentResponse> addIdentityDocument(
            @PathVariable UUID providerId,
            @Parameter(description = "Document details") @Valid @ModelAttribute ProviderIdentityDocumentRequest request,
            @Parameter(description = "Scanned images of the document") @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ProviderIdentityDocumentResponse response = documentService.addIdentityDocument(providerId, request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get All Identity Documents", description = "Retrieve all identity documents for a provider")
    @PreAuthorize("hasAuthority('provider_access')")
    public ResponseEntity<List<ProviderIdentityDocumentResponse>> getIdentityDocuments(@PathVariable UUID providerId) {
        return ResponseEntity.ok(documentService.getIdentityDocuments(providerId));
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete Identity Document", description = "Remove an identity document and its images")
    @PreAuthorize("hasAuthority('provider_registration')")
    public ResponseEntity<Void> deleteIdentityDocument(
            @PathVariable UUID providerId,
            @PathVariable UUID documentId) {
        documentService.deleteIdentityDocument(providerId, documentId);
        return ResponseEntity.noContent().build();
    }
}
