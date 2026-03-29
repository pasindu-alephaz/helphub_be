package lk.helphub.api.application.services.impl;

import jakarta.transaction.Transactional;
import lk.helphub.api.application.dto.ProviderIdentityDocumentRequest;
import lk.helphub.api.application.dto.ProviderIdentityDocumentResponse;
import lk.helphub.api.application.services.ProviderDocumentService;
import lk.helphub.api.domain.entity.ProviderIdentityDocument;
import lk.helphub.api.domain.entity.ProviderIdentityImage;
import lk.helphub.api.domain.entity.ProviderProfile;
import lk.helphub.api.domain.repository.ProviderIdentityDocumentRepository;
import lk.helphub.api.domain.repository.ProviderProfileRepository;
import lk.helphub.api.application.services.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderDocumentServiceImpl implements ProviderDocumentService {

    private final ProviderIdentityDocumentRepository documentRepository;
    private final ProviderProfileRepository profileRepository;
    private final ImageUploadService storageService;

    @Override
    @Transactional
    public ProviderIdentityDocumentResponse addIdentityDocument(UUID providerId, ProviderIdentityDocumentRequest request, List<MultipartFile> images) {
        ProviderProfile profile = profileRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        ProviderIdentityDocument document = new ProviderIdentityDocument();
        document.setProviderProfile(profile);
        document.setDocumentType(request.getDocumentType());
        document.setIssuingCountry(request.getIssuingCountry());
        document.setDocumentCode(request.getDocumentCode());

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String url = storageService.uploadGenericImage(image, "document", "provider-documents");
                        ProviderIdentityImage docImage = new ProviderIdentityImage();
                        docImage.setIdentityDocument(document);
                        docImage.setFileUrl(url);
                        document.getImages().add(docImage);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload document image", e);
                    }
                }
            }
        }

        document = documentRepository.save(document);
        return mapToResponse(document);
    }

    @Override
    public List<ProviderIdentityDocumentResponse> getIdentityDocuments(UUID providerId) {
        return documentRepository.findByProviderProfileId(providerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIdentityDocument(UUID providerId, UUID documentId) {
        ProviderIdentityDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (!document.getProviderProfile().getId().equals(providerId)) {
            throw new IllegalArgumentException("Document does not belong to provider");
        }
        documentRepository.delete(document);
    }

    private ProviderIdentityDocumentResponse mapToResponse(ProviderIdentityDocument doc) {
        List<String> imageUrls = doc.getImages().stream()
                .map(ProviderIdentityImage::getFileUrl)
                .collect(Collectors.toList());

        return ProviderIdentityDocumentResponse.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .issuingCountry(doc.getIssuingCountry())
                .documentCode(doc.getDocumentCode())
                .imageUrls(imageUrls)
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
